package mvc.view;

import mvc.model.Rubro;
import mvc.dto.RubroDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
public class AsociarProveedorRubroGUI extends JDialog{
    private JComboBox<Rubro> cbRubrosDisponibles;
    private JButton btnAgregarRubro,btnQuitarRubro;
    private JTable tablaRubrosAsociados;
    private DefaultTableModel modeloTabla;
    private String cuitProveedor;


    public AsociarProveedorRubroGUI(Frame owner, String cuitProveedor, String nombreProveedor) {
        super(owner, "Rubros del Proveedor: " + nombreProveedor, true);
        this.cuitProveedor = cuitProveedor;
        setSize(600, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        inicializarComponentes();

        new mvc.controller.AsociarProveedorRubroController(this);
    }

    private void inicializarComponentes() {
        // Panel superior para seleccionar y agregar
        JPanel panelNorte = new JPanel(new FlowLayout());
        panelNorte.add(new JLabel("Seleccionar Rubro:"));
        
        cbRubrosDisponibles = new JComboBox<>();
        panelNorte.add(cbRubrosDisponibles);

        btnAgregarRubro = new JButton("Agregar");
        panelNorte.add(btnAgregarRubro);

        btnQuitarRubro = new JButton("Quitar");
        panelNorte.add(btnQuitarRubro);

        add(panelNorte, BorderLayout.NORTH);

        // Tabla central para mostrar los asignados
        modeloTabla = new DefaultTableModel(new String[]{"Código", "Descripción"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaRubrosAsociados = new JTable(modeloTabla);
        add(new JScrollPane(tablaRubrosAsociados), BorderLayout.CENTER);
        
        // Un margen global
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public JButton getBtnAgregarRubro() { return btnAgregarRubro; }
    public JButton getBtnQuitarRubro() { return btnQuitarRubro; }
    public Rubro getRubroSeleccionado() { return (Rubro) cbRubrosDisponibles.getSelectedItem(); }
    public String getCuitProveedor() { return cuitProveedor; }

    public void cargarComboRubros(List<Rubro> todosLosRubros) {
        cbRubrosDisponibles.removeAllItems();
        for (Rubro r : todosLosRubros) {
            if (r.isActivo()) {
                cbRubrosDisponibles.addItem(r);
            }
        }
    }

    public void actualizarTabla(List<RubroDTO> rubrosDelProveedor) {
        modeloTabla.setRowCount(0);
        for (RubroDTO dto : rubrosDelProveedor) {
            modeloTabla.addRow(new Object[]{dto.getCodigo(), dto.getDescripcion()});
        }
    }

    public void mostrarMensaje(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

  
    public String getCodigoRubroSeleccionadoEnTabla() {
        int fila = tablaRubrosAsociados.getSelectedRow();
        if (fila != -1) {
            // Retorna el valor de la columna 0 (Código)
            return modeloTabla.getValueAt(fila, 0).toString(); 
        }
        return null;
    }
}
