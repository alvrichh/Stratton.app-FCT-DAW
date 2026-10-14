package com.StrattonApp.Backend.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Empleado de la asesoría. Es también el usuario que se autentica en la aplicación.
 */
@Entity
@Table(name = "empleados")
public class Empleado implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar en blanco")
    @Size(max = 60)
    @Column(name = "nombre", length = 60, nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido no puede estar en blanco")
    @Size(max = 80)
    @Column(name = "apellidos", length = 80, nullable = false)
    private String apellidos;

    @NotBlank(message = "El nombre de usuario no puede estar en blanco")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
    @Column(name = "usuario", unique = true, nullable = false, length = 20)
    private String username;

    @NotBlank(message = "El correo electrónico no puede estar en blanco")
    @Email(message = "El formato del correo electrónico no es válido")
    @Column(name = "correo", unique = true, nullable = false, length = 100)
    private String email;

    /** Contraseña cifrada con BCrypt. Nunca se serializa en las respuestas. */
    @NotBlank(message = "La contraseña no puede estar en blanco")
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = Role.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "usuario_rol", joinColumns = @JoinColumn(name = "empleado_id"))
    @Column(name = "rol", length = 20)
    private Set<Role> roles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "idAsesoria")
    @JsonIgnore
    private Asesoria asesoria;

    /** Al eliminar al empleado se eliminan sus clientes (caso de uso "Eliminar Empleado"). */
    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Cliente> clientes = new ArrayList<>();

    public Empleado() {
    }

    // ----- UserDetails -------------------------------------------------------

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.authority()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() { return username; }

    @Override
    @JsonIgnore
    public String getPassword() { return password; }

    @Override @JsonIgnore public boolean isAccountNonExpired() { return true; }
    @Override @JsonIgnore public boolean isAccountNonLocked() { return true; }
    @Override @JsonIgnore public boolean isCredentialsNonExpired() { return true; }
    @Override @JsonIgnore public boolean isEnabled() { return true; }

    // ----- Getters / setters ---------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles == null ? new HashSet<>() : roles; }
    public Asesoria getAsesoria() { return asesoria; }
    public void setAsesoria(Asesoria asesoria) { this.asesoria = asesoria; }
    public List<Cliente> getClientes() { return clientes; }
    public void setClientes(List<Cliente> clientes) { this.clientes = clientes; }

    /** Nombre completo del empleado. */
    public String getNombreCompleto() {
        return ((nombre == null ? "" : nombre) + " " + (apellidos == null ? "" : apellidos)).trim();
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    /** true si es ADMIN o MANAGER (puede ver los clientes de todos los empleados). */
    public boolean isJefe() {
        return roles.contains(Role.ADMIN) || roles.contains(Role.MANAGER);
    }

    /**
     * Rol principal del empleado, el de mayor privilegio: ADMIN &gt; MANAGER &gt; USER.
     */
    public String getMainRole() {
        if (roles.contains(Role.ADMIN)) return Role.ADMIN.name();
        if (roles.contains(Role.MANAGER)) return Role.MANAGER.name();
        return Role.USER.name();
    }
}
