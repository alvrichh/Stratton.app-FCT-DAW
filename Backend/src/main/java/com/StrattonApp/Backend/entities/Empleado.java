package com.StrattonApp.Backend.entities;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representa a un empleado en el sistema.
 */
@Table(name = "empleados")
@Entity
public class Empleado implements UserDetails {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar en blanco")
    @Column(name= "Nombre", length = 60, nullable= true)
    private String nombre;

    @NotBlank(message = "El apellido no puede estar en blanco")
    @Column(name= "Apellidos", length = 60, nullable= true)
    private String apellidos;

    @NotBlank(message = "El nombre de usuario no puede estar en blanco")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
    @Column(name= "Usuario", unique = true)
    private String username;

    @NotBlank(message = "El correo electrónico no puede estar en blanco")
    @Email(message = "El formato del correo electrónico no es válido")
    @Column(name = "correo",unique = true)
    private String email;

    @NotBlank(message = "La contraseña no puede estar en blanco")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @ElementCollection(fetch = FetchType.EAGER, targetClass = Role.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "usuario_rol")
    @Column(name = "RolesUsuario")
    private Set<Role> role = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "idAsesoria")
    private Asesoria asesoria;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Cliente> clientes = new HashSet<>();

    
    @Transactional
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Cargar la colección de roles de manera temprana
    	role.size(); // Esto carga la colección de roles

        return role.stream()
                .map(role -> new SimpleGrantedAuthority(role.name())) // Usar el nombre del rol como string
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return role;
    }

    public void setRoles(Set<Role> role) {
        this.role = role;
    }

    public Asesoria getAsesoria() {
        return asesoria;
    }

    public void setAsesoria(Asesoria asesoria) {
        this.asesoria = asesoria;
    }

    public Set<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(Set<Cliente> clientes) {
        this.clientes = clientes;
    }

    public Object getIdEmpleado() {
        // Método temporal, por implementar
        throw new UnsupportedOperationException("Unimplemented method 'getIdEmpleado'");
    }

    public String getMainRole() {
        if (role.isEmpty()) {
            return "USER"; // Valor por defecto si no hay roles
        } else {
            // Obtener el primer rol en la colección
            Role mainRole = role.iterator().next();
            return mainRole.name();
        }
    }

}
