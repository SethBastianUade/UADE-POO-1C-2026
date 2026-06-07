package mvc.view;

import mvc.model.Item;
import mvc.model.ProveedorItem;
import mvc.controller.PreciosAcordadosController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PreciosAcordadosGUI extends JDialog{
    private JComboBox<Item> cbItemsSistema;
    private JTextField txtPrecioAcordado;
    private JButton btnAsignar;
    private JTable tablaPrecios;
    private DefaultTableModel modeloTabla;
    private String cuitProveedor;

    public PreciosAcordadosGUI(Frame owner, String cuit, String razonSocial) {
        super(owner, "Precios Acordados - " + razonSocial, true);
        this.cuitProveedor = cuit;
        setSize(550, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        inicializarComponentes();
        
        // Crea su propio controlador dedicado
        new PreciosAcordadosController(this);
    }

    private void inicializarComponentes() {
        JPanel panelForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelForm.setBorder(BorderFactory.createTitledBorder("Pactar Nuevo Precio"));

        panelForm.add(new JLabel("Seleccionar Ítem:"));
        cbItemsSistema = new JComboBox<>();
        panelForm.add(cbItemsSistema);

        panelForm.add(new JLabel("Precio Especial ($):"));
        txtPrecioAcordado = new JTextField(8);
        panelForm.add(txtPrecioAcordado);

        btnAsignar = new JButton("Guardar Acuerdo");
        panelForm.add(btnAsignar);
        add(panelForm, BorderLayout.NORTH);

        // Tabla
        modeloTabla = new DefaultTableModel(new String[]{"Código Ítem", "Descripción", "Tipo", "Precio Base", "Precio Acordado"}, 0);
        tablaPrecios = new JTable(modeloTabla);
        add(new JScrollPane(tablaPrecios), BorderLayout.CENTER);

        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public String getCuitProveedor() { return cuitProveedor; }
    public Item getItemSeleccionado() { return (Item) cbItemsSistema.getSelectedItem(); }
    public String getPrecioIngresado() { return txtPrecioAcordado.getText().trim(); }
    public JButton getBtnAsignar() { return btnAsignar; }

    public void cargarComboItems(List<Item> todosLosItems) {
        cbItemsSistema.removeAllItems();
        for (Item i : todosLosItems) {
            if (i.isActivo()) cbItemsSistema.addItem(i);
        }
    }

    public void actualizarTabla(List<ProveedorItem> acuerdos) {
        modeloTabla.setRowCount(0);
        for (ProveedorItem a : acuerdos) {
            modeloTabla.addRow(new Object[]{
                a.getItem().getCodigo(),
                a.getItem().getDescripcion(),
                a.getItem().getTipoItem(),
                "$" + a.getItem().getPrecioUnitarioBase(),
                "$" + a.getPrecioAcordado()
            });
        }
    }
}
