package mvc.controller;

import mvc.dto.RubroDTO;
import mvc.model.Proveedor;
import mvc.model.Rubro;
import mvc.view.AsociarProveedorRubroGUI;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class AsociarProveedorRubroController {
    private AsociarProveedorRubroGUI vista;

    public AsociarProveedorRubroController(AsociarProveedorRubroGUI vista) {
        this.vista = vista;

        this.vista.getBtnAgregarRubro().addActionListener(e -> agregarRubro());
        this.vista.getBtnQuitarRubro().addActionListener(e -> quitarRubro());

        // Llenamos el desplegable con TODOS los rubros del sistema
        this.vista.cargarComboRubros(RubroController.getRubros());
        // Y la tablita con los rubros que YA TIENE el proveedor
        refrescarTabla();
    }

    private void agregarRubro() {
        Rubro rubroElegido = vista.getRubroSeleccionado();
        if (rubroElegido == null) {
            return;
        }
        boolean exito = ProveedorController.asignarRubroAProveedor(vista.getCuitProveedor(), rubroElegido.getCodigo());
        if (exito) {
            refrescarTabla();
        } else {
            vista.mostrarMensaje("El proveedor ya tiene asignado este rubro.");
        }
    }

    private void quitarRubro() {
        String codigoRubro = vista.getCodigoRubroSeleccionadoEnTabla();
        if (codigoRubro == null) {
            vista.mostrarMensaje("Por favor, seleccione un rubro de la tabla inferior para quitar.");
            return;
        }

        // Pedimos confirmación por seguridad
        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Estás seguro de que deseas desvincular este rubro del proveedor?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION
                && ProveedorController.desvincularRubroDeProveedor(vista.getCuitProveedor(), codigoRubro)) {
            refrescarTabla();
        }
    }

    // Pinta la tablita del diálogo convirtiendo Modelos a DTOs
    private void refrescarTabla() {
        List<RubroDTO> dtos = new ArrayList<>();
        Proveedor proveedor = ProveedorController.buscarProveedorPorCuit(vista.getCuitProveedor());
        if (proveedor != null) {
            for (Rubro r : proveedor.getRubrosAsociados()) {
                dtos.add(new RubroDTO(r.getCodigo(), r.getDescripcion(), r.isActivo()));
            }
        }
        vista.actualizarTabla(dtos);
    }
}
