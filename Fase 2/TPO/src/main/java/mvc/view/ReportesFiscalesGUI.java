package mvc.view;

import mvc.dto.LibroIVACompraDTO;
import mvc.dto.TotalRetenidoDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportesFiscalesGUI extends JInternalFrame {
    private JTextField txtDesde, txtHasta;
    private JButton btnGenerar;

    private JTable tablaRetenciones;
    private DefaultTableModel modeloTablaRetenciones;
    private JLabel lblTotalRetenciones;

    private JTable tablaLibroIVA;
    private DefaultTableModel modeloTablaLibro;
    private JLabel lblTotalesLibro;

    public ReportesFiscalesGUI() {
        super("Consulta: Reportes Fiscales", true, true, true, true);
        setSize(1000, 540);
        setLayout(new BorderLayout());

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Período (fechas vacías = sin límite)"));
        panelFiltros.add(new JLabel("Desde (AAAA-MM-DD):"));
        txtDesde = new JTextField(8);
        panelFiltros.add(txtDesde);
        panelFiltros.add(new JLabel("Hasta (AAAA-MM-DD):"));
        txtHasta = new JTextField(8);
        panelFiltros.add(txtHasta);
        btnGenerar = new JButton("Generar reportes");
        panelFiltros.add(btnGenerar);
        add(panelFiltros, BorderLayout.NORTH);

        JTabbedPane pestanias = new JTabbedPane();
        pestanias.addTab("Retenciones por impuesto", inicializarPanelRetenciones());
        pestanias.addTab("Libro IVA Compras", inicializarPanelLibroIVA());
        add(pestanias, BorderLayout.CENTER);

        new mvc.controller.ReportesFiscalesController(this);
    }

    private JPanel inicializarPanelRetenciones() {
        JPanel panel = new JPanel(new BorderLayout());
        modeloTablaRetenciones = new DefaultTableModel(new String[]{"Impuesto", "Cant. retenciones", "Total retenido"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaRetenciones = new JTable(modeloTablaRetenciones);
        panel.add(new JScrollPane(tablaRetenciones), BorderLayout.CENTER);
        lblTotalRetenciones = new JLabel("Total retenido en el período: $0.00");
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTotal.add(lblTotalRetenciones);
        panel.add(panelTotal, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel inicializarPanelLibroIVA() {
        JPanel panel = new JPanel(new BorderLayout());
        modeloTablaLibro = new DefaultTableModel(new String[]{
                "Fecha", "CUIT", "Proveedor", "Tipo Doc", "N° Doc", "Neto/Base", "IVA", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaLibroIVA = new JTable(modeloTablaLibro);
        panel.add(new JScrollPane(tablaLibroIVA), BorderLayout.CENTER);
        lblTotalesLibro = new JLabel("Totales del período — Base: $0.00 | IVA: $0.00 | Total: $0.00");
        JPanel panelTotal = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTotal.add(lblTotalesLibro);
        panel.add(panelTotal, BorderLayout.SOUTH);
        return panel;
    }

    // --- Métodos públicos que usa el controller ---
    public String getDesde() { return txtDesde.getText().trim(); }
    public String getHasta() { return txtHasta.getText().trim(); }

    public void actualizarTablaRetenciones(List<TotalRetenidoDTO> lista) {
        modeloTablaRetenciones.setRowCount(0);
        for (TotalRetenidoDTO dto : lista) {
            modeloTablaRetenciones.addRow(new Object[]{
                    dto.getTipoImpuesto(), dto.getCantidadRetenciones(),
                    String.format("$%.2f", dto.getTotalRetenido())
            });
        }
    }

    public void setTotalRetenciones(String texto) { lblTotalRetenciones.setText(texto); }

    public void actualizarTablaLibroIVA(List<LibroIVACompraDTO> lista) {
        modeloTablaLibro.setRowCount(0);
        for (LibroIVACompraDTO dto : lista) {
            modeloTablaLibro.addRow(new Object[]{
                    dto.getFecha(), dto.getCuit(), dto.getRazonSocialProveedor(),
                    dto.getTipoDocumento(), dto.getNumeroDocumento(),
                    String.format("$%.2f", dto.getBaseImponible()),
                    String.format("$%.2f", dto.getMontoIVA()),
                    String.format("$%.2f", dto.getTotal())
            });
        }
    }

    public void setTotalesLibro(String texto) { lblTotalesLibro.setText(texto); }

    public void mostrarMensaje(String msg, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, msg, titulo, tipo);
    }

    public JButton getBtnGenerar() { return btnGenerar; }
}
