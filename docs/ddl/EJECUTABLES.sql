DELIMITER //
CREATE FUNCTION calcular_edad(fecha_nacimiento DATE)
RETURNS INT DETERMINISTIC
BEGIN
    DECLARE edad INT;
    SET edad = TIMESTAMPDIFF(YEAR, fecha_nacimiento, CURDATE());
    RETURN edad;
END //
DELIMITER ;
-- EJECUCIÓN
SELECT nombre, apellidos, calcular_edad(fecha_nacimiento) AS edad
FROM CLIENTE
WHERE idCliente = 1; 

-- CLIENTES SIN RETRO COMISIÓN
DELIMITER //
CREATE PROCEDURE clientes_sin_retro()
BEGIN
    DECLARE fecha_limite DATE;
    -- Calcula la fecha límite 77 días en el pasado
    SET fecha_limite = DATE_SUB(CURDATE(), INTERVAL 77 DAY);
    -- Obtiene los clientes que no darán retro comisión
    SELECT c.nombre, c.apellidos
    FROM CLIENTE c
    INNER JOIN CONTRATO co ON c.idCliente = co.idCliente
    WHERE co.fecha_inicio <= fecha_limite;
END //
DELIMITER ;

CALL  clientes_sin_retro();

-- Muestra la cantidad de contratos que tiene cada comercializadora
SELECT co.nombre, COUNT(*) AS cantidad_contratos
FROM COMERCIALIZADORA co
INNER JOIN COMERCIALIZADORA_CONTRATO cc ON co.idComercializadora = cc.idComercializadora
GROUP BY co.nombre;

-- Muestra los clientes inactivos en el parner
SELECT c.nombre, c.apellidos, c.telefono, ct.idContrato, ct.estado
FROM CLIENTE c
INNER JOIN CONTRATO ct ON c.idCliente = ct.idCliente
WHERE ct.estado = 'Inactivo';

SELECT e.idEmpleado, e.nombre, e.apellidos, COUNT(c.idCliente) AS numero_clientes
FROM EMPLEADO e
LEFT JOIN CLIENTE c ON e.idCliente = c.idCliente
GROUP BY e.idEmpleado, e.nombre, e.apellidos;

-- Seleccioanr contratos con mayor potencia (sirve para encontrar las 2.1 y 3.0)
SELECT *
FROM CONTRATO
WHERE potencia_contratada > 5000;

-- Numero de clientes por provincia
SELECT u.Ciudad, COUNT(c.idCliente) AS numero_clientes
FROM UBICACION u
INNER JOIN CLIENTE c ON u.idCliente = c.idCliente
GROUP BY u.Ciudad;

-- Numero de cuentas de cada banco
SELECT BANCO.nombre, COUNT(BANCO.CCC) AS num_cuentas
FROM BANCO
GROUP BY BANCO.nombre
ORDER BY num_cuentas;


-- trigger de update contrato
DELIMITER //
CREATE TRIGGER actualizar_fecha_fin
BEFORE UPDATE ON contrato
FOR EACH ROW
BEGIN
    SET NEW.fecha_fin = DATE_ADD(NEW.fecha_inicio, INTERVAL 77 DAY);
END //
DELIMITER ;


