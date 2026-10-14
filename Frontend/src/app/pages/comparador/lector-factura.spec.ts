import { leerFactura } from './lector-factura';
import { lineasDePdf } from './texto-factura';

/**
 * Texto tal como lo devuelve pdf.js de una factura real de Endesa (2.0TD, 56 días), solo con las
 * líneas de facturación: sin nombre, NIF, dirección, CUPS ni IBAN. Incluye las líneas en las que
 * la maquetación a dos columnas mezcla texto de la otra columna.
 */
const ENDESA = `Endesa Energía, S.A. Unipersonal. Inscrita en el Registro Mercantil de Madrid.
Periodo de facturación: del 04/07/2023 a 29/08/2023 (56 días)
Total 275,24 € Versión: 0000
De 04/07/2023 a 29/08/2023 (56 días)
Consumo Total 1.129,643 kWh
ha salido a 0.1 94344 €/kWh Coste en esta factura 4,92 €/día
A81948077. Consumo último año 6.936 kWh
Inscrita Las potencias máximas demandadas en el último año han sido 6,830 kW en P1 (punta) y
7,210 kW en P3 (valle).
Contrato de mercado libre: Tarifa One Luz Segmento de cargos: 1
Potencias contratadas: punta 5,750 kW; valle 5,750 kW
Potencia ......................................................................................................................... 37,65 € El importe total de su factura tiene este destino:
Pot. Punta 5,750 kW x 0,102759 Eur/kW x 56 días ....................................... 33,09 € > 72,85% Energía. Incluye, entre otros
Pot. Valle 5,750 kW x 0,014153 Eur/kW x 56 días ........................................... 4,56 € capacidad y la retribución al Operador
Energía ......................................................................................................................... 219,54 € > 0,54% Alquiler de contador
Facturación del Consumo 1.129,643 kWh x 0,194345 Eur/kWh ............. 219,54 € > 4,79% Impuestos
Promoción especial horas 0,000 kWh x 0,000000 Eur/kWh .......................... 0,00 € > 13,92% Peajes de transporte y distribución.
Financiación Bono Social 56 días x 0,038455 Eur/día ..................................... 2,15 € 0,00%, las anualidades del déficit 81,00%
Alquiler del contador ( 56 días x 0,026630 Eur/día ) ........................................ 1,49 € peninsulares) 18,00% y otros 1,00%.
TOTAL 275,24 €
*Importe de la energía asociada al mecanismo ibérico regulado por el Real Decreto-ley 10/2022, Energía kWh
Punta 23.404,00 23.739,00 1,00 0,00 335,00
compensación por el mecanismo ibérico (tope del gas), fue de 0,00 €/MWh. Llano 16.466,00 16.829,00 1,00 0,00 363,00
Efecto reductor del mecanismo ibérico de ajuste regulado por el RDL 10/2022: En el mes de Valle 4.747,00 5.178,00 1,00 0,00 431,00`;

describe('leerFactura', () => {
  it('lee todos los datos de una factura real de Endesa', () => {
    expect(leerFactura(ENDESA)).toEqual({
      comercializadora: 'Endesa',
      tarifa: 'Tarifa One Luz',
      dias: 56,
      potenciaP1Kw: 5.75,
      potenciaP2Kw: 5.75,
      precioPotenciaP1: 0.102759,
      precioPotenciaP2: 0.014153,
      // 335 + el ajuste de decimales del total facturado (1.129,643 frente a 1.129)
      consumoP1Kwh: 335.643,
      consumoP2Kwh: 363,
      consumoP3Kwh: 431,
      precioEnergiaP1: 0.194345,
      precioEnergiaP2: 0.194345,
      precioEnergiaP3: 0.194345,
      alquilerContadorDia: 0.02663,
      bonoSocialDia: 0.038455,
      totalFactura: 275.24,
      consumoAnualKwh: 6936,
    });
  });

  it('repara el texto de un OCR: barra de €/kW perdida y coma decimal perdida en un consumo', () => {
    // Líneas tal como las devuelve Tesseract de la foto de la misma factura
    const ocr = `Contrato de mercado libre: Tarifa One Luz | Segmento de cargos: 1
Potencias contratadas: punta 5,750 kW; valle 5,750 kW ;
Pot. Punta 5,750 KW x 0,102759 EUr/kW x 56 días -....—.——.—....—..... 93,09 €
Pot. Valle 5,750 kW x 0,014153 EurkW x 56 días ....exmmenemenemenemnnneeeo 4,56 €
Facturación del Consumo 1.129,643 kWh x 0,194345 Eur/kWh ............ 219,54 €
Promoción especial horas 0,000 kWh x 0,000000 Eur/kWh .............. 0,00 €
Financiación Bono Social 56 días x 0,038455 Eur/día .....rm.cen.ecnneensmenzeneens 2,15 €
Alquiler del contador ( 56 días x 0,026630 Eur/día ) ......zorm.eceneennneenemeccenes 1,49 €
TOTAL — NN 27626 Periodo 04/07/2023 29/08/2023 Multipl. Ajuste Consumo
Punta 23.404,00 23.739,00 1,00 0,00 335,00
compensación por'l mecanismo Ibérico (lope del gas), le de 0,00 E MW. Llano 16.465,00 16.829,00 1,00 000 36300
Electo reductor del mecanismo ibérico de ajuste regulado por el RDL 10/2022: En el mes de Valle 4.747,00 5.178,00 1,00 0,00 431,00`;
    const d = leerFactura(ocr);
    expect(d.tarifa).toBe('Tarifa One Luz');
    expect(d.dias).toBe(56);
    expect(d.precioPotenciaP1).toBe(0.102759);
    expect(d.precioPotenciaP2).toBe(0.014153);
    // 36300 era "363,00": se corrige porque así la suma cuadra con los 1.129,643 kWh facturados
    expect([d.consumoP1Kwh, d.consumoP2Kwh, d.consumoP3Kwh]).toEqual([335.643, 363, 431]);
    expect(d.precioEnergiaP1).toBe(0.194345);
    expect(d.bonoSocialDia).toBe(0.038455);
    expect(d.alquilerContadorDia).toBe(0.02663);
    // El total está ilegible ("27626" sin € ni coma): se deja vacío en lugar de inventarlo
    expect(d.totalFactura).toBeUndefined();
  });

  it('lee precios por periodo y potencia en €/kW·año', () => {
    const texto = `Iberdrola Clientes
Periodo de facturación (30 días)
Potencia P1 3,450 kW x 30,6725 €/kW año x 30 días 8,70 €
Potencia P2 3,450 kW x 2,5 €/kW año x 30 días 0,71 €
Energía punta 80 kWh x 0,2100 €/kWh
Energía llano 70 kWh x 0,1500 €/kWh
Energía valle 100 kWh x 0,0900 €/kWh
P1 80
P2 70
P3 100`;
    const d = leerFactura(texto);
    expect(d.comercializadora).toBe('Iberdrola');
    expect(d.dias).toBe(30);
    expect(d.precioPotenciaP1).toBeCloseTo(30.6725 / 365, 6);
    expect(d.precioEnergiaP1).toBe(0.21);
    expect(d.precioEnergiaP2).toBe(0.15);
    expect(d.precioEnergiaP3).toBe(0.09);
    expect([d.consumoP1Kwh, d.consumoP2Kwh, d.consumoP3Kwh]).toEqual([80, 70, 100]);
  });

  it('con solo el consumo total lo pone en punta', () => {
    const d = leerFactura('Consumo Total 250 kWh\nTérmino de energía 250 kWh x 0,15 €/kWh');
    expect([d.consumoP1Kwh, d.consumoP2Kwh, d.consumoP3Kwh]).toEqual([250, 0, 0]);
    expect(d.precioEnergiaP3).toBe(0.15);
  });

  it('devuelve vacío si el texto no es una factura', () => {
    expect(leerFactura('Hola, esto no es una factura')).toEqual({});
  });
});

describe('lineasDePdf', () => {
  it('reconstruye las líneas por altura y de izquierda a derecha', () => {
    const t = lineasDePdf([
      { str: '335,00', transform: [1, 0, 0, 1, 300, 100] },
      { str: 'Punta', transform: [1, 0, 0, 1, 10, 101] },
      { str: 'TOTAL 275,24 €', transform: [1, 0, 0, 1, 10, 200] },
    ]);
    expect(t).toBe('TOTAL 275,24 €\nPunta 335,00');
  });
});
