package mvc.view;
import mvc.model.CertificadoExclusion;
import mvc.enums.TipoImpuesto;
import mvc.controller.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CertificadosProveedorGUI extends JDialog{
    private JTextField txtNumero, txtFechaDesde, txtFechaHasta;
    private JComboBox<TipoImpuesto> cbTipoImpuesto;
    private JButton btnAgregar;
    private JTable tablaCertificados;
    private DefaultTableModel modeloTabla;
    private String cuitProveedor;

    public CertificadosProveedorGUI(Frame owner, String cuit, String razonSocial) {
        super(owner, "Certificados de Exclusión - " + razonSocial, true);
        this.cuitProveedor = cuit;
        setSize(550, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        inicializarComponentes();
        
        
        new CertificadoController(this);
    }

    private void inicializarComponentes() {
        // Formulario superior
        JPanel panelForm = new JPanel(new GridLayout(2, 4, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Nuevo Certificado"));

        panelForm.add(new JLabel("Número:"));
        txtNumero = new JTextField();
        panelForm.add(txtNumero);

        panelForm.add(new JLabel("Impuesto:"));
        cbTipoImpuesto = new JComboBox<>(TipoImpuesto.values());
        panelForm.add(cbTipoImpuesto);

        panelForm.add(new JLabel("Desde (AAAA-MM-DD):"));
        txtFechaDesde = new JTextField();
        panelForm.add(txtFechaDesde);

        panelForm.add(new JLabel("Hasta (AAAA-MM-DD):"));
        txtFechaHasta = new JTextField();
        panelForm.add(txtFechaHasta);

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAgregar = new JButton("Registrar Certificado");
        panelBoton.add(btnAgregar);

        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelForm, BorderLayout.CENTER);
        panelNorte.add(panelBoton, BorderLayout.SOUTH);
        add(panelNorte, BorderLayout.NORTH);

        // Tabla inferior
        modeloTabla = new DefaultTableModel(new String[]{"Número", "Impuesto", "Válido Desde", "Válido Hasta"}, 0);
        tablaCertificados = new JTable(modeloTabla);
        add(new JScrollPane(tablaCertificados), BorderLayout.CENTER);

        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    // Getters para el controlador
    public String getCuitProveedor() { return cuitProveedor; }
    public String getNumero() { return txtNumero.getText().trim(); }
    public TipoImpuesto getTipoImpuesto() { return (TipoImpuesto) cbTipoImpuesto.getSelectedItem(); }
    public String getFechaDesde() { return txtFechaDesde.getText().trim(); }
    public String getFechaHasta() { return txtFechaHasta.getText().trim(); }
    public JButton getBtnAgregar() { return btnAgregar; }

    public void limpiarFormulario() {
        txtNumero.setText("");
        txtFechaDesde.setText("");
        txtFechaHasta.setText("");
    }

    public void actualizarTabla(List<CertificadoExclusion> certificados) {
        modeloTabla.setRowCount(0);
        for (CertificadoExclusion c : certificados) {
            modeloTabla.addRow(new Object[]{
                c.getNumeroCertificado(), c.getTipoImpuesto(), c.getFechaDesde(), c.getFechaHasta()
            });
        }
    }    
}
