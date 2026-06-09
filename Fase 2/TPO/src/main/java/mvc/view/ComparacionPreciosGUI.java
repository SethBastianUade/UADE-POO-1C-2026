package mvc.view;

import mvc.dto.ComparacionPrecioDTO;
import mvc.dto.ItemDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ComparacionPreciosGUI extends JInternalFrame {
    private JComboBox<String> cbItem;
    private JTable tablaPrecios;
    private DefaultTableModel modeloTabla;

    private List<ItemDTO> itemsCombo = new ArrayList<>();

    public ComparacionPreciosGUI() {
        super("Consulta: Comparación de Precios de Ítems", true, true, true, true);
        setSize(700, 400);
        setLayout(new BorderLayout());

        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltro.setBorder(BorderFactory.createTitledBorder("Ítem a comparar"));
        panelFiltro.add(new JLabel("Ítem:"));
        cbItem = new JComboBox<>();
        cbItem.setPreferredSize(new Dimension(300, 25));
        panelFiltro.add(cbItem);
        add(panelFiltro, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[]{"Proveedor", "CUIT", "Precio acordado", "Fecha acuerdo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaPrecios = new JTable(modeloTabla);
        tablaPrecios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tablaPrecios);
        scroll.setBorder(BorderFactory.createTitledBorder("Precio acordado con cada proveedor que lo suministra"));
        add(scroll, BorderLayout.CENTER);

        new mvc.controller.ComparacionPreciosController(this);
    }

    // --- Métodos públicos que usa el controller ---
    public void cargarComboItems(List<ItemDTO> items) {
        itemsCombo = items;
        cbItem.removeAllItems();
        for (ItemDTO i : items) {
            cbItem.addItem(i.getCodigo() + " - " + i.getDescripcion());
        }
    }

    public String getCodigoItemSeleccionado() {
        int idx = cbItem.getSelectedIndex();
        return (idx != -1) ? itemsCombo.get(idx).getCodigo() : null;
    }

    public void actualizarTabla(List<ComparacionPrecioDTO> lista) {
        modeloTabla.setRowCount(0);
        for (ComparacionPrecioDTO dto : lista) {
            modeloTabla.addRow(new Object[]{
                    dto.getRazonSocialProveedor(), dto.getCuit(),
                    String.format("$%.2f", dto.getPrecioAcordado()), dto.getFechaAcuerdo()
            });
        }
    }

    public void mostrarMensaje(String msg, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, msg, titulo, tipo);
    }

    public JComboBox<String> getCbItem() { return cbItem; }
}
