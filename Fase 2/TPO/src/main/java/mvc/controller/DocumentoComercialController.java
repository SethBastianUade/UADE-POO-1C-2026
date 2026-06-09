package mvc.controller;

import mvc.dto.DocumentoComercialDTO;
import mvc.dto.ItemDTO;
import mvc.dto.LineaDocumentoDTO;
import mvc.dto.ProveedorDTO;
import mvc.enums.EstadoRegistroDocumento;
import mvc.model.DocumentoComercial;
import mvc.model.Factura;
import mvc.model.Item;
import mvc.model.LineaDocumento;
import mvc.model.Proveedor;
import mvc.model.SistemaCompras;
import mvc.model.Usuario;
import mvc.view.DocumentoComercialGUI;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DocumentoComercialController {
    private DocumentoComercialGUI vista;
    private SistemaCompras sistema;
    // Líneas cargadas en el formulario, a la espera del "Registrar documento"
    private List<LineaDocumento> lineasPendientes = new ArrayList<>();

    public DocumentoComercialController(DocumentoComercialGUI vista) {
        this.vista = vista;
        this.sistema = SistemaCompras.getInstance();

        this.vista.getBtnAgregarLinea().addActionListener(e -> agregarLineaPendiente());
        this.vista.getBtnRegistrar().addActionListener(e -> registrarDocumento());
        this.vista.getBtnAprobar().addActionListener(e -> aprobarDocumento());
        this.vista.getCbProveedor().addActionListener(e -> cargarComboFacturas());

        this.vista.getTablaDocumentos().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalleSeleccion();
            }
        });

        cargarCombos();
        cargarTabla();
    }

    private void agregarLineaPendiente() {
        String codigoItem = vista.getCodigoItemSeleccionado();
        if (codigoItem == null) {
            vista.mostrarMensaje("Seleccione un ítem.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            double cantidad = Double.parseDouble(vista.getCantidad());
            double precio = Double.parseDouble(vista.getPrecioUnitario());
            double alicuota = Double.parseDouble(vista.getAlicuota());
            if (cantidad <= 0 || precio <= 0 || alicuota < 0) {
                vista.mostrarMensaje("Cantidad y precio deben ser mayores a cero; la alícuota no puede ser negativa.",
                        "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Item item = sistema.buscarItemPorCodigo(codigoItem);
            lineasPendientes.add(new LineaDocumento(lineasPendientes.size() + 1, item, cantidad, precio, alicuota));
            vista.limpiarFormularioLinea();
            // La sub-tabla pasa a mostrar las líneas pendientes del documento en carga
            vista.getTablaDocumentos().clearSelection();
            mostrarLineasPendientes();
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("Cantidad, precio y alícuota deben ser numéricos.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarDocumento() {
        // 1. Validaciones comunes de cabecera
        String cuit = vista.getCuitProveedorSeleccionado();
        if (cuit == null) {
            vista.mostrarMensaje("Seleccione un proveedor.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String numero = vista.getNumeroDocumento();
        if (numero.isEmpty()) {
            vista.mostrarMensaje("El número de documento es obligatorio.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (sistema.buscarDocumentoComercial(numero) != null) {
            vista.mostrarMensaje("Ya existe un documento con ese número.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        LocalDate fecha;
        double importe;
        try {
            fecha = LocalDate.parse(vista.getFechaEmision());
        } catch (DateTimeParseException ex) {
            vista.mostrarMensaje("Fecha de emisión inválida (use AAAA-MM-DD).", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            importe = Double.parseDouble(vista.getImporteTotal());
            if (importe <= 0) {
                vista.mostrarMensaje("El importe total debe ser mayor a cero.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("El importe total debe ser numérico.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Proveedor proveedor = sistema.buscarProveedorPorCuit(cuit);
        String tipo = vista.getTipoSeleccionado();

        // 2. Registro según el tipo
        if (tipo.equals("FACTURA")) {
            registrarFactura(proveedor, numero, fecha, importe);
        } else {
            registrarNota(tipo, proveedor, numero, fecha, importe);
        }
    }

    private void registrarFactura(Proveedor proveedor, String numero, LocalDate fecha, double importe) {
        String cae = vista.getCae();
        if (cae.isEmpty()) {
            vista.mostrarMensaje("El CAE es obligatorio para una factura.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (lineasPendientes.isEmpty()) {
            vista.mostrarMensaje("Agregue al menos una línea: sin líneas no se puede validar la factura contra las OC.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate fechaCae;
        double baseIVA, montoIVA;
        try {
            fechaCae = LocalDate.parse(vista.getFechaVencimientoCae());
        } catch (DateTimeParseException ex) {
            vista.mostrarMensaje("Vencimiento de CAE inválido (use AAAA-MM-DD).", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            baseIVA = Double.parseDouble(vista.getBaseIVA());
            montoIVA = Double.parseDouble(vista.getMontoIVA());
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("La base imponible y el monto de IVA deben ser numéricos.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Factura factura = sistema.registrarFactura(proveedor, numero, fecha, importe, cae, fechaCae, baseIVA, montoIVA);
        volcarLineasPendientes(factura);

        // Regla crítica: validación de amparo con OC + control de precios
        EstadoRegistroDocumento resultado = sistema.validarDocumentoConOC(factura);
        if (resultado == EstadoRegistroDocumento.APROBADO) {
            vista.mostrarMensaje("Factura " + numero + " registrada y APROBADA:\n"
                    + "todos los ítems están amparados por OC emitidas con precios coincidentes.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            vista.mostrarMensaje("Factura " + numero + " registrada como OBSERVADA:\n"
                    + "hay ítems sin OC emitida del proveedor que los ampare,\n"
                    + "o con precio distinto al acordado en la OC.\n"
                    + "Requiere aprobación de un SUPERVISOR.",
                    "Documento observado", JOptionPane.WARNING_MESSAGE);
        }
        finalizarRegistro(numero);
    }

    private void registrarNota(String tipo, Proveedor proveedor, String numero, LocalDate fecha, double importe) {
        String motivo = vista.getMotivo();
        if (motivo.isEmpty()) {
            vista.mostrarMensaje("El motivo es obligatorio para una nota de débito/crédito.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String numeroFactura = vista.getNumeroFacturaOrigen();
        if (numeroFactura == null) {
            vista.mostrarMensaje("El proveedor no tiene facturas registradas: no se puede asociar la nota a una factura de origen.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Factura facturaOrigen = (Factura) sistema.buscarDocumentoComercial(numeroFactura);

        DocumentoComercial doc;
        if (tipo.equals("NOTA_DE_DEBITO")) {
            doc = sistema.registrarNotaDeDebito(proveedor, numero, fecha, importe, motivo, facturaOrigen);
        } else {
            doc = sistema.registrarNotaDeCredito(proveedor, numero, fecha, importe, motivo, facturaOrigen);
        }
        volcarLineasPendientes(doc);
        vista.mostrarMensaje(doc.getTipoDocumento() + " " + numero + " registrada (vinculada a la factura "
                + numeroFactura + ").", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        finalizarRegistro(numero);
    }

    private void volcarLineasPendientes(DocumentoComercial doc) {
        for (LineaDocumento linea : lineasPendientes) {
            sistema.agregarLineaDocumento(doc, linea.getItem(), linea.getCantidad(),
                    linea.getPrecioUnitario(), linea.getAlicuotaIVA());
        }
        lineasPendientes.clear();
    }

    private void finalizarRegistro(String numeroDocumento) {
        vista.limpiarFormulario();
        cargarTabla();
        cargarComboFacturas(); // si fue una factura, ya puede ser origen de ND/NC
        vista.seleccionarDocumento(numeroDocumento);
    }

    private void aprobarDocumento() {
        DocumentoComercial doc = getDocumentoSeleccionado();
        if (doc == null || doc.getEstadoRegistro() != EstadoRegistroDocumento.OBSERVADO) {
            vista.mostrarMensaje("Seleccione un documento en estado OBSERVADO.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Usuario usuario = sistema.getUsuarioLogueado();
        if (usuario == null || !usuario.tienePermiso(Usuario.APROBAR_DOCUMENTO)) {
            vista.mostrarMensaje("No tiene permisos para aprobar documentos.\nSe requiere rol SUPERVISOR.",
                    "Permiso denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (sistema.aprobarDocumento(doc, usuario)) {
            vista.mostrarMensaje("Documento " + doc.getNumeroDocumento() + " APROBADO.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
            vista.seleccionarDocumento(doc.getNumeroDocumento());
        }
    }

    // Muestra las líneas del documento seleccionado y habilita "Aprobar" solo si está OBSERVADO
    private void mostrarDetalleSeleccion() {
        DocumentoComercial doc = getDocumentoSeleccionado();
        if (doc == null) {
            mostrarLineasPendientes();
            vista.habilitarAprobar(false);
            return;
        }
        vista.actualizarTablaLineas(convertirLineas(doc.getLineas()));
        vista.habilitarAprobar(doc.getEstadoRegistro() == EstadoRegistroDocumento.OBSERVADO);
    }

    private void mostrarLineasPendientes() {
        vista.actualizarTablaLineas(convertirLineas(lineasPendientes));
    }

    private List<LineaDocumentoDTO> convertirLineas(List<LineaDocumento> lineas) {
        List<LineaDocumentoDTO> dtos = new ArrayList<>();
        for (LineaDocumento linea : lineas) {
            dtos.add(new LineaDocumentoDTO(
                    linea.getItem().getCodigo(),
                    linea.getItem().getDescripcion(),
                    linea.getCantidad(),
                    linea.getPrecioUnitario(),
                    linea.getAlicuotaIVA(),
                    linea.getSubtotal()
            ));
        }
        return dtos;
    }

    private DocumentoComercial getDocumentoSeleccionado() {
        String numero = vista.getNumeroDocumentoSeleccionado();
        return (numero != null) ? sistema.buscarDocumentoComercial(numero) : null;
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

        cargarComboFacturas();
    }

    // El combo de facturas de origen depende del proveedor elegido
    private void cargarComboFacturas() {
        String cuit = vista.getCuitProveedorSeleccionado();
        List<String> numeros = new ArrayList<>();
        if (cuit != null) {
            Proveedor p = sistema.buscarProveedorPorCuit(cuit);
            for (Factura f : sistema.getFacturas(p)) {
                numeros.add(f.getNumeroDocumento());
            }
        }
        vista.cargarComboFacturas(numeros);
    }

    private void cargarTabla() {
        List<DocumentoComercialDTO> dtos = new ArrayList<>();
        for (DocumentoComercial doc : sistema.getDocumentosComerciales()) {
            dtos.add(new DocumentoComercialDTO(
                    doc.getNumeroDocumento(),
                    doc.getTipoDocumento(),
                    doc.getProveedor().getRazonSocial(),
                    doc.getFechaEmision().toString(),
                    doc.getImporteTotal(),
                    doc.getSaldoPendiente(),
                    doc.getEstadoCancelacion().toString(),
                    doc.getEstadoRegistro().toString()
            ));
        }
        vista.actualizarTabla(dtos);
    }
}
