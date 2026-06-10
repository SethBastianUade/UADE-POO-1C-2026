package mvc.controller;
import mvc.dto.RubroDTO;
import mvc.model.Rubro;
import mvc.view.RubroGUI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class RubroController {
    // ============================================================
    // DATOS DEL MÓDULO: RUBROS (compartidos por toda la app)
    // ============================================================
    private static final List<Rubro> rubros = new ArrayList<>();
    private static int contadorIdRubros = 1;

    private RubroGUI vista;

    public RubroController(RubroGUI vista) {
        this.vista = vista;

        // Le decimos al botón qué debe hacer cuando lo hagan clic
        this.vista.getBtnGuardar().addActionListener(e -> guardarRubro());
        this.vista.getBtnModificar().addActionListener(e -> modificarRubroDesdeVista());
        this.vista.getBtnCambiarEstado().addActionListener(e -> cambiarEstadoDesdeVista());
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

        if (buscarRubro(codigo) != null) {
            vista.mostrarMensaje("Ya existe un rubro con ese código.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llamamos a la lógica de negocio del módulo
        agregarRubro(codigo, desc);

        // Limpiamos la vista y avisamos
        vista.limpiarFormulario();
        vista.mostrarMensaje("Rubro guardado exitosamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        // Refrescamos la tabla
        cargarRubrosEnVista();
    }
    private void cargarRubrosEnVista() {
        // 1. Pedimos los rubros al módulo
        List<Rubro> rubrosDelSistema = getRubros();

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
            Rubro rubro = buscarRubro(codigoSeleccionado);
            if (rubro != null) {
                // Pasamos los datos al formulario visual
                vista.setDatosFormulario(rubro.getCodigo(), rubro.getDescripcion());
            }
        }
    }
    private void modificarRubroDesdeVista() {
        String codigo = vista.getCodigoIngresado(); // Estaba bloqueado, así que es seguro leerlo
        String nuevaDesc = vista.getDescripcionIngresada();

        if (nuevaDesc.isEmpty()) {
            vista.mostrarMensaje("La descripción no puede estar vacía.", "Error", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (modificarRubro(codigo, nuevaDesc)) {
            vista.limpiarFormulario();
            vista.mostrarMensaje("Rubro modificado correctamente.", "Éxito", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            cargarRubrosEnVista();
        }
    }
    private void cambiarEstadoDesdeVista() {
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
            cambiarEstadoRubro(codigoSeleccionado);
            vista.limpiarFormulario();
            cargarRubrosEnVista();
        }
    }

    // ============================================================
    // LÓGICA DE NEGOCIO DEL MÓDULO (antes en SistemaCompras)
    // ============================================================
    public static List<Rubro> getRubros() {
        return rubros;
    }

    public static void agregarRubro(String codigo, String descripcion) {
        Rubro nuevoRubro = new Rubro(contadorIdRubros, codigo, descripcion);
        rubros.add(nuevoRubro);
        contadorIdRubros++;
    }

    public static Rubro buscarRubro(String codigo) {
        for (Rubro r : rubros) {
            if (r.getCodigo().equalsIgnoreCase(codigo)) {
                return r;
            }
        }
        return null; // Si no lo encuentra
    }

    public static boolean modificarRubro(String codigo, String nuevaDescripcion) {
        Rubro rubro = buscarRubro(codigo);
        if (rubro != null) {
            rubro.setDescripcion(nuevaDescripcion);
            return true;
        }
        return false;
    }

    public static boolean cambiarEstadoRubro(String codigo) {
        Rubro rubro = buscarRubro(codigo);
        if (rubro != null) {
            rubro.setActivo(!rubro.isActivo()); // Si es true pasa a false, y viceversa
            return true;
        }
        return false;
    }
}
