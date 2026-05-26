package mvc.controller;
import mvc.Menu.VentanaPrincipal;
import mvc.model.SistemaCompras;
import mvc.model.Usuario;
import mvc.view.LoginGUI;
import javax.swing.JOptionPane;

public class LoginController {
    private LoginGUI vista;
    private SistemaCompras sistema;

    public LoginController(LoginGUI vista) {
        this.vista = vista;
        this.sistema = SistemaCompras.getInstance();

        // Asignamos la acción al botón Ingresar
        this.vista.getBtnIngresar().addActionListener(e -> iniciarSesion());
    }

    private void iniciarSesion() {
        String usuario = vista.getUsuarioIngresado();
        String password = vista.getPasswordIngresado();

        if (usuario.isEmpty() || password.isEmpty()) {
            vista.mostrarMensaje("Por favor, complete ambos campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Llamamos al método del Singleton
        Usuario userValido = sistema.autenticarUsuario(usuario, password);

        if (userValido != null) {
            // Login Exitoso
            vista.mostrarMensaje("Bienvenido, " + userValido.getNombre() + " (" + userValido.getRol() + ")", 
                                 "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);
            
            // 1. Cerramos la ventana de Login
            vista.dispose(); 

            // 2. Abrimos la Ventana Principal
            VentanaPrincipal ventanaPrincipal = new VentanaPrincipal();
            
            // (Opcional) Puedes mostrar el usuario logueado en el título de la ventana
            ventanaPrincipal.setTitle("ERP Compras - Usuario: " + userValido.getNombreUsuario() + " | Rol: " + userValido.getRol());
            
            ventanaPrincipal.setVisible(true);

        } else {
            // Login Fallido
            vista.mostrarMensaje("Usuario o contraseña incorrectos.", "Error de Acceso", JOptionPane.ERROR_MESSAGE);
        }
    }
}
