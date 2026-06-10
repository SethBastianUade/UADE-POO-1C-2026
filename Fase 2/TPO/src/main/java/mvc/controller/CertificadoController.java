package mvc.controller;

import mvc.model.Proveedor;
import mvc.view.CertificadosProveedorGUI;
import mvc.enums.TipoImpuesto;
import java.time.LocalDate;
import javax.swing.JOptionPane;

public class CertificadoController {
    private CertificadosProveedorGUI vista;

    public CertificadoController(CertificadosProveedorGUI vista) {
        this.vista = vista;

        this.vista.getBtnAgregar().addActionListener(e -> guardarCertificado());
        refrescarTabla();
    }

    private void guardarCertificado() {
        try {
            String numero = vista.getNumero();
            TipoImpuesto tipo = vista.getTipoImpuesto();
            LocalDate desde = LocalDate.parse(vista.getFechaDesde());
            LocalDate hasta = LocalDate.parse(vista.getFechaHasta());

            if (numero.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "El número de certificado es obligatorio.");
                return;
            }

            ProveedorController.agregarCertificadoAProveedor(vista.getCuitProveedor(), numero, tipo, desde, hasta);
            vista.limpiarFormulario();
            JOptionPane.showMessageDialog(vista, "Certificado guardado con éxito.");
            refrescarTabla();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error en el formato de las fechas (Use AAAA-MM-DD).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescarTabla() {
        Proveedor p = ProveedorController.buscarProveedorPorCuit(vista.getCuitProveedor());
        if (p != null) {
            vista.actualizarTabla(p.getCertificados());
        }
    }
}
