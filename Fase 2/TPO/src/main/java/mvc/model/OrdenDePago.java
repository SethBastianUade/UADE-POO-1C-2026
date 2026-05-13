package mvc.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdenDePago {
    private int idOP;
    private String numeroOP;
    private LocalDate fechaEmision;
    private double totalBrutoPagado;
    private double totalRetenido;
    private double totalNetoPagar;
    private Usuario operadorCreador;
    private Proveedor proveedor;
    private List<DocumentoPago> documentosPago;
    private List<MedioDePago> mediosDePago;
    private List<Retencion> retenciones;

    public OrdenDePago(int idOP, String numeroOP, LocalDate fechaEmision,
                       Usuario operadorCreador, Proveedor proveedor) {
        this.idOP = idOP;
        this.numeroOP = numeroOP;
        this.fechaEmision = fechaEmision;
        this.operadorCreador = operadorCreador;
        this.proveedor = proveedor;
        this.documentosPago = new ArrayList<>();
        this.mediosDePago = new ArrayList<>();
        this.retenciones = new ArrayList<>();
    }

    public double calcularTotalBruto() {
        return documentosPago.stream().mapToDouble(DocumentoPago::getMontoAplicado).sum();
    }

    public double calcularTotalRetenido() {
        return retenciones.stream().mapToDouble(Retencion::getMontoRetenido).sum();
    }

    public double calcularNeto() {
        return calcularTotalBruto() - calcularTotalRetenido();
    }

    public int getIdOP() { return idOP; }
    public void setIdOP(int idOP) { this.idOP = idOP; }
    public String getNumeroOP() { return numeroOP; }
    public void setNumeroOP(String numeroOP) { this.numeroOP = numeroOP; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public double getTotalBrutoPagado() { return totalBrutoPagado; }
    public void setTotalBrutoPagado(double totalBrutoPagado) { this.totalBrutoPagado = totalBrutoPagado; }
    public double getTotalRetenido() { return totalRetenido; }
    public void setTotalRetenido(double totalRetenido) { this.totalRetenido = totalRetenido; }
    public double getTotalNetoPagar() { return totalNetoPagar; }
    public void setTotalNetoPagar(double totalNetoPagar) { this.totalNetoPagar = totalNetoPagar; }
    public Usuario getOperadorCreador() { return operadorCreador; }
    public void setOperadorCreador(Usuario operadorCreador) { this.operadorCreador = operadorCreador; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
    public List<DocumentoPago> getDocumentosPago() { return documentosPago; }
    public void setDocumentosPago(List<DocumentoPago> documentosPago) { this.documentosPago = documentosPago; }
    public List<MedioDePago> getMediosDePago() { return mediosDePago; }
    public void setMediosDePago(List<MedioDePago> mediosDePago) { this.mediosDePago = mediosDePago; }
    public List<Retencion> getRetenciones() { return retenciones; }
    public void setRetenciones(List<Retencion> retenciones) { this.retenciones = retenciones; }
}
