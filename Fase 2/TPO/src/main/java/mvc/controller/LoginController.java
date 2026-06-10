package mvc.controller;
import mvc.enums.RolUsuario;
import mvc.model.Usuario;
import mvc.view.LoginGUI;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class LoginController {
    // ============================================================
    // DATOS DEL MÓDULO: USUARIOS Y SESIÓN (compartidos por toda la app)
    // ============================================================
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static Usuario usuarioLogueado;

    static {
        usuarios.add(new Usuario("admin", "1234", "Juan", "Pérez", RolUsuario.SUPERVISOR));
        usuarios.add(new Usuario("operador", "1234", "María", "Gómez", RolUsuario.OPERADOR));
    }

    private LoginGUI vista;

    public LoginController(LoginGUI vista) {
        this.vista = vista;

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

        Usuario userValido = autenticarUsuario(usuario, password);

        if (userValido != null) {
            // Login Exitoso
            vista.mostrarMensaje("Bienvenido, " + userValido.getNombre() + " (" + userValido.getRol() + ")",
                                 "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);

            // La vista se encarga de cerrarse y abrir la ventana principal
            // (el controller no instancia vistas)
            vista.abrirVentanaPrincipal("ERP Compras - Usuario: " + userValido.getNombreUsuario()
                    + " | Rol: " + userValido.getRol());
        } else {
            // Login Fallido
            vista.mostrarMensaje("Usuario o contraseña incorrectos.", "Error de Acceso", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
    // LÓGICA DE NEGOCIO DEL MÓDULO (antes en SistemaCompras)
    // ============================================================
    public static Usuario autenticarUsuario(String nombreUsuario, String password) {
        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equals(nombreUsuario) &&
                u.getPasswordHash().equals(password) &&
                u.isActivo()) {

                usuarioLogueado = u;
                return u; // Retorna el usuario si los datos coinciden
            }
        }
        return null; // Retorna nulo si falla
    }

    public static Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public static Usuario buscarUsuario(String nombreUsuario) {
        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equals(nombreUsuario)) {
                return u;
            }
        }
        return null;
    }
}
