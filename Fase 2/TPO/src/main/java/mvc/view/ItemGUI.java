package mvc.view;

import mvc.controller.ItemController;
import mvc.dto.ItemDTO;
import mvc.model.Rubro;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ItemGUI extends JInternalFrame {
   
    // Campos comunes
    private JTextField txtCodigo, txtDescripcion, txtUnidad, txtPrecio, txtIva;
    private JComboBox<Rubro> cbRubro;
    private JRadioButton rbProducto, rbServicio;
    
    // Paneles dinámicos
    private JPanel panelDinamico;
    private CardLayout cardLayout;

    // Campos Producto
    private JTextField txtLote, txtVencimiento, txtStockMinimo;
    
    // Campos Servicio
    private JTextField txtModalidad, txtHoras, txtRequisitos;

    private JButton btnGuardar;
    private JTable tablaItems;
    private DefaultTableModel modeloTabla;

    public ItemGUI() {
        super("Gestión de Ítems", true, true, true, true);
        setSize(1200, 800);
        setLayout(new BorderLayout());
    
        inicializarFormulario();
        inicializarTabla();
        new ItemController(this);
    }

    private void inicializarFormulario() {
        JPanel panelNorte = new JPanel(new BorderLayout());

        // 1. Datos Comunes
        JPanel panelComun = new JPanel(new GridLayout(4, 2, 5, 5));
        panelComun.setBorder(BorderFactory.createTitledBorder("Datos Generales"));
        
        panelComun.add(new JLabel("Código:")); txtCodigo = new JTextField(); panelComun.add(txtCodigo);
        panelComun.add(new JLabel("Descripción:")); txtDescripcion = new JTextField(); panelComun.add(txtDescripcion);
        panelComun.add(new JLabel("Unidad / Precio / IVA:"));
        
        // Mini panel para agrupar
        JPanel miniPanel = new JPanel(new GridLayout(1, 3));
        txtUnidad = new JTextField("Unid"); txtPrecio = new JTextField("0.0"); txtIva = new JTextField("21.0");
        miniPanel.add(txtUnidad); miniPanel.add(txtPrecio); miniPanel.add(txtIva);
        panelComun.add(miniPanel);

        panelComun.add(new JLabel("Rubro Asociado:")); 
        cbRubro = new JComboBox<>(); 
        panelComun.add(cbRubro);

        // 2. Selector de Tipo
        JPanel panelTipo = new JPanel();
        rbProducto = new JRadioButton("Es Producto", true);
        rbServicio = new JRadioButton("Es Servicio");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbProducto); bg.add(rbServicio);
        panelTipo.add(rbProducto); panelTipo.add(rbServicio);

        // 3. Panel Dinámico (CardLayout)
        cardLayout = new CardLayout();
        panelDinamico = new JPanel(cardLayout);
        panelDinamico.setBorder(BorderFactory.createTitledBorder("Datos Específicos"));

        // Carta Producto
        JPanel panelProd = new JPanel(new GridLayout(3, 2));
        panelProd.add(new JLabel("Lote:")); txtLote = new JTextField(); panelProd.add(txtLote);
        panelProd.add(new JLabel("Vencimiento (YYYY-MM-DD):")); txtVencimiento = new JTextField(); panelProd.add(txtVencimiento);
        panelProd.add(new JLabel("Stock Mínimo:")); txtStockMinimo = new JTextField("0"); panelProd.add(txtStockMinimo);
        panelDinamico.add(panelProd, "PRODUCTO");

        // Carta Servicio
        JPanel panelServ = new JPanel(new GridLayout(3, 2));
        panelServ.add(new JLabel("Modalidad:")); txtModalidad = new JTextField(); panelServ.add(txtModalidad);
        panelServ.add(new JLabel("Duración (Hs):")); txtHoras = new JTextField("0"); panelServ.add(txtHoras);
        panelServ.add(new JLabel("Requisitos:")); txtRequisitos = new JTextField(); panelServ.add(txtRequisitos);
        panelDinamico.add(panelServ, "SERVICIO");

        // Botón
        btnGuardar = new JButton("Guardar Ítem");
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.add(btnGuardar);

        // Armado del Norte
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(panelComun, BorderLayout.NORTH);
        panelTop.add(panelTipo, BorderLayout.CENTER);
        panelTop.add(panelDinamico, BorderLayout.SOUTH);

        panelNorte.add(panelTop, BorderLayout.CENTER);
        panelNorte.add(panelBoton, BorderLayout.SOUTH);

        add(panelNorte, BorderLayout.NORTH);
    }

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel(new String[]{"Código", "Descripción", "Tipo", "Rubro", "Precio", "Estado", "Stock"}, 0);
        tablaItems = new JTable(modeloTabla);
        add(new JScrollPane(tablaItems), BorderLayout.CENTER);
    }

    // Getters para el Controlador
    public JButton getBtnGuardar() { return btnGuardar; }
    public JRadioButton getRbProducto() { return rbProducto; }
    public JRadioButton getRbServicio() { return rbServicio; }
    public JComboBox<Rubro> getCbRubro() { return cbRubro; }
    public CardLayout getCardLayout() { return cardLayout; }
    public JPanel getPanelDinamico() { return panelDinamico; }

    // Getters de datos comunes
    public String getCodigo() { return txtCodigo.getText(); }
    public String getDescripcion() { return txtDescripcion.getText(); }
    public String getUnidad() { return txtUnidad.getText(); }
    public String getPrecio() { return txtPrecio.getText(); }
    public String getIva() { return txtIva.getText(); }

    // Getters de Producto
    public String getLote() { return txtLote.getText(); }
    public String getVencimiento() { return txtVencimiento.getText(); }
    public String getStockMinimo() { return txtStockMinimo.getText(); }

    // Getters de Servicio
    public String getModalidad() { return txtModalidad.getText(); }
    public String getHoras() { return txtHoras.getText(); }
    public String getRequisitos() { return txtRequisitos.getText(); }

    public void cargarRubrosCombo(List<Rubro> rubros) {
        cbRubro.removeAllItems();
        for (Rubro r : rubros) {
            if (r.isActivo()) cbRubro.addItem(r);
        }
    }

    public void actualizarTabla(List<ItemDTO> items) {
        modeloTabla.setRowCount(0);
        for (ItemDTO dto : items) {
            modeloTabla.addRow(new Object[]{dto.getCodigo(), dto.getDescripcion(), dto.getTipo(), 
                                            dto.getRubroDescripcion(), dto.getPrecio(), dto.isActivo() ? "Activo" : "Baja", dto.getStockActual()});
        }
    }
}
