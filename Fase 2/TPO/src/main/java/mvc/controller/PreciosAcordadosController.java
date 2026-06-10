package mvc.controller;
import mvc.model.Item;
import mvc.model.Proveedor;
import mvc.view.PreciosAcordadosGUI;
import javax.swing.JOptionPane;

public class PreciosAcordadosController {
    private PreciosAcordadosGUI vista;

    public PreciosAcordadosController(PreciosAcordadosGUI vista) {
        this.vista = vista;

        this.vista.getBtnAsignar().addActionListener(e -> guardarAcuerdo());

        // Llenamos el combo con los productos/servicios registrados en el sistema
        this.vista.cargarComboItems(ItemController.getItems());
        refrescarTabla();
    }

    private void guardarAcuerdo() {
        try {
            Item item = vista.getItemSeleccionado();
            double precio = Double.parseDouble(vista.getPrecioIngresado());

            if (item == null) {
                JOptionPane.showMessageDialog(vista, "No hay ningún ítem seleccionado del catálogo.");
                return;
            }

            ProveedorController.registrarPrecioAcordado(vista.getCuitProveedor(), item.getCodigo(), precio);
            JOptionPane.showMessageDialog(vista, "Precio pactado correctamente.");
            refrescarTabla();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Por favor, ingrese un precio numérico válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescarTabla() {
        Proveedor p = ProveedorController.buscarProveedorPorCuit(vista.getCuitProveedor());
        if (p != null) {
            vista.actualizarTabla(p.getPreciosAcordados());
        }
    }
}
