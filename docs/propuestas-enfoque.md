# Propuestas de enfoque del portal (pendiente de decidir)

Fecha: 14/10/2026 · Estado: **sin decidir**

Hoy StrattonApp es, sobre todo, un CRUD de fichas (clientes, empleados, comercializadoras).
Hay dos propuestas para enfocarlo mejor. Son independientes: se puede hacer una, otra o las dos
(en ese caso, recomendable empezar por la B, que es la que recoge el diagrama de casos de uso).

---

## A. Pirámide de equipos (visibilidad jerárquica)

### Situación actual
- Cada cliente pertenece a **un empleado** (`Cliente.empleado`); empleados y clientes pertenecen a una asesoría.
- MANAGER y ADMIN ven los clientes de **todos**; USER solo los suyos.
- No existe relación jefe → subordinado (`Empleado` no tiene superior): un jefe no tiene "su equipo".
- "Empleados" es una pantalla de administración (solo ADMIN), una lista sin jerarquía.

### Propuesta
- **Modelo:** añadir `Empleado.superior` (otro empleado). Con eso se forma el árbol
  director → jefes → asesores, con los niveles que haga falta.
- **Regla de visibilidad:** cada uno ve **sus clientes y los de todos los que dependen de él**,
  a cualquier profundidad. El ADMIN lo ve todo. Los roles quedan como permisos; la pirámide la marca el árbol.
- **Inicio = panel de control** calculado sobre "mi pirámide": clientes activos / pendientes / K.O.,
  contratos del mes, suministros por comercializadora.
- **Pantalla "Mi equipo"** (jefes): árbol del equipo con los números de cada persona. Al hacer clic en un
  asesor se ve su cartera; al hacer clic en un jefe intermedio, su equipo.
- **Clientes:** una sola lista con filtro "de quién" (yo, una persona o un equipo entero) y columna de asesor responsable.
- **Empleados:** queda como administración (altas y quién depende de quién).

### Trabajo
- Backend:
  - campo `superior` y consulta de subordinados (recursiva);
  - nueva regla de visibilidad en clientes y suministros;
  - endpoints de "mi equipo" y de las cifras del panel;
  - datos de ejemplo con 3 niveles.
- Frontend: panel de inicio, "Mi equipo" con el árbol, filtro por equipo, elegir superior al dar de alta un empleado.
- Tests: un jefe ve su rama y no la de al lado; un asesor solo lo suyo.

---

## B. El cliente como expediente (diagrama de casos de uso "Gestión de clientes")

### Lo que dice el diagrama
El **empleado** lleva al cliente por un proceso hasta dejarlo contratado:

1. **Crear cliente** y editar su información.
2. **Asignar compañía y oferta** → *extends* **Enviar contrato al cliente** (actor: Comercializadora).
3. **Asociar CUPS y ubicación** → *include* **Distribuidora**.
4. **Asociar información bancaria** → *extends* **Domiciliar recibo a la compañía** (actor: Banco).
5. **Ver detalles** y **eliminar** cliente.

Leyenda: verde = requiere login; morado = requiere **login y LOPD**; azul = informativo.

### Qué cubre hoy la app

| Caso de uso | Estado |
|---|---|
| Crear / editar / ver / eliminar cliente | ✅ |
| Asignar compañía y oferta | ⚠️ solo comercializadora; el "plan" es fijo por compañía, no hay oferta por cliente |
| Enviar contrato | ❌ no hay estado del contrato (enviado, firmado…) |
| CUPS y ubicación → distribuidora | ⚠️ se guarda el CUPS, pero no se deduce la distribuidora ni la ubicación por código postal (RF-4) |
| Información bancaria → domiciliación | ⚠️ se guarda el IBAN, sin estado de domiciliación |
| LOPD | ❌ no se registra el consentimiento |

### Propuesta: ficha del cliente por pasos
1. **Datos personales + consentimiento LOPD** (con fecha). Sin LOPD no se pueden hacer los pasos morados.
2. **Suministro:** CUPS y código postal.
   - La **distribuidora** se deduce del código del CUPS (por ejemplo `ES0021…` es i-DE, de Iberdrola,
     y `ES0031…` es e-distribución, de Endesa).
   - La provincia y el municipio se sacan del código postal.
3. **Compañía y oferta** (de una lista de ofertas por comercializadora) → **contrato**:
   pendiente de enviar → enviado → firmado / K.O.
4. **Banco:** IBAN → **domiciliación** pendiente / confirmada.

- Cada cliente muestra de un vistazo **en qué paso está**.
- La lista de clientes pasa a ser una **bandeja de trabajo**: "faltan 3 LOPD, 5 contratos sin enviar, 2 domiciliaciones pendientes".

### Trabajo
- Modelo:
  - consentimiento LOPD (sí/no + fecha);
  - entidad `Oferta` por comercializadora;
  - estado del contrato;
  - `Distribuidora` (tabla de códigos CUPS);
  - estado de domiciliación;
  - ubicación por código postal.
- Backend: validaciones de orden (sin LOPD no hay contrato ni domiciliación); endpoints de cambio de estado y de "pendientes".
- Frontend: ficha por pasos con indicador de progreso, bandeja de pendientes y filtros por paso.
- Tests del flujo completo y de las restricciones LOPD.

---

## Decisión

- [ ] Solo A (pirámide)
- [ ] Solo B (expediente)
- [ ] B y después A
- [ ] Ninguna por ahora

Notas:
