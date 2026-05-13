package mvc.model;

import mvc.enums.EstadoCancelacionDocumento;
import mvc.enums.EstadoRegistroDocumento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class DocumentoComercial {
    private int idDocumento;
    private String numeroDocumento;
    private LocalDate fechaEmision;
    private double importeTotal;
    private EstadoCancelacionDocumento estadoCancelacion;
    private EstadoRegistroDocumento estadoRegistro;
    private double montoPagado;
    private Proveedor proveedor;
    private List<LineaDocumento> lineas;
    private List<OrdenDeCompra> ordenesDeCompra;

    public DocumentoComercial(int idDocumento, String numeroDocumento,
                              LocalDate fechaEmision, double importeTotal, Proveedor proveedor) {
        this.idDocumento = idDocumento;
        this.numeroDocumento = numeroDocumento;
        this.fechaEmision = fechaEmision;
        this.importeTotal = importeTotal;
        this.proveedor = proveedor;
        this.estadoCancelacion = EstadoCancelacionDocumento.PENDIENTE;
        this.estadoRegistro = EstadoRegistroDocumento.INGRESADO;
        this.montoPagado = 0.0;
        this.lineas = new ArrayList<>();
        this.ordenesDeCompra = new ArrayList<>();
    }

    public abstract String getTipoDocumento();

    public double getSaldoPendiente() { return importeTotal - montoPagado; }

    public void aplicarPago(double monto) {
        this.montoPagado += monto;
        if (montoPagado >= importeTotal) {
            this.estadoCancelacion = EstadoCancelacionDocumento.CANCELADO;
        } else if (montoPagado > 0) {
            this.estadoCancelacion = EstadoCancelacionDocumento.PARCIALMENTE_CANCELADO;
        }
    }

    public boolean estaCancelado() {
        return this.estadoCancelacion == EstadoCancelacionDocumento.CANCELADO;
    }

    public int getIdDocumento() { return idDocumento; }
    public void setIdDocumento(int idDocumento) { this.idDocumento = idDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public double getImporteTotal() { return importeTotal; }
    public void setImporteTotal(double importeTotal) { this.importeTotal = importeTotal; }
    public EstadoCancelacionDocumento getEstadoCancelacion() { return estadoCancelacion; }
    public void setEstadoCancelacion(EstadoCancelacionDocumento estadoCancelacion) { this.estadoCancelacion = estadoCancelacion; }
    public EstadoRegistroDocumento getEstadoRegistro() { return estadoRegistro; }
    public void setEstadoRegistro(EstadoRegistroDocumento estadoRegistro) { this.estadoRegistro = estadoRegistro; }
    public double getMontoPagado() { return montoPagado; }
    public void setMontoPagado(double montoPagado) { this.montoPagado = montoPagado; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
    public List<LineaDocumento> getLineas() { return lineas; }
    public void setLineas(List<LineaDocumento> lineas) { this.lineas = lineas; }
    public List<OrdenDeCompra> getOrdenesDeCompra() { return ordenesDeCompra; }
    public void setOrdenesDeCompra(List<OrdenDeCompra> ordenesDeCompra) { this.ordenesDeCompra = ordenesDeCompra; }
}
