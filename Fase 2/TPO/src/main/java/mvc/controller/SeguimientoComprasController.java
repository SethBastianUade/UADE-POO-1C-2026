package mvc.controller;

import mvc.dto.OrdenDeCompraDTO;
import mvc.dto.OrdenDePagoDTO;
import mvc.dto.ProveedorDTO;
import mvc.dto.RubroDTO;
import mvc.enums.EstadoOrdenCompra;
import mvc.model.OrdenDeCompra;
import mvc.model.OrdenDePago;
import mvc.model.Proveedor;
import mvc.model.Rubro;
import mvc.model.SistemaCompras;
import mvc.view.SeguimientoComprasGUI;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class SeguimientoComprasController {
    private SeguimientoComprasGUI vista;
    private SistemaCompras sistema;

    public SeguimientoComprasController(SeguimientoComprasGUI vista) {
        this.vista = vista;
        this.sistema = SistemaCompras.getInstance();

        this.vista.getBtnBuscarOC().addActionListener(e -> buscarOC());
        this.vista.getBtnBuscarPagos().addActionListener(e -> buscarPagos());

        cargarCombos();
        buscarOC();
        buscarPagos();
    }

    private void buscarOC() {
        String estadoTexto = vista.getEstadoOCFiltro();
        EstadoOrdenCompra estado = (estadoTexto != null) ? EstadoOrdenCompra.valueOf(estadoTexto) : null;

        String codigoRubro = vista.getCodigoRubroFiltro();
        Rubro rubro = (codigoRubro != null) ? sistema.buscarRubro(codigoRubro) : null;

        String cuit = vista.getCuitProveedorOCFiltro();
        Proveedor proveedor = (cuit != null) ? sistema.buscarProveedorPorCuit(cuit) : null;

        List<OrdenDeCompraDTO> dtos = new ArrayList<>();
        for (OrdenDeCompra oc : sistema.buscarOrdenesDeCompra(estado, rubro, proveedor)) {
            dtos.add(new OrdenDeCompraDTO(
                    oc.getNumeroOC(), oc.getFechaEmision().toString(), oc.getProveedor().getRazonSocial(),
                    oc.calcularTotal(), oc.getEstado().toString(), oc.getOperadorCreador().getNombreUsuario()
            ));
        }
        vista.actualizarTablaOC(dtos);
    }

    private void buscarPagos() {
        LocalDate desde, hasta;
        try {
            desde = vista.getDesde().isEmpty() ? null : LocalDate.parse(vista.getDesde());
            hasta = vista.getHasta().isEmpty() ? null : LocalDate.parse(vista.getHasta());
        } catch (DateTimeParseException ex) {
            vista.mostrarMensaje("Fechas inválidas (use AAAA-MM-DD o deje vacío).", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String tipoMedio = vista.getTipoMedioFiltro();
        String cuit = vista.getCuitProveedorOPFiltro();
        Proveedor proveedor = (cuit != null) ? sistema.buscarProveedorPorCuit(cuit) : null;

        List<OrdenDePagoDTO> dtos = new ArrayList<>();
        for (OrdenDePago op : sistema.buscarOrdenesDePago(desde, hasta, tipoMedio, proveedor)) {
            dtos.add(new OrdenDePagoDTO(
                    op.getNumeroOP(), op.getProveedor().getRazonSocial(), op.getFechaEmision().toString(),
                    op.getTotalBrutoPagado(), op.getTotalRetenido(), op.getTotalNetoPagar(),
                    op.getOperadorCreador().getNombreUsuario()
            ));
        }
        vista.actualizarTablaPagos(dtos);
    }

    private void cargarCombos() {
        List<ProveedorDTO> proveedores = new ArrayList<>();
        for (Proveedor p : sistema.getProveedores()) {
            proveedores.add(new ProveedorDTO(p.getCuit(), p.getRazonSocial(),
                    p.getCondicionImpositiva().toString(), p.getLimiteDeudaAutorizado(), p.isActivo()));
        }
        vista.cargarComboProveedores(proveedores);

        List<RubroDTO> rubros = new ArrayList<>();
        for (Rubro r : sistema.getRubros()) {
            rubros.add(new RubroDTO(r.getCodigo(), r.getDescripcion(), r.isActivo()));
        }
        vista.cargarComboRubros(rubros);
    }
}
