package com.StrattonApp.Backend.comparador;

import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.StrattonApp.Backend.exceptions.GlobalException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Lee una factura de luz (PDF o foto) con Claude y devuelve sus datos de consumo y precios.
 * Se fuerza la respuesta con una herramienta (tool use) cuyo esquema es {@link DatosFactura},
 * así el modelo siempre devuelve JSON estructurado. Solo se piden datos de consumo y precios:
 * ni nombre, ni NIF, ni dirección, ni IBAN. El archivo no se guarda.
 */
@Service
public class ExtractorFacturaService {

    private static final Logger log = LoggerFactory.getLogger(ExtractorFacturaService.class);
    private static final String HERRAMIENTA = "registrar_datos_factura";

    private static final String INSTRUCCIONES = """
            Eres un asesor energético español. Extrae de esta factura de electricidad los datos para recalcularla \
            y regístralos con la herramienta. Reglas:
            - Potencia contratada: P1 = punta, P2 = valle (en kW).
            - Consumo: P1 = punta, P2 = llano, P3 = valle (en kWh). Si solo aparece el consumo total, ponlo en P1 \
            y deja P2 y P3 a 0.
            - Precios SIN impuestos. Potencia en €/kW·día (si la factura lo da en €/kW·año, divide entre 365). \
            Energía en €/kWh; si hay un único precio de energía, úsalo en los tres periodos.
            - alquilerContadorDia y bonoSocialDia en €/día (si dan el importe total, divide entre los días).
            - totalFactura: importe total con impuestos. consumoAnualKwh: consumo del último año si aparece.
            - Si un dato no aparece, omítelo. No inventes valores.
            - No extraigas datos personales (nombre, NIF, dirección, CUPS, IBAN).""";

    private final ComparadorProperties props;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public ExtractorFacturaService(ComparadorProperties props, ObjectMapper objectMapper) {
        this.props = props;
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(10).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(90).toMillis());
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    public boolean disponible() {
        return props.ia() != null && props.ia().configurada();
    }

    public DatosFactura extraer(byte[] archivo) {
        if (!disponible()) {
            throw new GlobalException("La lectura automática de facturas no está disponible: introduce los datos a mano",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }
        String tipo = tipoDeArchivo(archivo);
        Map<String, Object> adjunto = Map.of(
                "type", tipo.equals("application/pdf") ? "document" : "image",
                "source", Map.of("type", "base64", "media_type", tipo, "data", Base64.getEncoder().encodeToString(archivo)));

        Map<String, Object> peticion = new LinkedHashMap<>();
        peticion.put("model", props.ia().modelo());
        peticion.put("max_tokens", 1024);
        peticion.put("tools", List.of(Map.of(
                "name", HERRAMIENTA,
                "description", "Registra los datos de consumo y precios de la factura de luz",
                "input_schema", esquema())));
        peticion.put("tool_choice", Map.of("type", "tool", "name", HERRAMIENTA));
        peticion.put("messages", List.of(Map.of("role", "user", "content", List.of(adjunto,
                Map.of("type", "text", "text", INSTRUCCIONES)))));

        try {
            JsonNode respuesta = restClient.post()
                    .uri(props.ia().url())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-api-key", props.ia().apiKey())
                    .header("anthropic-version", "2023-06-01")
                    .body(peticion)
                    .retrieve()
                    .body(JsonNode.class);
            for (JsonNode bloque : respuesta.path("content")) {
                if ("tool_use".equals(bloque.path("type").asText())) {
                    return objectMapper.treeToValue(bloque.path("input"), DatosFactura.class);
                }
            }
            throw new IllegalStateException("La respuesta no contiene los datos de la factura");
        } catch (RestClientException | IllegalStateException | com.fasterxml.jackson.core.JsonProcessingException e) {
            log.warn("No se pudo leer la factura con IA: {}", e.getMessage());
            throw new GlobalException("No se ha podido leer la factura. Prueba con otro archivo o introduce los datos a mano",
                    HttpStatus.BAD_GATEWAY);
        }
    }

    /** Tipo real del archivo por sus primeros bytes (no se confía en la extensión ni en el Content-Type). */
    static String tipoDeArchivo(byte[] a) {
        if (a.length > 4 && a[0] == '%' && a[1] == 'P' && a[2] == 'D' && a[3] == 'F') return "application/pdf";
        if (a.length > 3 && (a[0] & 0xFF) == 0xFF && (a[1] & 0xFF) == 0xD8 && (a[2] & 0xFF) == 0xFF) return "image/jpeg";
        if (a.length > 8 && (a[0] & 0xFF) == 0x89 && a[1] == 'P' && a[2] == 'N' && a[3] == 'G') return "image/png";
        if (a.length > 12 && a[0] == 'R' && a[1] == 'I' && a[2] == 'F' && a[3] == 'F'
                && a[8] == 'W' && a[9] == 'E' && a[10] == 'B' && a[11] == 'P') return "image/webp";
        throw new IllegalArgumentException("Formato no admitido: sube la factura en PDF, JPG, PNG o WEBP");
    }

    private static Map<String, Object> esquema() {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("comercializadora", campo("string", "Compañía que emite la factura"));
        p.put("tarifa", campo("string", "Nombre de la tarifa o producto contratado"));
        p.put("dias", campo("integer", "Días del periodo de facturación"));
        p.put("potenciaP1Kw", campo("number", "Potencia contratada en punta (P1), kW"));
        p.put("potenciaP2Kw", campo("number", "Potencia contratada en valle (P2), kW"));
        p.put("consumoP1Kwh", campo("number", "Consumo en punta (P1), kWh"));
        p.put("consumoP2Kwh", campo("number", "Consumo en llano (P2), kWh"));
        p.put("consumoP3Kwh", campo("number", "Consumo en valle (P3), kWh"));
        p.put("precioPotenciaP1", campo("number", "Precio de la potencia en punta, €/kW·día sin impuestos"));
        p.put("precioPotenciaP2", campo("number", "Precio de la potencia en valle, €/kW·día sin impuestos"));
        p.put("precioEnergiaP1", campo("number", "Precio de la energía en punta, €/kWh sin impuestos"));
        p.put("precioEnergiaP2", campo("number", "Precio de la energía en llano, €/kWh sin impuestos"));
        p.put("precioEnergiaP3", campo("number", "Precio de la energía en valle, €/kWh sin impuestos"));
        p.put("alquilerContadorDia", campo("number", "Alquiler del contador, €/día"));
        p.put("bonoSocialDia", campo("number", "Financiación del bono social, €/día"));
        p.put("totalFactura", campo("number", "Importe total de la factura con impuestos, €"));
        p.put("consumoAnualKwh", campo("number", "Consumo del último año si la factura lo indica, kWh"));
        return Map.of("type", "object", "properties", p);
    }

    private static Map<String, Object> campo(String tipo, String descripcion) {
        return Map.of("type", tipo, "description", descripcion);
    }
}
