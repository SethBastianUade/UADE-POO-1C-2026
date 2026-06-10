package mvc;
import javax.swing.*;
import mvc.view.*;
import mvc.controller.*;
import java.awt.*;

public class Menu {
    public static class VentanaPrincipal extends JFrame {
        private JDesktopPane escritorio;

        public VentanaPrincipal() {

            setTitle("Sistema de Gestión Integral de Compras");
            setSize(1200, 1000);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            escritorio = new JDesktopPane();
            add(escritorio, BorderLayout.CENTER);

            crearMenu();
        }

        private void crearMenu() {
            JMenuBar barraMenu = new JMenuBar();
            JMenu menuCatalogos = new JMenu("Módulo de Catálogos");
            JMenu menuProveedores = new JMenu("Módulo de Proveedores");
            JMenu menuCompras = new JMenu("Módulo de Compras");
            JMenu menuFacturacionPagos = new JMenu("Módulo de Facturación y Pagos");
            JMenu menuConsultas = new JMenu("Módulo de Consultas");
            
            JMenuItem itemRubros = new JMenuItem("Gestionar Rubros");
            JMenuItem itemProductoServicio = new JMenuItem("Gestionar Productos/Servicios - Rubros");
            JMenuItem itemProveedor = new JMenuItem("Gestionar Proveedores");
            JMenuItem itemOrdenesCompra = new JMenuItem("Gestionar Órdenes de Compra");
            JMenuItem itemDocumentos = new JMenuItem("Gestionar Documentos Comerciales");
            JMenuItem itemOrdenesPago = new JMenuItem("Gestionar Órdenes de Pago");
            JMenuItem itemTrazabilidad = new JMenuItem("Trazabilidad Documental");
            JMenuItem itemCuentaCorriente = new JMenuItem("Cuenta Corriente de Proveedor");
            JMenuItem itemSeguimiento = new JMenuItem("Seguimiento de Compras y Pagos");
            JMenuItem itemComparacionPrecios = new JMenuItem("Comparación de Precios de Ítems");
            JMenuItem itemReportesFiscales = new JMenuItem("Reportes Fiscales");


            itemRubros.addActionListener(e -> abrirVistaRubros());
            itemProductoServicio.addActionListener(e -> abrirVistaProductoServicio());
            itemProveedor.addActionListener(e -> abrirVistaProveedor());
            itemOrdenesCompra.addActionListener(e -> abrirVistaOrdenesCompra());
            itemDocumentos.addActionListener(e -> abrirVistaDocumentos());
            itemOrdenesPago.addActionListener(e -> abrirVistaOrdenesPago());
            itemTrazabilidad.addActionListener(e -> abrirVista(new ConsultaDocumentosGUI()));
            itemCuentaCorriente.addActionListener(e -> abrirVista(new CuentaCorrienteGUI()));
            itemSeguimiento.addActionListener(e -> abrirVista(new SeguimientoComprasGUI()));
            itemComparacionPrecios.addActionListener(e -> abrirVista(new ComparacionPreciosGUI()));
            itemReportesFiscales.addActionListener(e -> abrirVista(new ReportesFiscalesGUI()));

            menuCatalogos.add(itemRubros);
            menuCatalogos.add(itemProductoServicio);
            menuProveedores.add(itemProveedor);
            menuCompras.add(itemOrdenesCompra);
            menuFacturacionPagos.add(itemDocumentos);
            menuFacturacionPagos.add(itemOrdenesPago);
            menuConsultas.add(itemTrazabilidad);
            menuConsultas.add(itemCuentaCorriente);
            menuConsultas.add(itemSeguimiento);
            menuConsultas.add(itemComparacionPrecios);
            menuConsultas.add(itemReportesFiscales);
            barraMenu.add(menuCatalogos);
            barraMenu.add(menuProveedores);
            barraMenu.add(menuCompras);
            barraMenu.add(menuFacturacionPagos);
            barraMenu.add(menuConsultas);
            setJMenuBar(barraMenu);
        }

        private void abrirVistaRubros() {
            // Instanciamos la vista
            RubroGUI vistaRubros = new RubroGUI();
            
            // Agregamos la ventanita al escritorio y la hacemos visible
            escritorio.add(vistaRubros);
            vistaRubros.setVisible(true);
        }

        private void abrirVistaProductoServicio() {
            // Instanciamos la vista
            ItemGUI vistaItems = new ItemGUI();

            // Agregamos la ventanita al escritorio y la hacemos visible
            escritorio.add(vistaItems);
            vistaItems.setVisible(true);
        }

        private void abrirVistaProveedor() {
            // Instanciamos la vista
            ProveedorGUI vistaProveedores = new ProveedorGUI();

            // Agregamos la ventanita al escritorio y la hacemos visible
            escritorio.add(vistaProveedores);
            vistaProveedores.setVisible(true);
        }

        private void abrirVistaOrdenesCompra() {
            // Instanciamos la vista
            OrdenDeCompraGUI vistaOC = new OrdenDeCompraGUI();

            // Agregamos la ventanita al escritorio y la hacemos visible
            escritorio.add(vistaOC);
            vistaOC.setVisible(true);
        }

        private void abrirVistaDocumentos() {
            // Instanciamos la vista
            DocumentoComercialGUI vistaDocumentos = new DocumentoComercialGUI();

            // Agregamos la ventanita al escritorio y la hacemos visible
            escritorio.add(vistaDocumentos);
            vistaDocumentos.setVisible(true);
        }

        private void abrirVistaOrdenesPago() {
            // Instanciamos la vista
            OrdenDePagoGUI vistaOP = new OrdenDePagoGUI();

            // Agregamos la ventanita al escritorio y la hacemos visible
            escritorio.add(vistaOP);
            vistaOP.setVisible(true);
        }

        // Helper genérico para las vistas de consultas
        private void abrirVista(JInternalFrame vista) {
            escritorio.add(vista);
            vista.setVisible(true);
        }

        public static void main(String[] args) {
            // Carga inicial de datos (comentar esta línea para arrancar el sistema vacío)
            DatosDePrueba.cargar();

            // Hilo seguro de Swing: mostramos sólo la ventana de Login al iniciar.
            SwingUtilities.invokeLater(() -> {
                // 1. Instanciamos la Vista del Login
                LoginGUI vistaLogin = new LoginGUI();

                // 2. Hacemos visible el Login
                vistaLogin.setVisible(true);
            });
        }
    }
}
