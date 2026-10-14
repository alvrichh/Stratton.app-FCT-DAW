package com.StrattonApp.Backend;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Pruebas de los flujos principales de la API contra H2: login, roles, CRUD de clientes y empleados.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiIntegrationTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper json;

    private String adminToken;
    private String userToken;
    private long userId;

    @BeforeAll
    void login() throws Exception {
        JsonNode admin = login("admin", "admin");
        adminToken = admin.get("token").asText();
        JsonNode user = login("usuario", "usuario");
        userToken = user.get("token").asText();
        userId = user.get("id").asLong();
    }

    private JsonNode login(String username, String password) throws Exception {
        MvcResult res = mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("username", username, "password", password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andReturn();
        return json.readTree(res.getResponse().getContentAsString());
    }

    @Test
    void loginDevuelveRolPrincipal() throws Exception {
        JsonNode admin = login("admin", "admin");
        org.assertj.core.api.Assertions.assertThat(admin.get("role").asText()).isEqualTo("ADMIN");
        JsonNode user = login("usuario", "usuario");
        org.assertj.core.api.Assertions.assertThat(user.get("role").asText()).isEqualTo("USER");
    }

    @Test
    void loginConCredencialesIncorrectasDevuelve401() throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("username", "admin", "password", "incorrecta"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Usuario o contraseña incorrectos")));
    }

    @Test
    void sinTokenDevuelve401() throws Exception {
        mvc.perform(get("/api/v2/clientes")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v2/empleados")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/v2/empleados/1")).andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioNoPuedeGestionarEmpleados() throws Exception {
        mvc.perform(get("/api/v2/empleados").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
        mvc.perform(delete("/api/v2/empleados/1").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminListaEmpleadosSinContrasenas() throws Exception {
        mvc.perform(get("/api/v2/empleados").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[0].role", notNullValue()));
    }

    @Test
    void perfilDevuelveElUsuarioAutenticado() throws Exception {
        mvc.perform(get("/api/v2/empleados/perfil").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("usuario")))
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.clientes", hasSize(greaterThan(0))));
    }

    @Test
    void usuarioSoloVeSusClientesYAdminVeTodos() throws Exception {
        MvcResult res = mvc.perform(get("/api/v2/clientes").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk()).andReturn();
        JsonNode propios = json.readTree(res.getResponse().getContentAsString());
        org.assertj.core.api.Assertions.assertThat(propios.size()).isGreaterThan(0);
        for (JsonNode c : propios) {
            org.assertj.core.api.Assertions.assertThat(c.get("empleadoId").asLong()).isEqualTo(userId);
        }
        mvc.perform(get("/api/v2/clientes").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(10))))
                .andExpect(jsonPath("$[0].cups", notNullValue()))
                .andExpect(jsonPath("$[0].companiaContratada", notNullValue()))
                .andExpect(jsonPath("$[0].fechaSubidaContrato", notNullValue()))
                .andExpect(jsonPath("$[0].empleadoNombre", notNullValue()));
    }

    @Test
    void usuarioNoPuedeVerClienteDeOtroEmpleado() throws Exception {
        MvcResult res = mvc.perform(get("/api/v2/clientes").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk()).andReturn();
        JsonNode todos = json.readTree(res.getResponse().getContentAsString());
        long ajeno = -1;
        for (JsonNode c : todos) {
            if (c.get("empleadoId").asLong() != userId) { ajeno = c.get("idCliente").asLong(); break; }
        }
        org.assertj.core.api.Assertions.assertThat(ajeno).isGreaterThan(0);
        mvc.perform(get("/api/v2/clientes/" + ajeno).header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearActualizarYBorrarClienteConCups() throws Exception {
        Map<String, Object> body = Map.of(
                "nombre", "Nuevo", "apellidos", "Cliente Test", "dni", "12345678Z", "email", "nuevo@example.com",
                "telefono", "699999999", "direccion", "Calle Test 1", "iban", "ES91 2100 0418 4502 0005 1332",
                "cups", "ES0031408000000000AA0F", "companiaContratada", "Endesa", "fechaSubidaContrato", "2024-06-01");
        MvcResult res = mvc.perform(post("/api/v2/clientes")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cups", is("ES0031408000000000AA0F")))
                .andExpect(jsonPath("$.companiaContratada", is("Endesa")))
                .andExpect(jsonPath("$.fechaSubidaContrato", is("2024-06-01")))
                .andExpect(jsonPath("$.empleadoId", is((int) userId)))
                .andExpect(jsonPath("$.iban", is("ES9121000418450200051332")))
                .andExpect(jsonPath("$.suministros", hasSize(1)))
                .andReturn();
        long id = json.readTree(res.getResponse().getContentAsString()).get("idCliente").asLong();

        // Duplicado de DNI -> 409
        mvc.perform(post("/api/v2/clientes")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(body)))
                .andExpect(status().isConflict());

        // Actualizar: cambia comercializadora y teléfono
        Map<String, Object> update = new java.util.HashMap<>(body);
        update.put("companiaContratada", "Iberdrola");
        update.put("telefono", "688888888");
        mvc.perform(put("/api/v2/clientes/" + id)
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companiaContratada", is("Iberdrola")))
                .andExpect(jsonPath("$.telefono", is("688888888")))
                .andExpect(jsonPath("$.suministros[0].companiaContratada", is("Iberdrola")));

        // Añadir un segundo suministro
        mvc.perform(post("/api/v2/clientes/" + id + "/suministros")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("cups", "ES0021000000000001BB", "direccion", "Calle Gas 2",
                        "estado", "ACTIVO", "companiaContratada", "Naturgy"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado", is("ACTIVO")));
        mvc.perform(get("/api/v2/clientes/" + id + "/suministros").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Borrar el cliente borra sus suministros
        mvc.perform(delete("/api/v2/clientes/" + id).header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/v2/clientes/" + id).header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void cambiarElCupsPrincipalSustituyeElSuministro() throws Exception {
        Map<String, Object> body = Map.of("nombre", "Cambio", "apellidos", "De Cups", "dni", "45678901B",
                "email", "cups@example.com", "telefono", "655555555", "direccion", "Calle Cups 1",
                "cups", "ES0031000000000099AA", "companiaContratada", "Endesa");
        MvcResult res = mvc.perform(post("/api/v2/clientes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cups", is("ES0031000000000099AA")))
                .andReturn();
        long id = json.readTree(res.getResponse().getContentAsString()).get("idCliente").asLong();

        // El CUPS de otro cliente no se puede reutilizar -> 409
        Map<String, Object> ajeno = new java.util.HashMap<>(body);
        ajeno.put("cups", "ES0031000000000001BJ"); // CUPS del primer cliente de ejemplo
        mvc.perform(put("/api/v2/clientes/" + id)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(ajeno)))
                .andExpect(status().isConflict());

        // Cambiar el CUPS principal sustituye el suministro (misma comercializadora) sin duplicarlo
        Map<String, Object> update = new java.util.HashMap<>(body);
        update.put("cups", "ES0031000000000098ZZ");
        mvc.perform(put("/api/v2/clientes/" + id)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cups", is("ES0031000000000098ZZ")))
                .andExpect(jsonPath("$.suministros", hasSize(1)))
                .andExpect(jsonPath("$.suministros[0].companiaContratada", is("Endesa")));
        mvc.perform(get("/api/v2/clientes/" + id + "/suministros").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cups", is("ES0031000000000098ZZ")));

        // El CUPS antiguo queda libre: se puede dar de alta en otro cliente
        Map<String, Object> otro = Map.of("nombre", "Otro", "apellidos", "Cliente", "dni", "56789012C",
                "email", "otro@example.com", "telefono", "644444444", "cups", "ES0031000000000099AA");
        res = mvc.perform(post("/api/v2/clientes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(otro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cups", is("ES0031000000000099AA")))
                .andReturn();
        long otroId = json.readTree(res.getResponse().getContentAsString()).get("idCliente").asLong();

        // Limpieza: los tests comparten el contexto
        mvc.perform(delete("/api/v2/clientes/" + id).header("Authorization", "Bearer " + adminToken)).andExpect(status().isNoContent());
        mvc.perform(delete("/api/v2/clientes/" + otroId).header("Authorization", "Bearer " + adminToken)).andExpect(status().isNoContent());
    }

    @Test
    void validacionDeClienteDevuelve400ConErroresPorCampo() throws Exception {
        Map<String, Object> body = Map.of("nombre", "", "apellidos", "X", "dni", "123", "email", "no-es-email",
                "telefono", "12", "iban", "XX", "cups", "ABC");
        mvc.perform(post("/api/v2/clientes")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nombre", notNullValue()))
                .andExpect(jsonPath("$.errors.dni", notNullValue()))
                .andExpect(jsonPath("$.errors.email", notNullValue()))
                .andExpect(jsonPath("$.errors.telefono", notNullValue()))
                .andExpect(jsonPath("$.errors.iban", notNullValue()))
                .andExpect(jsonPath("$.errors.cups", notNullValue()));
    }

    @Test
    void menorDeEdadNoPuedeSerTitular() throws Exception {
        Map<String, Object> body = Map.of("nombre", "Menor", "apellidos", "De Edad", "dni", "87654321X",
                "email", "menor@example.com", "telefono", "677777777",
                "fechaNacimiento", java.time.LocalDate.now().minusYears(15).toString());
        mvc.perform(post("/api/v2/clientes")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("El titular del contrato debe ser mayor de edad")));
    }

    @Test
    void adminCreaEmpleadoQuePuedeIniciarSesion() throws Exception {
        Map<String, Object> body = Map.of("nombre", "Nuevo", "apellidos", "Empleado", "username", "nuevo",
                "email", "nuevo@strattonapp.com", "password", "secreto1", "roles", java.util.List.of("USER"));
        MvcResult res = mvc.perform(post("/api/v2/empleados")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();
        long id = json.readTree(res.getResponse().getContentAsString()).get("id").asLong();

        // La contraseña se guardó cifrada: el nuevo empleado puede iniciar sesión
        login("nuevo", "secreto1");

        // Actualizar sin contraseña conserva la actual y cambia el rol
        Map<String, Object> update = Map.of("nombre", "Nuevo", "apellidos", "Empleado Editado", "username", "nuevo",
                "email", "nuevo@strattonapp.com", "roles", java.util.List.of("MANAGER"));
        mvc.perform(put("/api/v2/empleados/" + id)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apellidos", is("Empleado Editado")))
                .andExpect(jsonPath("$.role", is("MANAGER")));
        login("nuevo", "secreto1");

        mvc.perform(delete("/api/v2/empleados/" + id).header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eliminar", is(true)));
    }

    @Test
    void adminNoPuedeQuitarseElRolAdminNiSiquieraOmitiendoRoles() throws Exception {
        JsonNode admin = login("admin", "admin");
        long adminId = admin.get("id").asLong();
        // Sin "roles" en la petición se conservan los actuales (sigue siendo ADMIN)
        Map<String, Object> sinRoles = Map.of("nombre", "Admin", "apellidos", "Stratton", "username", "admin",
                "email", "admin@strattonapp.com");
        mvc.perform(put("/api/v2/empleados/" + adminId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(sinRoles)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("ADMIN")));
        // Quitarse ADMIN explícitamente -> 400
        Map<String, Object> degradar = Map.of("nombre", "Admin", "apellidos", "Stratton", "username", "admin",
                "email", "admin@strattonapp.com", "roles", java.util.List.of("USER"));
        mvc.perform(put("/api/v2/empleados/" + adminId)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(degradar)))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/v2/empleados").header("Authorization", "Bearer " + adminToken)).andExpect(status().isOk());
    }

    @Test
    void actualizarClienteNoPisaLaDireccionDelSuministro() throws Exception {
        Map<String, Object> body = Map.of("nombre", "Direccion", "apellidos", "Suministro", "dni", "33333333P",
                "email", "dir@example.com", "telefono", "622222222", "direccion", "Calle Cliente 1",
                "cups", "ES0031000000000050AA", "companiaContratada", "Endesa");
        MvcResult res = mvc.perform(post("/api/v2/clientes")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.suministros[0].direccion", is("Calle Cliente 1")))
                .andReturn();
        long id = json.readTree(res.getResponse().getContentAsString()).get("idCliente").asLong();

        // Se edita la dirección del suministro desde su propio endpoint
        mvc.perform(put("/api/v2/suministros/ES0031000000000050AA")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("cups", "ES0031000000000050AA", "direccion", "Calle Suministro 9", "estado", "ACTIVO"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direccion", is("Calle Suministro 9")));

        // Cambiar el teléfono del cliente no toca la dirección del suministro
        Map<String, Object> update = new java.util.HashMap<>(body);
        update.put("telefono", "622222223");
        mvc.perform(put("/api/v2/clientes/" + id)
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direccion", is("Calle Cliente 1")))
                .andExpect(jsonPath("$.suministros[0].direccion", is("Calle Suministro 9")));

        mvc.perform(delete("/api/v2/clientes/" + id).header("Authorization", "Bearer " + adminToken)).andExpect(status().isNoContent());
    }

    @Test
    void erroresDeSpringMvcDevuelven4xxEnJson() throws Exception {
        mvc.perform(get("/api/v2/clientes/abc").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/v2/clientes/1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isMethodNotAllowed());
        mvc.perform(post("/api/v2/clientes").header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.TEXT_PLAIN).content("hola"))
                .andExpect(status().isUnsupportedMediaType());
        // username con espacios: se recorta antes de validar -> 400 por tamaño, nunca 500
        mvc.perform(put("/api/v2/empleados/3").header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("nombre", "U", "apellidos", "A", "username", "  ab  ", "email", "usuario@strattonapp.com"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username", notNullValue()));
    }

    @Test
    void registroPublicoCreaUsuarioConRolUser() throws Exception {
        Map<String, Object> body = Map.of("nombre", "Auto", "apellidos", "Registrado", "username", "autoreg",
                "email", "autoreg@example.com", "password", "clave123");
        mvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("USER")));
        // Usuario duplicado -> 409
        mvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }

    @Test
    void catalogosDisponiblesParaUsuariosAutenticados() throws Exception {
        mvc.perform(get("/api/v2/comercializadoras").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(15)))
                .andExpect(jsonPath("$[?(@.companiaContratada == 'Endesa')].logo", org.hamcrest.Matchers.contains("assets/images/comercializadoras/endesa.png")))
                .andExpect(jsonPath("$[?(@.logo == null)]", hasSize(0)))
                .andExpect(jsonPath("$[?(@.companiaContratada == 'Repsol')]", hasSize(1)));
        mvc.perform(get("/api/v2/asesorias").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void swaggerEsPublico() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }
}
