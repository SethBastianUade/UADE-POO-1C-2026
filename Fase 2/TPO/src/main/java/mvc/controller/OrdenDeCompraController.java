package mvc.controller;

import mvc.dto.ItemDTO;
import mvc.dto.LineaOrdenCompraDTO;
import mvc.dto.OrdenDeCompraDTO;
import mvc.dto.ProveedorDTO;
import mvc.enums.EstadoOrdenCompra;
import mvc.model.Item;
import mvc.model.LineaOrdenCompra;
import mvc.model.OrdenDeCompra;
import mvc.model.Producto;
import mvc.model.Proveedor;
import mvc.model.ProveedorItem;
import mvc.model.Rubro;
import mvc.model.Usuario;
import mvc.view.OrdenDeCompraGUI;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class OrdenDeCompraController {
    // ============================================================
    // DATOS DEL MÓDULO: ÓRDENES DE COMPRA (compartidos por toda la app)
    // ============================================================
    private static final List<OrdenDeCompra> ordenesDeCompra = new ArrayList<>();
    private static int contadorIdOC = 1;

    private OrdenDeCompraGUI vista;

    public OrdenDeCompraController(OrdenDeCompraGUI vista) {
        this.vista = vista;

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
        Usuario operador = LoginController.getUsuarioLogueado();
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

        Proveedor proveedor = ProveedorController.buscarProveedorPorCuit(cuit);
        OrdenDeCompra oc = crearOrdenDeCompra(proveedor, fechaEntrega, operador);
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
            Item item = ItemController.buscarItemPorCodigo(codigoItem);
            agregarLineaOC(oc, item, cantidad, precio);
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
        double deuda = ProveedorController.calcularDeudaProveedor(oc.getProveedor());
        double total = oc.calcularTotal();
        double limite = oc.getProveedor().getLimiteDeudaAutorizado();

        EstadoOrdenCompra resultado = confirmarOrdenDeCompra(oc);

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
        Usuario usuario = LoginController.getUsuarioLogueado();
        if (usuario == null || !usuario.tienePermiso(Usuario.APROBAR_OC)) {
            vista.mostrarMensaje("No tiene permisos para aprobar OC.\nSe requiere rol SUPERVISOR.", "Permiso denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (aprobarOrdenDeCompra(oc, usuario)) {
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
        if (resp == JOptionPane.YES_OPTION && cancelarOrdenDeCompra(oc)) {
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
        Item item = ItemController.buscarItemPorCodigo(codigo);
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
        return (numeroOC != null) ? buscarOrdenDeCompra(numeroOC) : null;
    }

    private void cargarCombos() {
        List<ProveedorDTO> proveedores = new ArrayList<>();
        for (Proveedor p : ProveedorController.getProveedores()) {
            if (p.isActivo()) {
                proveedores.add(new ProveedorDTO(p.getCuit(), p.getRazonSocial(),
                        p.getCondicionImpositiva().toString(), p.getLimiteDeudaAutorizado(), p.isActivo()));
            }
        }
        vista.cargarComboProveedores(proveedores);

        List<ItemDTO> items = new ArrayList<>();
        for (Item i : ItemController.getItems()) {
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
        for (OrdenDeCompra oc : getOrdenesDeCompra()) {
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

    // ============================================================
    // LÓGICA DE NEGOCIO DEL MÓDULO (antes en SistemaCompras)
    // ============================================================
    public static OrdenDeCompra crearOrdenDeCompra(Proveedor proveedor, LocalDate fechaEntregaEsperada, Usuario operador) {
        String numero = String.format("OC-%05d", contadorIdOC);
        OrdenDeCompra oc = new OrdenDeCompra(contadorIdOC, numero, LocalDate.now(), fechaEntregaEsperada, operador, proveedor);
        contadorIdOC++;
        ordenesDeCompra.add(oc);
        return oc;
    }

    public static OrdenDeCompra buscarOrdenDeCompra(String numeroOC) {
        for (OrdenDeCompra oc : ordenesDeCompra) {
            if (oc.getNumeroOC().equalsIgnoreCase(numeroOC)) {
                return oc;
            }
        }
        return null;
    }

    public static void agregarLineaOC(OrdenDeCompra oc, Item item, double cantidad, double precioAcordado) {
        LineaOrdenCompra linea = new LineaOrdenCompra(oc.getLineas().size() + 1, item, cantidad, precioAcordado);
        oc.agregarLinea(linea);
    }

    // Regla de límite de crédito: el módulo de Proveedores aporta la deuda
    // y se delega el cálculo y la transición de estado en la OC
    public static EstadoOrdenCompra confirmarOrdenDeCompra(OrdenDeCompra oc) {
        double deudaActual = ProveedorController.calcularDeudaProveedor(oc.getProveedor());
        return oc.confirmar(deudaActual);
    }

    public static boolean aprobarOrdenDeCompra(OrdenDeCompra oc, Usuario supervisor) {
        return oc.aprobar(supervisor);
    }

    public static boolean cancelarOrdenDeCompra(OrdenDeCompra oc) {
        return oc.cancelar();
    }

    public static List<OrdenDeCompra> getOrdenesDeCompra() {
        return ordenesDeCompra;
    }

    public static List<OrdenDeCompra> getOrdenesDeCompra(Proveedor proveedor) {
        List<OrdenDeCompra> resultado = new ArrayList<>();
        for (OrdenDeCompra oc : ordenesDeCompra) {
            if (oc.getProveedor() == proveedor) {
                resultado.add(oc);
            }
        }
        return resultado;
    }

    // OC que pueden recibir mercadería: emitidas o ya parcialmente recibidas
    public static List<OrdenDeCompra> getOrdenesRecibibles() {
        List<OrdenDeCompra> resultado = new ArrayList<>();
        for (OrdenDeCompra oc : ordenesDeCompra) {
            if (oc.getEstado() == EstadoOrdenCompra.EMITIDA
                    || oc.getEstado() == EstadoOrdenCompra.RECIBIDA_PARCIALMENTE) {
                resultado.add(oc);
            }
        }
        return resultado;
    }

    // Registra la recepción de una cantidad sobre una línea de la OC. Valida que la
    // cantidad sea positiva y no supere lo pendiente; suma stock si el ítem es Producto;
    // recalcula el estado de la OC (RECIBIDA_PARCIALMENTE / CERRADA).
    public static boolean registrarRecepcion(OrdenDeCompra oc, int idLinea, double cantidad) {
        if (oc == null || cantidad <= 0) {
            return false;
        }
        LineaOrdenCompra linea = null;
        for (LineaOrdenCompra l : oc.getLineas()) {
            if (l.getIdLineaOrdenCompra() == idLinea) {
                linea = l;
                break;
            }
        }
        if (linea == null || cantidad > linea.getCantidadPendiente()) {
            return false;
        }
        linea.registrarRecepcion(cantidad);
        if (linea.getItem() instanceof Producto) {
            ((Producto) linea.getItem()).incrementarStock(cantidad);
        }
        oc.evaluarYActualizarEstado();
        return true;
    }

    // Búsqueda con filtros para las consultas; null significa "sin filtro"
    public static List<OrdenDeCompra> buscarOrdenesDeCompra(EstadoOrdenCompra estadoFiltro, Rubro rubroFiltro, Proveedor proveedorFiltro) {
        List<OrdenDeCompra> resultado = new ArrayList<>();
        for (OrdenDeCompra oc : ordenesDeCompra) {
            boolean cumpleEstado = (estadoFiltro == null) || oc.getEstado() == estadoFiltro;
            boolean cumpleRubro = (rubroFiltro == null) || oc.incluyeRubro(rubroFiltro);
            boolean cumpleProveedor = (proveedorFiltro == null) || oc.getProveedor() == proveedorFiltro;
            if (cumpleEstado && cumpleRubro && cumpleProveedor) {
                resultado.add(oc);
            }
        }
        return resultado;
    }
}
