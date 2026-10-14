package com.StrattonApp.Backend.DTO;

import com.StrattonApp.Backend.entities.Asesoria;

/**
 * Representación de una asesoría para el frontend.
 */
public class AsesoriaDTO {

    private Long idAsesoria;
    private String nombre;
    private String direccion;
    private String descripcion;
    private int valoracion;
    private int totalEmpleados;
    private int totalClientes;

    public AsesoriaDTO() {
    }

    public static AsesoriaDTO from(Asesoria a) {
        AsesoriaDTO dto = new AsesoriaDTO();
        dto.idAsesoria = a.getIdAsesoria();
        dto.nombre = a.getNombre();
        dto.direccion = a.getDireccion();
        dto.descripcion = a.getDescripcion();
        dto.valoracion = a.getValoracion();
        dto.totalEmpleados = a.getEmpleados() == null ? 0 : a.getEmpleados().size();
        dto.totalClientes = a.getClientes() == null ? 0 : a.getClientes().size();
        return dto;
    }

    public Long getIdAsesoria() { return idAsesoria; }
    public void setIdAsesoria(Long idAsesoria) { this.idAsesoria = idAsesoria; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getValoracion() { return valoracion; }
    public void setValoracion(int valoracion) { this.valoracion = valoracion; }
    public int getTotalEmpleados() { return totalEmpleados; }
    public void setTotalEmpleados(int totalEmpleados) { this.totalEmpleados = totalEmpleados; }
    public int getTotalClientes() { return totalClientes; }
    public void setTotalClientes(int totalClientes) { this.totalClientes = totalClientes; }
}
