package mvc.model;
import mvc.enums.RolUsuario;

import java.time.LocalDate;
import java.util.List;

public class Usuario {
    // Acciones que requieren rol SUPERVISOR (usar estas constantes desde los controllers)
    public static final String APROBAR_OC = "APROBAR_OC";
    public static final String APROBAR_DOCUMENTO = "APROBAR_DOCUMENTO";

    private String nombreUsuario;
    private String passwordHash;
    private String nombre;
    private String apellido;
    private RolUsuario rol;
    private boolean activo;

    public Usuario(String nombreUsuario, String passwordHash, String nombre, String apellido, RolUsuario rol) {
        this.nombreUsuario = nombreUsuario;
        this.passwordHash = passwordHash;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.activo = true;
    }

    public boolean esSupervisor() {
        return this.rol == RolUsuario.SUPERVISOR;
    }

    public boolean tienePermiso(String accion) {
        if (!activo) {
            return false;
        }
        if (rol == RolUsuario.SUPERVISOR) {
            return true;
        }
        // OPERADOR: puede crear OC, registrar documentos y generar OP,
        // pero no aprobar OC en PENDIENTE_APROBACION ni documentos OBSERVADOS
        return !APROBAR_OC.equals(accion) && !APROBAR_DOCUMENTO.equals(accion);
    }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}