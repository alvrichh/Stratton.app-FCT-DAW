package com.StrattonApp.Backend.comparador;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.StrattonApp.Backend.exceptions.GlobalException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Límite sencillo de lecturas con IA por IP y hora (ventana fija, en memoria), más un tope global
 * por hora. El comparador es público y cada lectura cuesta dinero: la IP de X-Forwarded-For se
 * puede falsear, así que el tope global es el que acota el gasto en cualquier caso.
 */
@Component
public class LimitadorPeticiones {

    private static final long HORA_MS = 3_600_000L;

    private final ComparadorProperties props;
    private final Clock reloj;
    private final Map<String, long[]> ventanas = new ConcurrentHashMap<>();
    private final long[] global = { 0, 0 };

    @Autowired
    public LimitadorPeticiones(ComparadorProperties props) {
        this(props, Clock.systemUTC());
    }

    LimitadorPeticiones(ComparadorProperties props, Clock reloj) {
        this.props = props;
        this.reloj = reloj;
    }

    public void comprobar(HttpServletRequest req) {
        long ahora = reloj.millis();
        if (ventanas.size() > 10_000) {
            ventanas.values().removeIf(v -> ahora - v[0] >= HORA_MS);
        }
        long[] ventana = ventanas.compute(ip(req), (ip, v) -> {
            if (v == null || ahora - v[0] >= HORA_MS) {
                return new long[] { ahora, 1 };
            }
            v[1]++;
            return v;
        });
        if (ventana[1] > props.maxExtraccionesPorHora()) {
            throw new GlobalException("Has alcanzado el límite de facturas por hora. Inténtalo más tarde o introduce los datos a mano",
                    HttpStatus.TOO_MANY_REQUESTS);
        }
        synchronized (global) {
            if (ahora - global[0] >= HORA_MS) {
                global[0] = ahora;
                global[1] = 0;
            }
            if (++global[1] > props.maxExtraccionesTotalesPorHora()) {
                throw new GlobalException("El comparador está muy solicitado ahora mismo. Inténtalo más tarde o introduce los datos a mano",
                        HttpStatus.TOO_MANY_REQUESTS);
            }
        }
    }

    /** IP del cliente; detrás de nginx/Render es la primera de X-Forwarded-For. */
    private static String ip(HttpServletRequest req) {
        String reenviada = req.getHeader("X-Forwarded-For");
        if (reenviada != null && !reenviada.isBlank()) {
            return reenviada.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }
}
