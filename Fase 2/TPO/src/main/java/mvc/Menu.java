package mvc;
import javax.swing.*;
import mvc.view.*;
import mvc.controller.*;
import java.awt.*;

public class Menu {
    public static class VentanaPrincipal extends JFrame {

        // El "escritorio" donde vivirán las ventanas internas
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
            JMenu menuCatalogos = new JMenu("Maestros");
            JMenuItem itemRubros = new JMenuItem("Gestionar Rubros");
            JMenuItem itemProductoServicio = new JMenuItem("Gestionar Productos/Servicios - Rubros");


            itemRubros.addActionListener(e -> abrirVistaRubros());
            itemProductoServicio.addActionListener(e -> abrirVistaProductoServicio());

            menuCatalogos.add(itemRubros);
            menuCatalogos.add(itemProductoServicio);
            barraMenu.add(menuCatalogos);
            setJMenuBar(barraMenu);
        }

        private void abrirVistaRubros() {
            // Instanciamos la vista
            RubroGUI vistaRubros = new RubroGUI();
            
            // Instanciamos el controlador y le pasamos la vista para que la controle
            new RubroController(vistaRubros);

            // Agregamos la ventanita al escritorio y la hacemos visible
            escritorio.add(vistaRubros);
            vistaRubros.setVisible(true);
        }

        private void abrirVistaProductoServicio() {
            // Instanciamos la vista
            ItemGUI vistaItems = new ItemGUI();
            
            // Instanciamos el controlador y le pasamos la vista para que la controle
            new ItemController(vistaItems);

            // Agregamos la ventanita al escritorio y la hacemos visible
            escritorio.add(vistaItems);
            vistaItems.setVisible(true);
        }

        public static void main(String[] args) {
            
            // Hilo seguro de Swing
            SwingUtilities.invokeLater(() -> {
                new VentanaPrincipal().setVisible(true);
            });

            SwingUtilities.invokeLater(() -> {
            // 1. Instanciamos la Vista del Login
            LoginGUI vistaLogin = new LoginGUI();
            
            // 2. Instanciamos el Controlador y le pasamos la vista
            new LoginController(vistaLogin);
            
            // 3. Hacemos visible el Login
            vistaLogin.setVisible(true);
        });
        }
    }
}



