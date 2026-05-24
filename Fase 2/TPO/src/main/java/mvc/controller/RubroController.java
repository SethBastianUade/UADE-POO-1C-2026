package mvc.controller;
import mvc.dto.RubroDTO;
import mvc.model.Rubro;
import mvc.view.RubroGUI;
import mvc.model.SistemaCompras;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RubroController {

    private RubroGUI vista;
    private SistemaCompras sistema;

    public RubroController(RubroGUI vista) {
        this.vista = vista;
        // Obtenemos la instancia global
        this.sistema = SistemaCompras.getInstance();

        // Le decimos al botón qué debe hacer cuando lo hagan clic
        this.vista.getBtnGuardar().addActionListener(e -> guardarRubro());
        this.vista.getBtnModificar().addActionListener(e -> modificarRubro());
        this.vista.getBtnCambiarEstado().addActionListener(e -> cambiarEstadoRubro());
        this.vista.getBtnLimpiar().addActionListener(e -> vista.limpiarFormulario());
        
        // 2. Asignar Evento a la Tabla (Detectar cuando hacen clic en una fila)
        this.vista.getTablaRubros().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // if(!e.getValueIsAdjusting()) evita que el evento se dispare dos veces por un solo clic
                if (!e.getValueIsAdjusting() && vista.getTablaRubros().getSelectedRow() != -1) {
                    cargarDatosParaEdicion();
                }
            }
        });
        // Cargamos la tabla por primera vez al abrir
        cargarRubrosEnVista();
    }

    private void guardarRubro() {
        String codigo = vista.getCodigoIngresado();
        String desc = vista.getDescripcionIngresada();

        // Validación simple
        if (codigo.isEmpty() || desc.isEmpty()) {
            vista.mostrarMensaje("Por favor, completa todos los campos.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (sistema.buscarRubro(codigo) != null) {
            vista.mostrarMensaje("Ya existe un rubro con ese código.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llamamos a la lógica de negocio (Singleton)
        sistema.agregarRubro(codigo, desc);

        // Limpiamos la vista y avisamos
        vista.limpiarFormulario();
        vista.mostrarMensaje("Rubro guardado exitosamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        // Refrescamos la tabla
        cargarRubrosEnVista();
    }
    private void cargarRubrosEnVista() {
        // 1. Pedimos los rubros al sistema
        List<Rubro> rubrosDelSistema = sistema.getRubros();
        
        // 2. Los convertimos a DTOs (para no pasarle el Modelo directo a la Vista)
        List<RubroDTO> listaDTOs = new ArrayList<>();
        for (Rubro r : rubrosDelSistema) {
            listaDTOs.add(new RubroDTO(r.getCodigo(), r.getDescripcion(), r.isActivo()));
        }

        // 3. Se los mandamos a la tabla para que los pinte
        vista.actualizarTabla(listaDTOs);
    }
    private void cargarDatosParaEdicion() {
        String codigoSeleccionado = vista.getCodigoSeleccionadoEnTabla();
        if (codigoSeleccionado != null) {
            Rubro rubro = sistema.buscarRubro(codigoSeleccionado);
            if (rubro != null) {
                // Pasamos los datos al formulario visual
                vista.setDatosFormulario(rubro.getCodigo(), rubro.getDescripcion());
            }
        }
    }
    private void modificarRubro() {
        String codigo = vista.getCodigoIngresado(); // Estaba bloqueado, así que es seguro leerlo
        String nuevaDesc = vista.getDescripcionIngresada();

        if (nuevaDesc.isEmpty()) {
            vista.mostrarMensaje("La descripción no puede estar vacía.", "Error", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (sistema.modificarRubro(codigo, nuevaDesc)) {
            vista.limpiarFormulario();
            vista.mostrarMensaje("Rubro modificado correctamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            cargarRubrosEnVista();
        }
    }
    private void cambiarEstadoRubro() {
        String codigoSeleccionado = vista.getCodigoSeleccionadoEnTabla();
        
        if (codigoSeleccionado == null) {
            vista.mostrarMensaje("Por favor, selecciona un rubro de la tabla.", "Atención", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Pedimos confirmación al usuario
        int confirmacion = javax.swing.JOptionPane.showConfirmDialog(vista, 
            "¿Estás seguro de cambiar el estado de este rubro?", "Confirmar", 
            javax.swing.JOptionPane.YES_NO_OPTION);

        if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
            sistema.cambiarEstadoRubro(codigoSeleccionado);
            vista.limpiarFormulario();
            cargarRubrosEnVista();
        }
    }
}


