package mvc.controller;
import mvc.dto.ProveedorDTO;
import mvc.enums.CondicionImpositiva;
import mvc.enums.TipoImpuesto;
import mvc.model.CertificadoExclusion;
import mvc.model.Item;
import mvc.model.Proveedor;
import mvc.model.Rubro;
import mvc.view.ProveedorGUI;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProveedorController {
    // ============================================================
    // DATOS DEL MÓDULO: PROVEEDORES (compartidos por toda la app)
    // ============================================================
    private static final List<Proveedor> proveedores = new ArrayList<>();
    private static int contadorIdProveedores = 1;

    private ProveedorGUI vista;

    public ProveedorController(ProveedorGUI vista) {
        this.vista = vista;

        // Eventos de botones
        this.vista.getBtnGuardar().addActionListener(e -> guardarProveedor());
        this.vista.getBtnModificar().addActionListener(e -> modificarProveedorDesdeVista());
        this.vista.getBtnCambiarEstado().addActionListener(e -> cambiarEstado());
        this.vista.getBtnLimpiar().addActionListener(e -> vista.limpiarFormulario());
        this.vista.getBtnAsociarRubros().addActionListener(e -> abrirVentanaRubros());
        this.vista.getBtnCertificados().addActionListener(e -> abrirVentanaCertificados());
        this.vista.getBtnPreciosAcordados().addActionListener(e -> abrirVentanaPrecios());

        // Evento de selección en la tabla
        this.vista.getTablaProveedores().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && vista.getTablaProveedores().getSelectedRow() != -1) {
                    cargarDatosParaEdicion();
                }
            }
        });

        cargarTabla();
    }

    private void guardarProveedor() {
        try {
            String cuit = vista.getCuit();
            String razon = vista.getRazonSocial();
            String tel = vista.getTelefono();
            String correo = vista.getCorreo();
            double limite = Double.parseDouble(vista.getLimiteDeuda());
            CondicionImpositiva cond = vista.getCondicionSeleccionada();

            if (cuit.isEmpty() || razon.isEmpty()) {
                vista.mostrarMensaje("El CUIT y la Razón Social son obligatorios.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!Proveedor.cuitEsValido(cuit)) {
                vista.mostrarMensaje("El CUIT no es válido (formato o dígito verificador).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (buscarProveedorPorCuit(cuit) != null) {
                vista.mostrarMensaje("Ya existe un proveedor con este CUIT.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            agregarProveedor(
                cuit,
                razon,
                razon,
                "No especificado",
                tel,
                correo,
                cond,
                "Pendiente",
                LocalDate.now(),
                limite
            );
            vista.limpiarFormulario();
            vista.mostrarMensaje("Proveedor guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();

        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("El límite de deuda debe ser numérico.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosParaEdicion() {
        String cuit = vista.getCuitSeleccionadoEnTabla();
        if (cuit != null) {
            Proveedor p = buscarProveedorPorCuit(cuit);
            if (p != null) {
                vista.setDatosFormulario(p.getCuit(), p.getRazonSocial(), p.getTelefono(),
                                         p.getCorreoElectronico(), p.getCondicionImpositiva(), p.getLimiteDeudaAutorizado());
            }
        }
    }

    private void modificarProveedorDesdeVista() {
        try {
            String cuit = vista.getCuit();
            String razon = vista.getRazonSocial();
            String tel = vista.getTelefono();
            String correo = vista.getCorreo();
            double limite = Double.parseDouble(vista.getLimiteDeuda());
            CondicionImpositiva cond = vista.getCondicionSeleccionada();

            if (modificarProveedor(cuit, razon, tel, correo, cond, limite)) {
                vista.limpiarFormulario();
                vista.mostrarMensaje("Proveedor actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTabla();
            }
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("El límite de deuda debe ser numérico.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstado() {
        String cuit = vista.getCuitSeleccionadoEnTabla();
        if (cuit == null) {
            vista.mostrarMensaje("Seleccione un proveedor.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(vista, "¿Desea cambiar el estado de este proveedor?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            cambiarEstadoProveedor(cuit);
            vista.limpiarFormulario();
            cargarTabla();
        }
    }

    private void cargarTabla() {
        List<ProveedorDTO> dtos = new ArrayList<>();
        for (Proveedor p : getProveedores()) {
            dtos.add(new ProveedorDTO(
                p.getCuit(),
                p.getRazonSocial(),
                p.getCondicionImpositiva().toString(),
                p.getLimiteDeudaAutorizado(),
                p.isActivo()
            ));
        }
        vista.actualizarTabla(dtos);
    }

    // Los diálogos los crea la vista: el controller solo valida la selección
    // y le pide a la vista que los abra
    private void abrirVentanaRubros() {
        Proveedor p = getProveedorSeleccionado();
        if (p != null) {
            vista.abrirDialogoAsociarRubros(p.getCuit(), p.getRazonSocial());
        }
    }

    private void abrirVentanaCertificados() {
        Proveedor p = getProveedorSeleccionado();
        if (p != null) {
            vista.abrirDialogoCertificados(p.getCuit(), p.getRazonSocial());
        }
    }

    private void abrirVentanaPrecios() {
        Proveedor p = getProveedorSeleccionado();
        if (p != null) {
            vista.abrirDialogoPreciosAcordados(p.getCuit(), p.getRazonSocial());
        }
    }

    private Proveedor getProveedorSeleccionado() {
        String cuit = vista.getCuitSeleccionadoEnTabla();
        if (cuit == null) {
            vista.mostrarMensaje("Por favor, seleccione un proveedor de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return buscarProveedorPorCuit(cuit);
    }

    // ============================================================
    // LÓGICA DE NEGOCIO DEL MÓDULO (antes en SistemaCompras)
    // ============================================================
    public static void agregarProveedor(String cuit, String razonSocial, String nombreComercial, String domicilio, String telefono,
                                        String correo, CondicionImpositiva condicion, String nroInscripcionIIBB,
                                        LocalDate fechaInicioActividades, double limite) {
        Proveedor p = new Proveedor(contadorIdProveedores++, cuit, razonSocial, nombreComercial,
                domicilio, telefono, correo, condicion, nroInscripcionIIBB, fechaInicioActividades, limite);
        proveedores.add(p);
    }

    public static Proveedor buscarProveedorPorCuit(String cuit) {
        for (Proveedor p : proveedores) {
            if (p.getCuit().equals(cuit)) {
                return p;
            }
        }
        return null;
    }

    public static boolean modificarProveedor(String cuit, String razonSocial, String telefono,
                                             String correo, CondicionImpositiva condicion, double limite) {
        Proveedor p = buscarProveedorPorCuit(cuit);
        if (p != null) {
            p.setRazonSocial(razonSocial);
            p.setTelefono(telefono);
            p.setCorreoElectronico(correo);
            p.setCondicionImpositiva(condicion);
            p.setLimiteDeudaAutorizado(limite);
            return true;
        }
        return false;
    }

    public static boolean cambiarEstadoProveedor(String cuit) {
        Proveedor p = buscarProveedorPorCuit(cuit);
        if (p != null) {
            p.setActivo(!p.isActivo());
            return true;
        }
        return false;
    }

    public static List<Proveedor> getProveedores() {
        return proveedores;
    }

    public static boolean asignarRubroAProveedor(String cuitProveedor, String codigoRubro) {
        Proveedor p = buscarProveedorPorCuit(cuitProveedor);
        Rubro r = RubroController.buscarRubro(codigoRubro);

        if (p != null && r != null) {
            return p.agregarRubro(r);
        }
        return false;
    }

    public static boolean desvincularRubroDeProveedor(String cuitProveedor, String codigoRubro) {
        Proveedor p = buscarProveedorPorCuit(cuitProveedor);
        Rubro r = RubroController.buscarRubro(codigoRubro);

        if (p != null && r != null) {
            return p.quitarRubro(r);
        }
        return false;
    }

    public static void agregarCertificadoAProveedor(String cuit, String numero, TipoImpuesto tipo, LocalDate desde, LocalDate hasta) {
        Proveedor p = buscarProveedorPorCuit(cuit);
        if (p != null) {
            // Un id autoincremental simple para el certificado
            int idCert = p.getCertificados().size() + 1;
            CertificadoExclusion nuevo = new CertificadoExclusion(idCert, numero, tipo, desde, hasta);
            p.agregarCertificado(nuevo);
        }
    }

    public static void registrarPrecioAcordado(String cuit, String codigoItem, double precio) {
        Proveedor p = buscarProveedorPorCuit(cuit);
        Item itemEncontrado = ItemController.buscarItemPorCodigo(codigoItem);

        if (p != null && itemEncontrado != null) {
            p.acordarPrecioItem(itemEncontrado, precio);
        }
    }

    public static List<Proveedor> getProveedoresQueSuministran(Item item) {
        List<Proveedor> resultado = new ArrayList<>();
        for (Proveedor p : proveedores) {
            if (p.getPrecioAcordadoPara(item) != null) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    // Atajo para los controllers: delega el cálculo en el Proveedor (dueño de la regla);
    // los documentos los aporta el módulo de Documentos Comerciales
    public static double calcularDeudaProveedor(Proveedor proveedor) {
        return proveedor.calcularDeudaActual(DocumentoComercialController.getDocumentosComerciales());
    }
}
