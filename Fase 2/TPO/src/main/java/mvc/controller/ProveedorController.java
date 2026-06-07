package mvc.controller;
import mvc.dto.ProveedorDTO;
import mvc.enums.CondicionImpositiva;
import mvc.model.Proveedor;
import mvc.model.SistemaCompras;
import mvc.view.ProveedorGUI;
import mvc.view.AsociarProveedorRubroGUI;
import mvc.view.CertificadosProveedorGUI;
import mvc.view.PreciosAcordadosGUI;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.List;

public class ProveedorController {
    private ProveedorGUI vista;
    private SistemaCompras sistema;

    public ProveedorController(ProveedorGUI vista) {
        this.vista = vista;
        this.sistema = SistemaCompras.getInstance();

        // Eventos de botones
        this.vista.getBtnGuardar().addActionListener(e -> guardarProveedor());
        this.vista.getBtnModificar().addActionListener(e -> modificarProveedor());
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

            if (sistema.buscarProveedorPorCuit(cuit) != null) {
                vista.mostrarMensaje("Ya existe un proveedor con este CUIT.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            sistema.agregarProveedor(
                cuit, 
                razon, 
                razon,               
                "No especificado",
                tel, 
                correo, 
                cond, 
                "Pendiente",
                java.time.LocalDate.now(),
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
            Proveedor p = sistema.buscarProveedorPorCuit(cuit);
            if (p != null) {
                vista.setDatosFormulario(p.getCuit(), p.getRazonSocial(), p.getTelefono(), 
                                         p.getCorreoElectronico(), p.getCondicionImpositiva(), p.getLimiteDeudaAutorizado());
            }
        }
    }

    private void modificarProveedor() {
        try {
            String cuit = vista.getCuit(); 
            String razon = vista.getRazonSocial();
            String tel = vista.getTelefono();
            String correo = vista.getCorreo();
            double limite = Double.parseDouble(vista.getLimiteDeuda());
            CondicionImpositiva cond = vista.getCondicionSeleccionada();

            if (sistema.modificarProveedor(cuit, razon, tel, correo, cond, limite)) {
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
            sistema.cambiarEstadoProveedor(cuit);
            vista.limpiarFormulario();
            cargarTabla();
        }
    }

    private void cargarTabla() {
        List<ProveedorDTO> dtos = new ArrayList<>();
        for (Proveedor p : sistema.getProveedores()) {
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

    private void abrirVentanaRubros() {
        String cuit = vista.getCuitSeleccionadoEnTabla();
        if (cuit == null) {
            vista.mostrarMensaje("Por favor, seleccione un proveedor de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Proveedor proveedorActual = sistema.buscarProveedorPorCuit(cuit);
        
        // 1. Instanciamos el diálogo pasándole un Frame dummy como padre para que se centre
        AsociarProveedorRubroGUI dialog = new AsociarProveedorRubroGUI(null, proveedorActual.getRazonSocial());

        // 2. Llenamos el desplegable con TODOS los rubros del sistema
        dialog.cargarComboRubros(sistema.getRubros());

        // 3. Llenamos la tablita con los rubros que YA TIENE el proveedor
        refrescarTablaDialogo(dialog, proveedorActual);

        // 4. Le damos vida al botón "Agregar" dentro del diálogo
        dialog.getBtnAgregarRubro().addActionListener(e -> {
            mvc.model.Rubro rubroElegido = dialog.getRubroSeleccionado();
            if (rubroElegido != null) {
                boolean exito = sistema.asignarRubroAProveedor(cuit, rubroElegido.getCodigo());
                if (exito) {
                    // Si se agregó, refrescamos la tablita del diálogo
                    refrescarTablaDialogo(dialog, proveedorActual);
                } else {
                    dialog.mostrarMensaje("El proveedor ya tiene asignado este rubro.");
                }
            }
        });

        dialog.getBtnQuitarRubro().addActionListener(e -> {
            String codigoRubroSeleccionado = dialog.getCodigoRubroSeleccionadoEnTabla();
            
            if (codigoRubroSeleccionado == null) {
                dialog.mostrarMensaje("Por favor, seleccione un rubro de la tabla inferior para quitar.");
                return;
            }

            // Pedimos confirmación por seguridad
            int confirmacion = javax.swing.JOptionPane.showConfirmDialog(dialog, 
                "¿Estás seguro de que deseas desvincular este rubro del proveedor?", 
                "Confirmar", 
                javax.swing.JOptionPane.YES_NO_OPTION);

            if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
                // Llamamos al sistema para que haga el trabajo
                boolean exito = sistema.desvincularRubroDeProveedor(cuit, codigoRubroSeleccionado);
                
                if (exito) {
                    // Refrescamos la tabla para que el rubro desaparezca visualmente
                    refrescarTablaDialogo(dialog, proveedorActual);
                }
            }
        });
        
        // 5. Mostramos la ventana (El código se "pausa" aquí hasta que el usuario la cierra)
        dialog.setVisible(true);
    }

    // Método auxiliar para pintar la tablita del diálogo convirtiendo Modelos a DTOs
    private void refrescarTablaDialogo(AsociarProveedorRubroGUI dialog, Proveedor proveedor) {
        List<mvc.dto.RubroDTO> dtos = new ArrayList<>();
        for (mvc.model.Rubro r : proveedor.getRubrosAsociados()) {
            dtos.add(new mvc.dto.RubroDTO(r.getCodigo(), r.getDescripcion(), r.isActivo()));
        }
        dialog.actualizarTabla(dtos);
    }

    private void abrirVentanaCertificados() {
        String cuit = vista.getCuitSeleccionadoEnTabla();
        if (cuit == null) {
            vista.mostrarMensaje("Seleccione un proveedor de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Proveedor p = sistema.buscarProveedorPorCuit(cuit);
        // Abrimos el JDialog
        new CertificadosProveedorGUI(null, cuit, p.getRazonSocial()).setVisible(true);
    }

    private void abrirVentanaPrecios() {
        String cuit = vista.getCuitSeleccionadoEnTabla();
        if (cuit == null) {
            vista.mostrarMensaje("Seleccione un proveedor de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Proveedor p = sistema.buscarProveedorPorCuit(cuit);
        // Abrimos el JDialog
        new PreciosAcordadosGUI(null, cuit, p.getRazonSocial()).setVisible(true);
    }

}
