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

    // Base imponible y monto de IVA discriminados, derivados de las líneas.
    // Factura los sobrescribe con los importes sellados por AFIP; ND/NC los computan
    // de sus líneas (antes figuraban con IVA $0 en el Libro IVA).
    public double getBaseImponible() {
        double base = 0.0;
        for (LineaDocumento linea : lineas) {
            base += linea.getSubtotal();
        }
        return base;
    }

    public double getMontoIVA() {
        double iva = 0.0;
        for (LineaDocumento linea : lineas) {
            iva += linea.getSubtotal() * linea.getAlicuotaIVA() / 100;
        }
        return iva;
    }

    // Cuánto aporta este documento a la deuda con el proveedor.
    // NotaDeCredito lo sobrescribe con signo negativo (un crédito resta deuda).
    public double getImpactoDeuda() { return getSaldoPendiente(); }

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

    public void agregarLinea(LineaDocumento linea) {
        lineas.add(linea);
    }

    // Valida el documento contra las OC del proveedor y actualiza estadoRegistro:
    // cada línea debe estar amparada por una OC emitida que contenga el ítem
    // con el mismo precio acordado. Recibe las OC porque el documento no
    // conoce la lista global del sistema.
    public EstadoRegistroDocumento validarContraOC(List<OrdenDeCompra> ocsDelProveedor) {
        boolean todoAmparado = !lineas.isEmpty();
        for (LineaDocumento linea : lineas) {
            boolean lineaAmparada = false;
            for (OrdenDeCompra oc : ocsDelProveedor) {
                if (!oc.amparaFacturacion()) {
                    continue;
                }
                LineaOrdenCompra lineaOC = oc.getLineaPorItem(linea.getItem().getIdItem());
                if (lineaOC != null && lineaOC.getPrecioUnitarioAcordado() == linea.getPrecioUnitario()) {
                    lineaAmparada = true;
                    if (!ordenesDeCompra.contains(oc)) {
                        ordenesDeCompra.add(oc); // vincula la OC que ampara al documento
                    }
                    break;
                }
            }
            // Coherencia de conceptos (§2.4 paso 3): el ítem debe pertenecer a un rubro
            // asociado al proveedor; si no, el documento queda OBSERVADO
            boolean rubroCoherente = proveedor.getRubros().contains(linea.getItem().getRubro());
            if (!lineaAmparada || !rubroCoherente) {
                todoAmparado = false;
            }
        }
        this.estadoRegistro = todoAmparado ? EstadoRegistroDocumento.APROBADO : EstadoRegistroDocumento.OBSERVADO;
        return this.estadoRegistro;
    }

    public boolean aprobar(Usuario supervisor) {
        if (estadoRegistro != EstadoRegistroDocumento.OBSERVADO || !supervisor.esSupervisor()) {
            return false;
        }
        this.estadoRegistro = EstadoRegistroDocumento.APROBADO;
        return true;
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
