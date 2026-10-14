package com.StrattonApp.Backend.comparador;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuración del comparador de facturas (prefijo {@code app.comparador}).
 *
 * @param oferta    precios de la oferta propia con la que se compara
 * @param impuestos impuestos vigentes que se aplican a las dos facturas
 * @param ia        acceso a la API de Anthropic para leer las facturas
 * @param maxExtraccionesPorHora        límite de lecturas con IA por IP y hora
 * @param maxExtraccionesTotalesPorHora límite de lecturas con IA de todo el comparador por hora
 */
@ConfigurationProperties(prefix = "app.comparador")
public record ComparadorProperties(Oferta oferta, Impuestos impuestos, Ia ia, int maxExtraccionesPorHora,
        int maxExtraccionesTotalesPorHora) {

    /**
     * Oferta propia: precio único de la energía para todas las horas y término de potencia.
     *
     * @param nombre            nombre comercial que se muestra en la comparativa
     * @param precioEnergia     €/kWh, igual en punta, llano y valle
     * @param precioPotenciaP1  €/kW·día en punta
     * @param precioPotenciaP2  €/kW·día en valle
     */
    public record Oferta(String nombre, double precioEnergia, double precioPotenciaP1, double precioPotenciaP2) {}

    /**
     * @param iva                      tipo de IVA (0,21 = 21 %)
     * @param impuestoElectrico        tipo del impuesto especial sobre la electricidad (0,0511269632)
     * @param impuestoElectricoMinimo  mínimo del impuesto eléctrico en €/kWh (1 €/MWh)
     */
    public record Impuestos(double iva, double impuestoElectrico, double impuestoElectricoMinimo) {}

    /**
     * @param apiKey clave de la API de Anthropic (vacía: la lectura automática queda desactivada)
     * @param modelo modelo de Claude con visión que lee la factura
     * @param url    endpoint de la API de mensajes
     */
    public record Ia(String apiKey, String modelo, String url) {
        public boolean configurada() {
            return apiKey != null && !apiKey.isBlank();
        }
    }
}
