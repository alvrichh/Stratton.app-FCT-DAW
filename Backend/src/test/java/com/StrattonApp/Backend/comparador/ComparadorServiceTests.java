package com.StrattonApp.Backend.comparador;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.Test;

import com.StrattonApp.Backend.comparador.ResultadoComparativa.Desglose;

/**
 * Cálculo de la factura y de la comparativa, sin Spring.
 * Los datos son los de una factura real de Endesa (2.0TD, 56 días) sin ningún dato personal.
 */
class ComparadorServiceTests {

    private static final ComparadorProperties.Oferta OFERTA =
            new ComparadorProperties.Oferta("Tarifa Stratton", 0.10, 0.09, 0.09);

    /** Factura de ejemplo: 5,75 kW, 1.129,643 kWh a 0,194345 €/kWh, total 275,24 € (IVA 5 % e IE 0,5 % de 2023). */
    private static final DatosFactura FACTURA = new DatosFactura("Endesa", "Tarifa One Luz", 56,
            5.75, 5.75, 335.643, 363.0, 431.0,
            0.102759, 0.014153, 0.194345, 0.194345, 0.194345,
            0.026630, 0.038455, 275.24, 6936.0);

    private static ComparadorService servicio(double iva, double impuestoElectrico) {
        return new ComparadorService(new ComparadorProperties(OFERTA,
                new ComparadorProperties.Impuestos(iva, impuestoElectrico, 0.001)));
    }

    @Test
    void reproduceLaFacturaRealConSusImpuestos() {
        // Con los impuestos que tenía la factura (IVA 5 %, impuesto eléctrico 0,5 %) el total debe cuadrar al céntimo
        Desglose d = servicio(0.05, 0.005).comparar(FACTURA).actual();

        assertThat(d.potencia()).isEqualTo(37.65);
        assertThat(d.energia()).isEqualTo(219.54);
        assertThat(d.bonoSocial()).isEqualTo(2.15);
        assertThat(d.alquilerContador()).isEqualTo(1.49);
        assertThat(d.impuestoElectrico()).isEqualTo(1.30);
        assertThat(d.iva()).isEqualTo(13.11);
        assertThat(d.total()).isEqualTo(275.24);
    }

    @Test
    void comparaConLaOfertaConLosImpuestosVigentesEnLosDosLados() {
        ResultadoComparativa r = servicio(0.21, 0.0511269632).comparar(FACTURA);

        // Oferta: 5,75 kW × 0,09 × 56 × 2 = 57,96 € de potencia; 1.129,643 kWh × 0,10 = 112,96 € de energía
        assertThat(r.oferta().potencia()).isEqualTo(57.96);
        assertThat(r.oferta().energia()).isEqualTo(112.96);
        // Bono social y contador son los mismos en las dos facturas
        assertThat(r.oferta().bonoSocial()).isEqualTo(r.actual().bonoSocial());
        assertThat(r.oferta().alquilerContador()).isEqualTo(r.actual().alquilerContador());

        assertThat(r.actual().total()).isGreaterThan(r.oferta().total());
        assertThat(r.ahorro()).isEqualTo(ComparadorService.redondear(r.actual().total() - r.oferta().total()));
        assertThat(r.ahorroPorcentaje()).isCloseTo(r.ahorro() / r.actual().total() * 100, within(0.01));
        // Con 6.936 kWh al año el ahorro anual es mucho mayor que el del periodo
        assertThat(r.ahorroAnual()).isGreaterThan(r.ahorro() * 4);
        assertThat(r.totalFacturaOriginal()).isEqualTo(275.24);
    }

    @Test
    void siLaOfertaEsMasCaraElAhorroSaleNegativo() {
        DatosFactura barata = new DatosFactura(null, null, 30, 3.3, 3.3, 50.0, 50.0, 50.0,
                0.03, 0.01, 0.07, 0.07, 0.07, 0.0266, 0.0127, null, null);
        ResultadoComparativa r = servicio(0.21, 0.0511269632).comparar(barata);

        assertThat(r.ahorro()).isNegative();
        assertThat(r.ahorroAnual()).isNegative();
    }

    @Test
    void elImpuestoElectricoRespetaElMinimoPorKwh() {
        Desglose d = servicio(0.21, 0.0511269632).desglose(30, 1, 1, 0, 0, 1000, 0, 0, 0.001, 0, 0, 0, 0);
        // 5,11 % de 1 € sería 0,05 €; el mínimo es 1 €/MWh × 1 MWh = 1 €
        assertThat(d.impuestoElectrico()).isEqualTo(1.00);
    }
}
