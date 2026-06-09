package mvc.controller;

import mvc.dto.ItemDTO;
import mvc.dto.LineaOrdenCompraDTO;
import mvc.dto.OrdenDeCompraDTO;
import mvc.dto.ProveedorDTO;
import mvc.enums.EstadoOrdenCompra;
import mvc.model.Item;
import mvc.model.LineaOrdenCompra;
import mvc.model.OrdenDeCompra;
import mvc.model.Proveedor;
import mvc.model.ProveedorItem;
import mvc.model.SistemaCompras;
import mvc.model.Usuario;
import mvc.view.OrdenDeCompraGUI;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class OrdenDeCompraController {
    private OrdenDeCompraGUI vista;
    private SistemaCompras sistema;

    public OrdenDeCompraController(OrdenDeCompraGUI vista) {
        this.vista = vista;
        this.sistema = SistemaCompras.getInstance();

        this.vista.getBtnCrearOC().addActionListener(e -> crearOC());
        this.vista.getBtnAgregarLinea().addActionListener(e -> agregarLinea());
        this.vista.getBtnConfirmar().addActionListener(e -> confirmarOC());
        this.vista.getBtnAprobar().addActionListener(e -> aprobarOC());
        this.vista.getBtnCancelar().addActionListener(e -> cancelarOC());
        this.vista.getCbItem().addActionListener(e -> sugerirPrecio());

        this.vista.getTablaOC().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalleSeleccion();
            }
        });

        cargarCombos();
        cargarTabla();
    }

    private void crearOC() {
        Usuario operador = sistema.getUsuarioLogueado();
        if (operador == null) {
            vista.mostrarMensaje("Debe iniciar sesión para crear una OC.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String cuit = vista.getCuitProveedorSeleccionado();
        if (cuit == null) {
            vista.mostrarMensaje("Seleccione un proveedor.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate fechaEntrega;
        try {
            fechaEntrega = LocalDate.parse(vista.getFechaEntrega());
        } catch (DateTimeParseException ex) {
            vista.mostrarMensaje("Fecha de entrega inválida (use AAAA-MM-DD).", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Proveedor proveedor = sistema.buscarProveedorPorCuit(cuit);
        OrdenDeCompra oc = sistema.crearOrdenDeCompra(proveedor, fechaEntrega, operador);
        cargarTabla();
        vista.seleccionarOC(oc.getNumeroOC());
        vista.mostrarMensaje("OC " + oc.getNumeroOC() + " creada en estado BORRADOR.\nAgregue líneas y luego confírmela.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void agregarLinea() {
        OrdenDeCompra oc = getOCSeleccionada();
        if (oc == null) {
            vista.mostrarMensaje("Seleccione una OC de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!oc.estaEditable()) {
            vista.mostrarMensaje("Solo se pueden agregar líneas a una OC en estado BORRADOR.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String codigoItem = vista.getCodigoItemSeleccionado();
        if (codigoItem == null) {
            vista.mostrarMensaje("Seleccione un ítem.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double cantidad = Double.parseDouble(vista.getCantidad());
            double precio = Double.parseDouble(vista.getPrecio());
            if (cantidad <= 0 || precio <= 0) {
                vista.mostrarMensaje("La cantidad y el precio deben ser mayores a cero.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Item item = sistema.buscarItemPorCodigo(codigoItem);
            sistema.agregarLineaOC(oc, item, cantidad, precio);
            vista.limpiarFormularioLinea();
            cargarTabla(); // el total de la OC cambió
            vista.seleccionarOC(oc.getNumeroOC());
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("La cantidad y el precio deben ser numéricos.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmarOC() {
        OrdenDeCompra oc = getOCSeleccionada();
        if (oc == null) {
            vista.mostrarMensaje("Seleccione una OC de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!oc.estaEditable()) {
            vista.mostrarMensaje("Solo se puede confirmar una OC en estado BORRADOR.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (oc.getLineas().isEmpty()) {
            vista.mostrarMensaje("La OC no tiene líneas cargadas.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Datos para el mensaje, calculados antes de confirmar
        double deuda = sistema.calcularDeudaProveedor(oc.getProveedor());
        double total = oc.calcularTotal();
        double limite = oc.getProveedor().getLimiteDeudaAutorizado();

        EstadoOrdenCompra resultado = sistema.confirmarOrdenDeCompra(oc);

        String detalle = String.format(
                "Deuda actual del proveedor: $%.2f%nTotal de la OC: $%.2f%nMonto comprometido: $%.2f%nLímite autorizado: $%.2f",
                deuda, total, deuda + total, limite);

        if (resultado == EstadoOrdenCompra.EMITIDA) {
            vista.mostrarMensaje("OC " + oc.getNumeroOC() + " EMITIDA: el monto comprometido no supera el límite de crédito.\n\n" + detalle,
                    "OC Emitida", JOptionPane.INFORMATION_MESSAGE);
        } else {
            vista.mostrarMensaje("El monto comprometido supera el límite de crédito del proveedor.\n"
                    + "La OC queda PENDIENTE DE APROBACIÓN por un supervisor.\n\n" + detalle,
                    "Requiere aprobación", JOptionPane.WARNING_MESSAGE);
        }
        cargarTabla();
        vista.seleccionarOC(oc.getNumeroOC());
    }

    private void aprobarOC() {
        OrdenDeCompra oc = getOCSeleccionada();
        if (oc == null || oc.getEstado() != EstadoOrdenCompra.PENDIENTE_APROBACION) {
            vista.mostrarMensaje("Seleccione una OC en estado PENDIENTE_APROBACION.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario usuario = sistema.getUsuarioLogueado();
        if (usuario == null || !usuario.tienePermiso(Usuario.APROBAR_OC)) {
            vista.mostrarMensaje("No tiene permisos para aprobar OC.\nSe requiere rol SUPERVISOR.", "Permiso denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (sistema.aprobarOrdenDeCompra(oc, usuario)) {
            vista.mostrarMensaje("OC " + oc.getNumeroOC() + " aprobada y EMITIDA.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
            vista.seleccionarOC(oc.getNumeroOC());
        }
    }

    private void cancelarOC() {
        OrdenDeCompra oc = getOCSeleccionada();
        if (oc == null) {
            vista.mostrarMensaje("Seleccione una OC de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (oc.getEstado() != EstadoOrdenCompra.BORRADOR && oc.getEstado() != EstadoOrdenCompra.PENDIENTE_APROBACION) {
            vista.mostrarMensaje("Solo se pueden cancelar OC en estado BORRADOR o PENDIENTE_APROBACION.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int resp = JOptionPane.showConfirmDialog(vista, "¿Desea cancelar la OC " + oc.getNumeroOC() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION && sistema.cancelarOrdenDeCompra(oc)) {
            cargarTabla();
        }
    }

    // Muestra las líneas de la OC seleccionada y habilita "Aprobar" solo si corresponde
    private void mostrarDetalleSeleccion() {
        OrdenDeCompra oc = getOCSeleccionada();
        if (oc == null) {
            vista.actualizarTablaLineas(new ArrayList<>());
            vista.habilitarAprobar(false);
            return;
        }
        List<LineaOrdenCompraDTO> dtos = new ArrayList<>();
        for (LineaOrdenCompra linea : oc.getLineas()) {
            dtos.add(new LineaOrdenCompraDTO(
                    linea.getItem().getCodigo(),
                    linea.getItem().getDescripcion(),
                    linea.getCantidad(),
                    linea.getPrecioUnitarioAcordado(),
                    linea.getSubtotal()
            ));
        }
        vista.actualizarTablaLineas(dtos);
        vista.habilitarAprobar(oc.getEstado() == EstadoOrdenCompra.PENDIENTE_APROBACION);
    }

    // Al elegir un ítem, sugiere el precio acordado con el proveedor de la OC
    // seleccionada; si no hay acuerdo, sugiere el precio base del ítem
    private void sugerirPrecio() {
        String codigo = vista.getCodigoItemSeleccionado();
        if (codigo == null) {
            return;
        }
        Item item = sistema.buscarItemPorCodigo(codigo);
        if (item == null) {
            return;
        }
        double sugerido = item.getPrecioUnitarioBase();
        OrdenDeCompra oc = getOCSeleccionada();
        if (oc != null) {
            for (ProveedorItem pi : oc.getProveedor().getPreciosAcordados()) {
                if (pi.getItem().getCodigo().equalsIgnoreCase(codigo)) {
                    sugerido = pi.getPrecioAcordado();
                    break;
                }
            }
        }
        vista.setPrecioSugerido(sugerido);
    }

    private OrdenDeCompra getOCSeleccionada() {
        String numeroOC = vista.getNumeroOCSeleccionada();
        return (numeroOC != null) ? sistema.buscarOrdenDeCompra(numeroOC) : null;
    }

    private void cargarCombos() {
        List<ProveedorDTO> proveedores = new ArrayList<>();
        for (Proveedor p : sistema.getProveedores()) {
            if (p.isActivo()) {
                proveedores.add(new ProveedorDTO(p.getCuit(), p.getRazonSocial(),
                        p.getCondicionImpositiva().toString(), p.getLimiteDeudaAutorizado(), p.isActivo()));
            }
        }
        vista.cargarComboProveedores(proveedores);

        List<ItemDTO> items = new ArrayList<>();
        for (Item i : sistema.getItems()) {
            if (i.isActivo()) {
                items.add(new ItemDTO(i.getCodigo(), i.getDescripcion(), i.getTipoItem(),
                        i.getRubro() != null ? i.getRubro().getDescripcion() : "",
                        i.getPrecioUnitarioBase(), i.isActivo(), "-"));
            }
        }
        vista.cargarComboItems(items);
    }

    private void cargarTabla() {
        List<OrdenDeCompraDTO> dtos = new ArrayList<>();
        for (OrdenDeCompra oc : sistema.getOrdenesDeCompra()) {
            dtos.add(new OrdenDeCompraDTO(
                    oc.getNumeroOC(),
                    oc.getFechaEmision().toString(),
                    oc.getProveedor().getRazonSocial(),
                    oc.calcularTotal(),
                    oc.getEstado().toString(),
                    oc.getOperadorCreador().getNombreUsuario()
            ));
        }
        vista.actualizarTabla(dtos);
    }
}
