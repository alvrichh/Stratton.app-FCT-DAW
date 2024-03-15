CREATE VIEW VistaClientesContrato AS
SELECT c.nombre, c.apellidos, c.DNI, c.telefono, co.fecha_inicio
FROM CLIENTE c
JOIN CONTRATO co ON c.idCliente = co.idCliente;

SELECT * FROM VistaClientesContrato;

CREATE VIEW VistaClientesCUPS_CCC AS
SELECT c.nombre, c.apellidos, c.DNI, c.telefono, co.CUPS, b.CCC
FROM CLIENTE c
JOIN CONTRATO co ON c.idCliente = co.idCliente
JOIN BANCO b ON c.idCliente = b.idCliente;

SELECT * FROM VistaClientesCUPS_CCC;
