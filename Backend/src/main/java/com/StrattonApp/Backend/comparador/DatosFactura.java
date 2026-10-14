package com.StrattonApp.Backend.comparador;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Datos de una factura de luz (peaje 2.0TD) necesarios para recalcularla.
 * Los devuelve la lectura con IA y el cliente los revisa antes de calcular.
 * No incluye datos personales (nombre, NIF, dirección, IBAN): no se extraen ni se guardan.
 *
 * Potencia: P1 = punta, P2 = valle. Consumo: P1 = punta, P2 = llano, P3 = valle.
 * Precios sin impuestos.
 */
public record DatosFactura(
        @Size(max = 80) String comercializadora,
        @Size(max = 80) String tarifa,
        @NotNull(message = "Indica los días facturados") @Min(value = 1, message = "Los días facturados deben ser al menos 1") @Max(value = 400, message = "Los días facturados no pueden superar 400") Integer dias,
        @NotNull(message = "Indica la potencia contratada en punta") @DecimalMin(value = "0.1", message = "La potencia en punta no es válida") Double potenciaP1Kw,
        @NotNull(message = "Indica la potencia contratada en valle") @DecimalMin(value = "0.1", message = "La potencia en valle no es válida") Double potenciaP2Kw,
        @NotNull(message = "Indica el consumo en punta") @PositiveOrZero Double consumoP1Kwh,
        @NotNull(message = "Indica el consumo en llano") @PositiveOrZero Double consumoP2Kwh,
        @NotNull(message = "Indica el consumo en valle") @PositiveOrZero Double consumoP3Kwh,
        @NotNull(message = "Indica el precio de la potencia en punta") @PositiveOrZero Double precioPotenciaP1,
        @NotNull(message = "Indica el precio de la potencia en valle") @PositiveOrZero Double precioPotenciaP2,
        @NotNull(message = "Indica el precio de la energía en punta") @PositiveOrZero Double precioEnergiaP1,
        @NotNull(message = "Indica el precio de la energía en llano") @PositiveOrZero Double precioEnergiaP2,
        @NotNull(message = "Indica el precio de la energía en valle") @PositiveOrZero Double precioEnergiaP3,
        @PositiveOrZero Double alquilerContadorDia,
        @PositiveOrZero Double bonoSocialDia,
        @PositiveOrZero Double totalFactura,
        @PositiveOrZero Double consumoAnualKwh) {

    public double consumoTotalKwh() {
        return consumoP1Kwh + consumoP2Kwh + consumoP3Kwh;
    }
}
