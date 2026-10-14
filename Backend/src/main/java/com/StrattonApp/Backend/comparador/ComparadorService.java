package com.StrattonApp.Backend.comparador;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.StrattonApp.Backend.comparador.ResultadoComparativa.Desglose;

/**
 * Recalcula una factura de luz 2.0TD con los precios del cliente y con los de la oferta propia.
 * El cálculo es determinista (no interviene la IA) y sigue el orden de una factura real:
 * <ol>
 *   <li>potencia = kW × €/kW·día × días (punta y valle),</li>
 *   <li>energía = kWh × €/kWh por periodo,</li>
 *   <li>impuesto eléctrico sobre potencia + energía + financiación del bono social (con su mínimo por kWh),</li>
 *   <li>IVA sobre todo lo anterior más el alquiler del contador.</li>
 * </ol>
 * Las dos facturas usan los mismos impuestos (los vigentes) y el mismo alquiler de contador y
 * bono social, que son conceptos regulados: la diferencia sale solo de los precios de potencia y energía.
 */
@Service
public class ComparadorService {

    private final ComparadorProperties props;

    public ComparadorService(ComparadorProperties props) {
        this.props = props;
    }

    public ComparadorProperties.Oferta oferta() {
        return props.oferta();
    }

    public ResultadoComparativa comparar(DatosFactura f) {
        ComparadorProperties.Oferta o = props.oferta();
        double alquiler = valor(f.alquilerContadorDia());
        double bono = valor(f.bonoSocialDia());

        Desglose actual = desglose(f.dias(), f.potenciaP1Kw(), f.potenciaP2Kw(),
                f.precioPotenciaP1(), f.precioPotenciaP2(),
                f.consumoP1Kwh(), f.consumoP2Kwh(), f.consumoP3Kwh(),
                f.precioEnergiaP1(), f.precioEnergiaP2(), f.precioEnergiaP3(), alquiler, bono);
        Desglose propia = desglose(f.dias(), f.potenciaP1Kw(), f.potenciaP2Kw(),
                o.precioPotenciaP1(), o.precioPotenciaP2(),
                f.consumoP1Kwh(), f.consumoP2Kwh(), f.consumoP3Kwh(),
                o.precioEnergia(), o.precioEnergia(), o.precioEnergia(), alquiler, bono);

        double ahorro = redondear(actual.total() - propia.total());
        double porcentaje = actual.total() > 0 ? redondear(ahorro / actual.total() * 100) : 0;

        return new ResultadoComparativa(actual, propia, ahorro, porcentaje, ahorroAnual(f, alquiler, bono),
                redondear(f.consumoTotalKwh()), f.dias(), f.totalFactura(), o.nombre(), o.precioEnergia());
    }

    /**
     * Ahorro en un año: si la factura trae el consumo del último año se reparte entre periodos con
     * las mismas proporciones de la factura; si no, se extrapola el periodo facturado a 365 días.
     */
    private double ahorroAnual(DatosFactura f, double alquiler, double bono) {
        double total = f.consumoTotalKwh();
        double factor = (f.consumoAnualKwh() != null && f.consumoAnualKwh() > 0 && total > 0)
                ? f.consumoAnualKwh() / total
                : 365.0 / f.dias();
        double c1 = f.consumoP1Kwh() * factor, c2 = f.consumoP2Kwh() * factor, c3 = f.consumoP3Kwh() * factor;
        ComparadorProperties.Oferta o = props.oferta();
        Desglose actual = desglose(365, f.potenciaP1Kw(), f.potenciaP2Kw(), f.precioPotenciaP1(), f.precioPotenciaP2(),
                c1, c2, c3, f.precioEnergiaP1(), f.precioEnergiaP2(), f.precioEnergiaP3(), alquiler, bono);
        Desglose propia = desglose(365, f.potenciaP1Kw(), f.potenciaP2Kw(), o.precioPotenciaP1(), o.precioPotenciaP2(),
                c1, c2, c3, o.precioEnergia(), o.precioEnergia(), o.precioEnergia(), alquiler, bono);
        return redondear(actual.total() - propia.total());
    }

    Desglose desglose(int dias, double kwP1, double kwP2, double precioPotP1, double precioPotP2,
            double kwhP1, double kwhP2, double kwhP3, double precioEnP1, double precioEnP2, double precioEnP3,
            double alquilerDia, double bonoDia) {
        ComparadorProperties.Impuestos imp = props.impuestos();
        double potencia = redondear(kwP1 * precioPotP1 * dias) + redondear(kwP2 * precioPotP2 * dias);
        double energia = redondear(kwhP1 * precioEnP1 + kwhP2 * precioEnP2 + kwhP3 * precioEnP3);
        double bonoSocial = redondear(bonoDia * dias);
        double alquiler = redondear(alquilerDia * dias);

        double baseElectrico = potencia + energia + bonoSocial;
        double minimo = (kwhP1 + kwhP2 + kwhP3) * imp.impuestoElectricoMinimo();
        double impuestoElectrico = redondear(Math.max(baseElectrico * imp.impuestoElectrico(), minimo));
        double iva = redondear((baseElectrico + impuestoElectrico + alquiler) * imp.iva());
        double total = redondear(baseElectrico + impuestoElectrico + alquiler + iva);
        return new Desglose(redondear(potencia), energia, bonoSocial, impuestoElectrico, alquiler, iva, total);
    }

    private static double valor(Double d) {
        return d == null ? 0 : d;
    }

    /** Redondeo a céntimos, como en las facturas. */
    static double redondear(double importe) {
        return BigDecimal.valueOf(importe).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
