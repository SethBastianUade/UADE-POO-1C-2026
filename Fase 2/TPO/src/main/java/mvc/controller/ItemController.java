package mvc.controller;

import mvc.dto.ItemDTO;
import mvc.model.Item;
import mvc.model.Rubro;
import mvc.model.Producto;
import mvc.model.SistemaCompras;
import mvc.view.ItemGUI;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ItemController {
    
    private ItemGUI vista;
    private SistemaCompras sistema;

    public ItemController(ItemGUI vista) {
        this.vista = vista;
        this.sistema = SistemaCompras.getInstance();

        // Cargar Rubros en el desplegable
        vista.cargarRubrosCombo(sistema.getRubros());

        // Eventos para cambiar entre Producto y Servicio (CardLayout)
        vista.getRbProducto().addActionListener(e -> vista.getCardLayout().show(vista.getPanelDinamico(), "PRODUCTO"));
        vista.getRbServicio().addActionListener(e -> vista.getCardLayout().show(vista.getPanelDinamico(), "SERVICIO"));

        // Evento Guardar
        vista.getBtnGuardar().addActionListener(e -> guardarItem());

        cargarTabla();
    }

    private void guardarItem() {
        try {
            // Leer datos comunes
            String cod = vista.getCodigo();
            String desc = vista.getDescripcion();
            String uni = vista.getUnidad();
            double precio = Double.parseDouble(vista.getPrecio());
            double iva = Double.parseDouble(vista.getIva());
            Rubro rubroSeleccionado = (Rubro) vista.getCbRubro().getSelectedItem();

            if (rubroSeleccionado == null) {
                JOptionPane.showMessageDialog(vista, "Debe existir al menos un rubro para crear un ítem.");
                return;
            }

            // Dependiendo del tipo, leemos el resto y guardamos
            if (vista.getRbProducto().isSelected()) {
                String lote = vista.getLote();
                LocalDate vto = LocalDate.parse(vista.getVencimiento()); // Espera formato YYYY-MM-DD
                int stockActual = 0; // Nuevo producto inicia sin stock
                int stockMin = Integer.parseInt(vista.getStockMinimo());             
                sistema.agregarProducto(cod, desc, uni, precio, iva, rubroSeleccionado, lote, vto, stockActual,stockMin);
            } else {
                String mod = vista.getModalidad();
                int horas = Integer.parseInt(vista.getHoras());
                String req = vista.getRequisitos();
                sistema.agregarServicio(cod, desc, uni, precio, iva, rubroSeleccionado, mod, horas, req);
            }

            JOptionPane.showMessageDialog(vista, "Ítem guardado con éxito.");
            cargarTabla();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error en los datos. Revise números y fecha (YYYY-MM-DD).\nDetalle: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTabla() {
        List<ItemDTO> dtos = new ArrayList<>();
        for (Item item : sistema.getItems()) {
            
            // Lógica para determinar qué mostrar en la columna de Stock
            String textoStock;
            if (item instanceof Producto) {
                // Si el item es un Producto, lo casteamos para acceder a getStockActual()
                Producto prod = (Producto) item;
                textoStock = String.valueOf(prod.getStockActual());
            } else {
                // Si es un Servicio, no tiene stock
                textoStock = "-";
            }

            dtos.add(new ItemDTO(
                item.getCodigo(), 
                item.getDescripcion(), 
                item.getTipoItem(), 
                item.getRubro().getDescripcion(), 
                item.getPrecioUnitarioBase(), 
                item.isActivo(),
                textoStock // <- Pasamos el stock aquí
            ));
        }
        vista.actualizarTabla(dtos);
    }
}