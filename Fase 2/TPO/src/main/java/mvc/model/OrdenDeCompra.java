package mvc.model;

import mvc.enums.EstadoOrdenCompra;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdenDeCompra {
    private int idOC;
    private String numeroOC;
    private LocalDate fechaEmision;
    private LocalDate fechaEntregaEsperada;
    private EstadoOrdenCompra estado;
    private String observaciones;
    private Usuario operadorCreador;
    private Usuario supervisorAprobador;
    private Proveedor proveedor;
    private List<LineaOrdenCompra> lineas;

    public OrdenDeCompra(int idOC, String numeroOC, LocalDate fechaEmision,
                         LocalDate fechaEntregaEsperada, Usuario operadorCreador, Proveedor proveedor) {
        this.idOC = idOC;
        this.numeroOC = numeroOC;
        this.fechaEmision = fechaEmision;
        this.fechaEntregaEsperada = fechaEntregaEsperada;
        this.operadorCreador = operadorCreador;
        this.proveedor = proveedor;
        this.estado = EstadoOrdenCompra.BORRADOR;
        this.lineas = new ArrayList<>();
    }

    public double calcularTotal() {
        return lineas.stream().mapToDouble(LineaOrdenCompra::getSubtotal).sum();
    }

    public EstadoOrdenCompra getEstado() { return estado; }
    public void setEstado(EstadoOrdenCompra nuevoEstado) { this.estado = nuevoEstado; }

    public LineaOrdenCompra getLineaPorItem(int idItem) {
        return lineas.stream()
                .filter(l -> l.getItem().getIdItem() == idItem)
                .findFirst()
                .orElse(null);
    }

    public void evaluarYActualizarEstado() {
        boolean todasCompletas = lineas.stream().allMatch(LineaOrdenCompra::estaCompletamenteRecibida);
        boolean algunaParcial = lineas.stream().anyMatch(l -> l.getCantidadPendiente() < l.getCantidad() && !l.estaCompletamenteRecibida());
        if (todasCompletas) {
            this.estado = EstadoOrdenCompra.CERRADA;
        } else if (algunaParcial) {
            this.estado = EstadoOrdenCompra.RECIBIDA_PARCIALMENTE;
        }
    }

    public int getIdOC() { return idOC; }
    public void setIdOC(int idOC) { this.idOC = idOC; }
    public String getNumeroOC() { return numeroOC; }
    public void setNumeroOC(String numeroOC) { this.numeroOC = numeroOC; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public LocalDate getFechaEntregaEsperada() { return fechaEntregaEsperada; }
    public void setFechaEntregaEsperada(LocalDate fechaEntregaEsperada) { this.fechaEntregaEsperada = fechaEntregaEsperada; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Usuario getOperadorCreador() { return operadorCreador; }
    public void setOperadorCreador(Usuario operadorCreador) { this.operadorCreador = operadorCreador; }
    public Usuario getSupervisorAprobador() { return supervisorAprobador; }
    public void setSupervisorAprobador(Usuario supervisorAprobador) { this.supervisorAprobador = supervisorAprobador; }
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
    public List<LineaOrdenCompra> getLineas() { return lineas; }
    public void setLineas(List<LineaOrdenCompra> lineas) { this.lineas = lineas; }
}
