package com.StrattonApp.Backend.comparador;

/**
 * Resultado de comparar la factura del cliente con la oferta propia, con los mismos impuestos
 * vigentes en los dos lados.
 *
 * @param actual              la factura recalculada con los precios actuales del cliente
 * @param oferta              la misma factura con los precios de la oferta propia
 * @param ahorro              diferencia en el periodo facturado (positiva si la oferta es más barata)
 * @param ahorroPorcentaje    ahorro sobre la factura actual, en %
 * @param ahorroAnual         estimación anual (con el consumo anual de la factura o extrapolando los días)
 * @param consumoTotalKwh     consumo del periodo
 * @param dias                días facturados
 * @param totalFacturaOriginal total que figura en la factura (con los impuestos de su fecha), o null
 * @param nombreOferta        nombre de la oferta propia
 * @param precioEnergiaOferta €/kWh de la oferta propia
 */
public record ResultadoComparativa(
        Desglose actual,
        Desglose oferta,
        double ahorro,
        double ahorroPorcentaje,
        double ahorroAnual,
        double consumoTotalKwh,
        int dias,
        Double totalFacturaOriginal,
        String nombreOferta,
        double precioEnergiaOferta) {

    /** Líneas de una factura de luz, en euros y redondeadas a céntimos. */
    public record Desglose(
            double potencia,
            double energia,
            double bonoSocial,
            double impuestoElectrico,
            double alquilerContador,
            double iva,
            double total) {}
}
