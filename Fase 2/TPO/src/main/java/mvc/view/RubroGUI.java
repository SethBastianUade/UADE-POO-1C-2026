package mvc.view;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import mvc.dto.RubroDTO;
import mvc.controller.RubroController;
import java.awt.*;
import java.util.List;

public class RubroGUI extends JInternalFrame {
    
    private JTextField txtCodigo;
    private JTextField txtDescripcion;
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnCambiarEstado;
    private JButton btnLimpiar;
    private DefaultTableModel modeloTabla;
    private JTable tablaRubros;

    public RubroGUI() {
        // Configuración de la ventanita interna (título, redimensionable, cerrable, maximizable, iconificable)
        super("Gestión de Rubros", true, true, true, true);
        setSize(800, 400);
        setLayout(new BorderLayout());
        inicializarFormulario();
        inicializarTabla();
        new RubroController(this);
    }

    private void inicializarFormulario() {
        // Panel superior para cargar datos
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelForm.add(new JLabel("Código del Rubro:"));
        txtCodigo = new JTextField();
        panelForm.add(txtCodigo);

        panelForm.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextField();
        panelForm.add(txtDescripcion);

        // Ponemos el botón en un panel abajo del formulario
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGuardar = new JButton("Guardar Rubro");
        btnModificar = new JButton("Guardar Cambios");
        btnCambiarEstado = new JButton("Activar / Desactivar");
        btnLimpiar = new JButton("Limpiar Formulario");

        
        btnModificar.setEnabled(false);

        panelBoton.add(btnLimpiar);
        panelBoton.add(btnCambiarEstado);
        panelBoton.add(btnModificar);
        panelBoton.add(btnGuardar);

        // Agrupamos el formulario y el botón en el Norte de la ventana
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelForm, BorderLayout.CENTER);
        panelNorte.add(panelBoton, BorderLayout.SOUTH);

        add(panelNorte, BorderLayout.NORTH);
    }

    private void inicializarTabla() {
        // Definimos las columnas
        String[] columnas = {"Código", "Descripción", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tablaRubros = new JTable(modeloTabla);
        tablaRubros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Lo metemos en un Scroll por si hay muchos rubros
        JScrollPane scrollPane = new JScrollPane(tablaRubros);
        add(scrollPane, BorderLayout.CENTER);
    }

    //Métodos para que el Controlador interactúe con la Vista
    public String getCodigoIngresado() {
        return txtCodigo.getText().trim();
    }
    public String getDescripcionIngresada() {
        return txtDescripcion.getText().trim();
    }
    public JButton getBtnGuardar() {
        return btnGuardar;
    }
    public void limpiarFormulario() {
        txtCodigo.setText("");
        txtDescripcion.setText("");
    }
    public void mostrarMensaje(String mensaje, String titulo, int tipoMensaje) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }
    public JButton getBtnModificar() { return btnModificar; }
    public JButton getBtnCambiarEstado() { return btnCambiarEstado; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JTable getTablaRubros() { return tablaRubros; }
    // Método clave para saber qué fila tocó el usuario
    public String getCodigoSeleccionadoEnTabla() {
        int fila = tablaRubros.getSelectedRow();
        if (fila != -1) {
            // Retorna el valor de la columna 0 (Código) de la fila seleccionada
            return modeloTabla.getValueAt(fila, 0).toString(); 
        }
        return null;
    }
    // Este método recibe los DTOs y pinta la tabla
    public void actualizarTabla(List<RubroDTO> rubrosDTO) {
        modeloTabla.setRowCount(0); // Borra las filas viejas

        // Bucle for sencillo (ideal para Junior)
        for (RubroDTO dto : rubrosDTO) {
            String estado = dto.isActivo() ? "Activo" : "Inactivo";
            modeloTabla.addRow(new Object[]{ dto.getCodigo(), dto.getDescripcion(), estado });
        }
    }
    public void setDatosFormulario(String codigo, String descripcion) {
        txtCodigo.setText(codigo);
        txtDescripcion.setText(descripcion);
        txtCodigo.setEnabled(false); // Bloqueamos el código al editar
        btnGuardar.setEnabled(false);
        btnModificar.setEnabled(true);
    }
}
