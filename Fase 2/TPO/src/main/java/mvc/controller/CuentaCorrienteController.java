package mvc.controller;

import mvc.dto.DocumentoComercialDTO;
import mvc.dto.MovimientoCuentaCorrienteDTO;
import mvc.dto.ProveedorDTO;
import mvc.model.DocumentoComercial;
import mvc.model.OrdenDePago;
import mvc.model.Proveedor;
import mvc.view.CuentaCorrienteGUI;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CuentaCorrienteController {
    // Movimiento intermedio para ordenar documentos y pagos por fecha
    private static class Movimiento {
        LocalDate fecha;
        String tipo;
        String numero;
        double debe;
        double haber;

        Movimiento(LocalDate fecha, String tipo, String numero, double debe, double haber) {
            this.fecha = fecha;
            this.tipo = tipo;
            this.numero = numero;
            this.debe = debe;
            this.haber = haber;
        }
    }

    private CuentaCorrienteGUI vista;

    public CuentaCorrienteController(CuentaCorrienteGUI vista) {
        this.vista = vista;

        this.vista.getCbProveedor().addActionListener(e -> cargarCuentaCorriente());
        this.vista.getBtnFiltrarPendientes().addActionListener(e -> cargarPendientes());

        cargarComboProveedores();
        cargarCuentaCorriente();
    }

    private Proveedor getProveedorSeleccionado() {
        String cuit = vista.getCuitProveedorSeleccionado();
        return (cuit != null) ? ProveedorController.buscarProveedorPorCuit(cuit) : null;
    }

    private void cargarCuentaCorriente() {
        Proveedor proveedor = getProveedorSeleccionado();
        if (proveedor == null) {
            vista.actualizarTablaMovimientos(new ArrayList<>());
            vista.actualizarTablaPendientes(new ArrayList<>());
            vista.setDeudaActual(0);
            return;
        }

        // Cronología: documentos al debe (NC con signo negativo) y pagos al haber
        List<Movimiento> movimientos = new ArrayList<>();
        for (DocumentoComercial doc : DocumentoComercialController.getDocumentosComerciales(proveedor)) {
            double debe = doc.getTipoDocumento().equals("NOTA_DE_CREDITO")
                    ? -doc.getImporteTotal()
                    : doc.getImporteTotal();
            movimientos.add(new Movimiento(doc.getFechaEmision(), doc.getTipoDocumento(),
                    doc.getNumeroDocumento(), debe, 0));
        }
        for (OrdenDePago op : OrdenDePagoController.getOrdenesDePago(proveedor)) {
            movimientos.add(new Movimiento(op.getFechaEmision(), "ORDEN_DE_PAGO",
                    op.getNumeroOP(), 0, op.getTotalBrutoPagado()));
        }
        movimientos.sort(Comparator.comparing(m -> m.fecha));

        // Saldo acumulado = debe - haber
        List<MovimientoCuentaCorrienteDTO> dtos = new ArrayList<>();
        double saldo = 0;
        for (Movimiento m : movimientos) {
            saldo += m.debe - m.haber;
            dtos.add(new MovimientoCuentaCorrienteDTO(m.fecha.toString(), m.tipo, m.numero, m.debe, m.haber, saldo));
        }
        vista.actualizarTablaMovimientos(dtos);

        vista.setDeudaActual(ProveedorController.calcularDeudaProveedor(proveedor));
        cargarPendientes();
    }

    private void cargarPendientes() {
        Proveedor proveedor = getProveedorSeleccionado();
        if (proveedor == null) {
            return;
        }
        int dias;
        try {
            dias = vista.getDiasAntiguedad().isEmpty() ? 0 : Integer.parseInt(vista.getDiasAntiguedad());
            if (dias < 0) {
                vista.mostrarMensaje("La antigüedad no puede ser negativa.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("La antigüedad debe ser un número entero de días.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<DocumentoComercialDTO> dtos = new ArrayList<>();
        for (DocumentoComercial doc : DocumentoComercialController.getDocumentosPendientes(proveedor, dias)) {
            dtos.add(new DocumentoComercialDTO(
                    doc.getNumeroDocumento(), doc.getTipoDocumento(), doc.getProveedor().getRazonSocial(),
                    doc.getFechaEmision().toString(), doc.getImporteTotal(), doc.getSaldoPendiente(),
                    doc.getEstadoCancelacion().toString(), doc.getEstadoRegistro().toString()
            ));
        }
        vista.actualizarTablaPendientes(dtos);
    }

    private void cargarComboProveedores() {
        List<ProveedorDTO> proveedores = new ArrayList<>();
        for (Proveedor p : ProveedorController.getProveedores()) {
            proveedores.add(new ProveedorDTO(p.getCuit(), p.getRazonSocial(),
                    p.getCondicionImpositiva().toString(), p.getLimiteDeudaAutorizado(), p.isActivo()));
        }
        vista.cargarComboProveedores(proveedores);
    }
}
