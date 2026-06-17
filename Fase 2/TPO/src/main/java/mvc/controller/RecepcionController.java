package mvc.controller;

import mvc.dto.OrdenDeCompraDTO;
import mvc.dto.RecepcionLineaDTO;
import mvc.model.LineaOrdenCompra;
import mvc.model.OrdenDeCompra;
import mvc.view.RecepcionMercaderiaGUI;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class RecepcionController {
    private RecepcionMercaderiaGUI vista;

    public RecepcionController(RecepcionMercaderiaGUI vista) {
        this.vista = vista;

        this.vista.getCbOC().addActionListener(e -> cargarLineas());
        this.vista.getBtnRegistrar().addActionListener(e -> registrar());

        cargarCombo();
        cargarLineas();
    }

    private void cargarCombo() {
        List<OrdenDeCompraDTO> dtos = new ArrayList<>();
        for (OrdenDeCompra oc : OrdenDeCompraController.getOrdenesRecibibles()) {
            dtos.add(new OrdenDeCompraDTO(
                    oc.getNumeroOC(),
                    oc.getFechaEmision().toString(),
                    oc.getProveedor().getRazonSocial(),
                    oc.calcularTotal(),
                    oc.getEstado().toString(),
                    oc.getOperadorCreador().getNombreUsuario()
            ));
        }
        vista.cargarComboOrdenes(dtos);
    }

    private void cargarLineas() {
        OrdenDeCompra oc = getOCSeleccionada();
        if (oc == null) {
            vista.actualizarTablaLineas(new ArrayList<>());
            return;
        }
        List<RecepcionLineaDTO> dtos = new ArrayList<>();
        for (LineaOrdenCompra linea : oc.getLineas()) {
            dtos.add(new RecepcionLineaDTO(
                    linea.getIdLineaOrdenCompra(),
                    linea.getItem().getCodigo(),
                    linea.getItem().getDescripcion(),
                    linea.getCantidad(),
                    linea.getCantidadRecibida(),
                    linea.getCantidadPendiente()
            ));
        }
        vista.actualizarTablaLineas(dtos);
    }

    private void registrar() {
        OrdenDeCompra oc = getOCSeleccionada();
        if (oc == null) {
            vista.mostrarMensaje("Seleccione una OC.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idLinea = vista.getIdLineaSeleccionada();
        if (idLinea == -1) {
            vista.mostrarMensaje("Seleccione una línea de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double cantidad;
        try {
            cantidad = Double.parseDouble(vista.getCantidad());
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("La cantidad debe ser numérica.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String estadoPrevio = oc.getEstado().toString();
        if (OrdenDeCompraController.registrarRecepcion(oc, idLinea, cantidad)) {
            vista.mostrarMensaje("Recepción registrada.\nEstado de la OC: " + estadoPrevio
                    + " -> " + oc.getEstado(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarCantidad();
            cargarCombo();   // si la OC quedó CERRADA, sale del combo de recibibles
            cargarLineas();
        } else {
            vista.mostrarMensaje("Cantidad inválida: debe ser mayor a cero y no superar lo pendiente.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private OrdenDeCompra getOCSeleccionada() {
        String numeroOC = vista.getNumeroOCSeleccionada();
        return (numeroOC != null) ? OrdenDeCompraController.buscarOrdenDeCompra(numeroOC) : null;
    }
}
