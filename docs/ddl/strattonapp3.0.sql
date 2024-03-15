DROP DATABASE IF EXISTS StrattonAPP_3;
CREATE DATABASE StrattonAPP_3;
USE StrattonAPP_3;

CREATE TABLE CLIENTE (
  idCliente INT PRIMARY KEY AUTO_INCREMENT,
  telefono INT(9) UNIQUE NOT NULL,
  correo VARCHAR(50) UNIQUE,
  nombre VARCHAR(25) NOT NULL,
  apellidos VARCHAR(25) NOT NULL, 
  DNI VARCHAR(9) UNIQUE NOT NULL,
  direccion VARCHAR(100),
  fecha_nacimiento DATE
);

CREATE TABLE BANCO (
  CCC VARCHAR(24) PRIMARY KEY UNIQUE,
  nombre VARCHAR(10),
  idCliente INT,
  FOREIGN KEY (idCliente) REFERENCES CLIENTE(idCliente)
);

CREATE TABLE UBICACION (
  idUbicacion INT PRIMARY KEY NOT NULL,
  ZIP INT(5),
  municipio VARCHAR(50),
  Ciudad VARCHAR(50),
  idCliente INT,
  FOREIGN KEY (idCliente) REFERENCES CLIENTE(idCliente)
);

CREATE TABLE COMERCIALIZADORA (
  idComercializadora INT PRIMARY KEY not null,
  nombre VARCHAR(15)
);

CREATE TABLE ASESORIA (
  idAsesoria INT PRIMARY KEY not null,
  direccion VARCHAR(50),
  rating INT
);

CREATE TABLE EMPLEADO (
  idEmpleado INT PRIMARY KEY not null,
  idAsesoria INT not null,
  nombre VARCHAR(15),
  apellidos VARCHAR(30),
  idJefe INT, -- nombre del jefe, o si es jefe o no
  FOREIGN KEY (idAsesoria) REFERENCES ASESORIA(idAsesoria),
  FOREIGN KEY (idJefe) REFERENCES EMPLEADO(idEmpleado)
);

CREATE TABLE CONTRATO (
  idContrato INT PRIMARY KEY,
  idCliente INT NOT NULL,
  CUPS VARCHAR(23) NOT NULL,
  plan VARCHAR(25),
  estado VARCHAR(25) NOT NULL, 
  fecha_inicio DATE,
  fecha_fin DATE, -- TE AVISARÁ A LOS 77 DÍAS
  cosnumo_anual INT,
  potencia_contratada INT,
  FOREIGN KEY (idCliente) REFERENCES CLIENTE(idCliente)
);

CREATE TABLE ASESORIA_COMERCIALIZADORA (
  idAsesoria INT,
  idComercializadora INT,
  FOREIGN KEY (idAsesoria) REFERENCES ASESORIA(idAsesoria),
  FOREIGN KEY (idComercializadora) 
  REFERENCES COMERCIALIZADORA(idComercializadora),
  PRIMARY KEY (idAsesoria, idComercializadora)
);

CREATE TABLE COMERCIALIZADORA_CONTRATO (
  idComercializadora INT,
  idContrato INT,
  FOREIGN KEY (idComercializadora) 
  REFERENCES COMERCIALIZADORA(idComercializadora),
  FOREIGN KEY (idContrato) REFERENCES CONTRATO(idContrato),
  PRIMARY KEY (idComercializadora, idContrato)
  
);