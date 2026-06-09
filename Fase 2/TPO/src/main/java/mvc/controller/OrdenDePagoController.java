package mvc.controller;

import mvc.dto.DocumentoComercialDTO;
import mvc.dto.MedioDePagoDTO;
import mvc.dto.OrdenDePagoDTO;
import mvc.dto.ProveedorDTO;
import mvc.dto.RetencionDTO;
import mvc.enums.TipoImpuesto;
import mvc.model.ChequeDeTerceros;
import mvc.model.ChequePropio;
import mvc.model.DocumentoComercial;
import mvc.model.DocumentoPago;
import mvc.model.Efectivo;
import mvc.model.MedioDePago;
import mvc.model.OrdenDePago;
import mvc.model.Proveedor;
import mvc.model.Retencion;
import mvc.model.SistemaCompras;
import mvc.model.TransferenciaBancaria;
import mvc.model.Usuario;
import mvc.view.OrdenDePagoGUI;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class OrdenDePagoController {
    // Tolerancia para comparar importes en double (1 centavo)
    private static final double TOLERANCIA = 0.01;

    private OrdenDePagoGUI vista;
    private SistemaCompras sistema;
    // Medios de pago cargados para la OP en construcción
    private List<MedioDePago> mediosPendientes = new ArrayList<>();
    private int contadorIdMedios = 1;
    // Evita que el recálculo dispare nuevos eventos de la tabla en cascada
    private boolean recalculando = false;

    public OrdenDePagoController(OrdenDePagoGUI vista) {
        this.vista = vista;
        this.sistema = SistemaCompras.getInstance();

        this.vista.getCbProveedor().addActionListener(e -> cambiarProveedor());
        this.vista.getBtnAgregarMedio().addActionListener(e -> agregarMedioDePago());
        this.vista.getBtnQuitarMedio().addActionListener(e -> quitarMedioDePago());
        this.vista.getBtnEmitir().addActionListener(e -> emitirOP());

        // Cada cambio en la tabla (marcar documento o editar monto) recalcula todo
        this.vista.getTablaDocumentos().getModel().addTableModelListener(e -> {
            if (recalculando) {
                return;
            }
            recalculando = true;
            try {
                proponerMontoPorDefecto(e.getFirstRow(), e.getColumn());
                recalcularTotales();
            } finally {
                recalculando = false;
            }
        });

        cargarComboProveedores();
        cambiarProveedor();
    }

    // Al marcar un documento sin monto cargado, propone pagar su saldo completo
    private void proponerMontoPorDefecto(int fila, int columna) {
        if (columna != 0 || fila < 0) {
            return;
        }
        if (vista.isDocumentoIncluidoEnFila(fila) && vista.getMontoAplicarEnFila(fila).isEmpty()) {
            DocumentoComercial doc = sistema.buscarDocumentoComercial(vista.getNumeroDocumentoEnFila(fila));
            if (doc != null) {
                vista.setMontoAplicarEnFila(fila, doc.getSaldoPendiente());
            }
        }
    }

    private void cambiarProveedor() {
        // La OP en construcción se descarta al cambiar de proveedor
        mediosPendientes.clear();
        contadorIdMedios = 1;
        vista.actualizarTablaMedios(new ArrayList<>());
        vista.limpiarFormularioMedio();
        cargarDocumentosPendientes();
        cargarHistorial();
        recalcularTotales();
    }

    private Proveedor getProveedorSeleccionado() {
        String cuit = vista.getCuitProveedorSeleccionado();
        return (cuit != null) ? sistema.buscarProveedorPorCuit(cuit) : null;
    }

    // Suma los montos a aplicar de los documentos marcados (los inválidos cuentan como 0;
    // se rechazan recién al emitir, para no molestar mientras se tipea)
    private double calcularBrutoSeleccionado() {
        double bruto = 0;
        for (int fila = 0; fila < vista.getCantidadFilasDocumentos(); fila++) {
            if (vista.isDocumentoIncluidoEnFila(fila)) {
                try {
                    bruto += Double.parseDouble(vista.getMontoAplicarEnFila(fila));
                } catch (NumberFormatException ex) {
                    // todavía no es un número válido
                }
            }
        }
        return bruto;
    }

    // Recalcula la vista previa de retenciones y los totales en cada cambio
    private void recalcularTotales() {
        Proveedor proveedor = getProveedorSeleccionado();
        double bruto = calcularBrutoSeleccionado();

        List<RetencionDTO> dtos = new ArrayList<>();
        double retenido = 0;
        if (proveedor != null && bruto > 0) {
            List<Retencion> retenciones = sistema.calcularRetenciones(proveedor, bruto);
            // La vista previa muestra los tres impuestos, incluso los excluidos o sin alícuota
            for (TipoImpuesto tipo : TipoImpuesto.values()) {
                if (proveedor.tieneExclusionActiva(tipo, LocalDate.now())) {
                    dtos.add(new RetencionDTO(tipo.toString(), bruto, 0, 0, "Sí (no se retiene)"));
                } else {
                    Retencion retencion = buscarRetencionPorTipo(retenciones, tipo);
                    if (retencion != null) {
                        dtos.add(new RetencionDTO(tipo.toString(), retencion.getBaseImponible(),
                                retencion.getPorcentaje(), retencion.getMontoRetenido(), "No"));
                        retenido += retencion.getMontoRetenido();
                    } else {
                        dtos.add(new RetencionDTO(tipo.toString(), bruto, 0, 0, "No"));
                    }
                }
            }
        }
        vista.actualizarTablaRetenciones(dtos);

        double medios = 0;
        for (MedioDePago medio : mediosPendientes) {
            medios += medio.getImporte();
        }
        vista.setTotales(bruto, retenido, bruto - retenido, medios);
    }

    private Retencion buscarRetencionPorTipo(List<Retencion> retenciones, TipoImpuesto tipo) {
        for (Retencion r : retenciones) {
            if (r.getTipoImpuesto() == tipo) {
                return r;
            }
        }
        return null;
    }

    private void agregarMedioDePago() {
        double importe;
        try {
            importe = Double.parseDouble(vista.getImporteMedio());
            if (importe <= 0) {
                vista.mostrarMensaje("El importe del medio de pago debe ser mayor a cero.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("El importe del medio de pago debe ser numérico.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tipo = vista.getTipoMedioSeleccionado();
        MedioDePago medio;
        if (tipo.equals("EFECTIVO")) {
            medio = new Efectivo(contadorIdMedios++, importe);
        } else if (tipo.equals("TRANSFERENCIA_BANCARIA")) {
            if (vista.getNroReferencia().isEmpty() || vista.getCuentaOrigen().isEmpty()) {
                vista.mostrarMensaje("Complete N° de referencia y cuenta de origen.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            medio = new TransferenciaBancaria(contadorIdMedios++, importe, vista.getNroReferencia(), vista.getCuentaOrigen());
        } else {
            if (vista.getNroCheque().isEmpty() || vista.getBanco().isEmpty() || vista.getFirmante().isEmpty()) {
                vista.mostrarMensaje("Complete N° de cheque, banco y firmante.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LocalDate emision, vencimiento;
            try {
                emision = LocalDate.parse(vista.getFechaEmisionCheque());
                vencimiento = LocalDate.parse(vista.getFechaVencimientoCheque());
            } catch (DateTimeParseException ex) {
                vista.mostrarMensaje("Fechas del cheque inválidas (use AAAA-MM-DD).", "Error de formato", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (tipo.equals("CHEQUE_PROPIO")) {
                medio = new ChequePropio(contadorIdMedios++, importe, vista.getNroCheque(), vista.getBanco(),
                        emision, vencimiento, vista.getFirmante());
            } else {
                medio = new ChequeDeTerceros(contadorIdMedios++, importe, vista.getNroCheque(), vista.getBanco(),
                        emision, vencimiento, vista.getFirmante());
            }
        }

        mediosPendientes.add(medio);
        vista.limpiarFormularioMedio();
        refrescarTablaMedios();
        recalcularTotales();
    }

    private void quitarMedioDePago() {
        int fila = vista.getFilaMedioSeleccionada();
        if (fila == -1) {
            vista.mostrarMensaje("Seleccione un medio de pago de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mediosPendientes.remove(fila);
        refrescarTablaMedios();
        recalcularTotales();
    }

    private void refrescarTablaMedios() {
        List<MedioDePagoDTO> dtos = new ArrayList<>();
        for (MedioDePago medio : mediosPendientes) {
            dtos.add(new MedioDePagoDTO(medio.getTipo(), describirMedio(medio), medio.getImporte()));
        }
        vista.actualizarTablaMedios(dtos);
    }

    private String describirMedio(MedioDePago medio) {
        if (medio instanceof TransferenciaBancaria) {
            TransferenciaBancaria t = (TransferenciaBancaria) medio;
            return "Ref: " + t.getNroReferencia() + " | Cta: " + t.getCuentaOrigen();
        }
        if (medio instanceof ChequePropio) {
            ChequePropio c = (ChequePropio) medio;
            return "N° " + c.getNroCheque() + " | " + c.getBanco() + " | Vto: " + c.getFechaVencimiento();
        }
        if (medio instanceof ChequeDeTerceros) {
            ChequeDeTerceros c = (ChequeDeTerceros) medio;
            return "N° " + c.getNroCheque() + " | " + c.getBanco() + " | Vto: " + c.getFechaVencimiento();
        }
        return "-";
    }

    private void emitirOP() {
        Usuario operador = sistema.getUsuarioLogueado();
        if (operador == null) {
            vista.mostrarMensaje("Debe iniciar sesión para emitir una OP.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Proveedor proveedor = getProveedorSeleccionado();
        if (proveedor == null) {
            vista.mostrarMensaje("Seleccione un proveedor.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Armar los DocumentoPago a partir de los documentos marcados
        List<DocumentoPago> documentosPago = new ArrayList<>();
        for (int fila = 0; fila < vista.getCantidadFilasDocumentos(); fila++) {
            if (!vista.isDocumentoIncluidoEnFila(fila)) {
                continue;
            }
            String numero = vista.getNumeroDocumentoEnFila(fila);
            DocumentoComercial doc = sistema.buscarDocumentoComercial(numero);
            double monto;
            try {
                monto = Double.parseDouble(vista.getMontoAplicarEnFila(fila));
            } catch (NumberFormatException ex) {
                vista.mostrarMensaje("El monto a aplicar del documento " + numero + " no es numérico.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (monto <= 0) {
                vista.mostrarMensaje("El monto a aplicar del documento " + numero + " debe ser mayor a cero.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (monto > doc.getSaldoPendiente() + TOLERANCIA) {
                vista.mostrarMensaje(String.format("El monto a aplicar del documento %s ($%.2f) supera su saldo pendiente ($%.2f).",
                        numero, monto, doc.getSaldoPendiente()), "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            documentosPago.add(new DocumentoPago(doc, monto));
        }
        if (documentosPago.isEmpty()) {
            vista.mostrarMensaje("Marque al menos un documento a pagar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Verificar que los medios de pago cubran exactamente el neto
        double bruto = 0;
        for (DocumentoPago dp : documentosPago) {
            bruto += dp.getMontoAplicado();
        }
        double retenido = 0;
        for (Retencion r : sistema.calcularRetenciones(proveedor, bruto)) {
            retenido += r.getMontoRetenido();
        }
        double neto = bruto - retenido;
        double totalMedios = 0;
        for (MedioDePago medio : mediosPendientes) {
            totalMedios += medio.getImporte();
        }
        if (mediosPendientes.isEmpty() || Math.abs(totalMedios - neto) > TOLERANCIA) {
            vista.mostrarMensaje(String.format("Los medios de pago ($%.2f) deben cubrir exactamente el neto a pagar ($%.2f).",
                    totalMedios, neto), "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Emitir: el sistema calcula las retenciones definitivas y aplica los pagos
        OrdenDePago op = sistema.emitirOrdenDePago(proveedor, documentosPago, mediosPendientes, operador);

        vista.mostrarMensaje(String.format(
                "OP %s emitida correctamente.%n%nTotal bruto: $%.2f%nTotal retenido: $%.2f%nNeto pagado: $%.2f%n%n"
                        + "Los documentos asociados actualizaron su estado de cancelación.",
                op.getNumeroOP(), op.getTotalBrutoPagado(), op.getTotalRetenido(), op.getTotalNetoPagar()),
                "Éxito", JOptionPane.INFORMATION_MESSAGE);

        // 4. Resetear la pantalla para una nueva OP
        mediosPendientes = new ArrayList<>(); // la lista anterior quedó dentro de la OP emitida
        contadorIdMedios = 1;
        refrescarTablaMedios();
        cargarDocumentosPendientes();
        cargarHistorial();
        recalcularTotales();
    }

    private void cargarComboProveedores() {
        List<ProveedorDTO> proveedores = new ArrayList<>();
        for (Proveedor p : sistema.getProveedores()) {
            if (p.isActivo()) {
                proveedores.add(new ProveedorDTO(p.getCuit(), p.getRazonSocial(),
                        p.getCondicionImpositiva().toString(), p.getLimiteDeudaAutorizado(), p.isActivo()));
            }
        }
        vista.cargarComboProveedores(proveedores);
    }

    private void cargarDocumentosPendientes() {
        Proveedor proveedor = getProveedorSeleccionado();
        List<DocumentoComercialDTO> dtos = new ArrayList<>();
        if (proveedor != null) {
            for (DocumentoComercial doc : sistema.getDocumentosPendientes(proveedor)) {
                dtos.add(new DocumentoComercialDTO(
                        doc.getNumeroDocumento(), doc.getTipoDocumento(), doc.getProveedor().getRazonSocial(),
                        doc.getFechaEmision().toString(), doc.getImporteTotal(), doc.getSaldoPendiente(),
                        doc.getEstadoCancelacion().toString(), doc.getEstadoRegistro().toString()
                ));
            }
        }
        vista.cargarDocumentosPendientes(dtos);
    }

    private void cargarHistorial() {
        Proveedor proveedor = getProveedorSeleccionado();
        List<OrdenDePagoDTO> dtos = new ArrayList<>();
        if (proveedor != null) {
            for (OrdenDePago op : sistema.getOrdenesDePago(proveedor)) {
                dtos.add(new OrdenDePagoDTO(
                        op.getNumeroOP(), op.getProveedor().getRazonSocial(), op.getFechaEmision().toString(),
                        op.getTotalBrutoPagado(), op.getTotalRetenido(), op.getTotalNetoPagar(),
                        op.getOperadorCreador().getNombreUsuario()
                ));
            }
        }
        vista.actualizarTablaOP(dtos);
    }
}
