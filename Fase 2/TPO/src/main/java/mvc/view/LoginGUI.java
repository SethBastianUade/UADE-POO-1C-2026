package mvc.view;
import javax.swing.*;
import java.awt.*;

public class LoginGUI extends JFrame{
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    public LoginGUI() {
        setTitle("Iniciar Sesión");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla
        setResizable(false);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Panel principal con un margen
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de campos (2 filas, 2 columnas)
        JPanel panelCampos = new JPanel(new GridLayout(2, 2, 5, 10));
        
        panelCampos.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField();
        panelCampos.add(txtUsuario);

        panelCampos.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        panelCampos.add(txtPassword);

        panelPrincipal.add(panelCampos, BorderLayout.CENTER);

        // Panel del botón
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnIngresar = new JButton("Ingresar al Sistema");
        panelBoton.add(btnIngresar);

        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    public String getUsuarioIngresado() {
        return txtUsuario.getText().trim();
    }

    public String getPasswordIngresado() {
        // JPasswordField devuelve char[], lo convertimos a String (Práctica Junior)
        return new String(txtPassword.getPassword());
    }

    public JButton getBtnIngresar() {
        return btnIngresar;
    }

    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
}
