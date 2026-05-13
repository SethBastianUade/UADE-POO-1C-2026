package mvc.model;
import mvc.enums.CondicionImpositiva;

import java.time.LocalDate;

public class Proveedor {

    private int idProveedor;
    private String cuit;
    private String razonSocial;
    private String nombreComercial;
    private String domicilio;
    private String telefono;
    private String correoElectronico;
    private CondicionImpositiva condicionImpositiva;
    private String nroInscripcionIIBB;
    private LocalDate fechaInicioActividades;
    private double limiteDeudaAutorizado;
    private boolean activo;

    // Constructor
    public Proveedor(int idProveedor, String cuit, String razonSocial, String nombreComercial,
                     String domicilio, String telefono, String correoElectronico,
                     CondicionImpositiva condicionImpositiva, String nroInscripcionIIBB,
                     LocalDate fechaInicioActividades, double limiteDeudaAutorizado) {
        this.idProveedor = idProveedor;
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.nombreComercial = nombreComercial;
        this.domicilio = domicilio;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.condicionImpositiva = condicionImpositiva;
        this.nroInscripcionIIBB = nroInscripcionIIBB;
        this.fechaInicioActividades = fechaInicioActividades;
        this.limiteDeudaAutorizado = limiteDeudaAutorizado;
        this.activo = true;
    }

    // Métodos de negocio
    public double calcularDeudaActual() {
        // TODO: sumar documentos comerciales pendientes de pago
        return 0.0;
    }

    public double calcularMontoComprometido(double montoNuevaOC) {
        return calcularDeudaActual() + montoNuevaOC;
    }

    // Getters y Setters
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public CondicionImpositiva getCondicionImpositiva() { return condicionImpositiva; }
    public void setCondicionImpositiva(CondicionImpositiva condicionImpositiva) { this.condicionImpositiva = condicionImpositiva; }

    public String getNroInscripcionIIBB() { return nroInscripcionIIBB; }
    public void setNroInscripcionIIBB(String nroInscripcionIIBB) { this.nroInscripcionIIBB = nroInscripcionIIBB; }

    public LocalDate getFechaInicioActividades() { return fechaInicioActividades; }
    public void setFechaInicioActividades(LocalDate fechaInicioActividades) { this.fechaInicioActividades = fechaInicioActividades; }

    public double getLimiteDeudaAutorizado() { return limiteDeudaAutorizado; }
    public void setLimiteDeudaAutorizado(double limiteDeudaAutorizado) { this.limiteDeudaAutorizado = limiteDeudaAutorizado; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
