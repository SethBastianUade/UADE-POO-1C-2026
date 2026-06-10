package mvc.controller;

import mvc.dto.ItemDTO;
import mvc.model.Item;
import mvc.model.Rubro;
import mvc.model.Producto;
import mvc.model.Servicio;
import mvc.view.ItemGUI;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ItemController {
    // ============================================================
    // DATOS DEL MÓDULO: ÍTEMS (compartidos por toda la app)
    // ============================================================
    private static final List<Item> items = new ArrayList<>();
    private static int contadorIdItems = 1;

    private ItemGUI vista;

    public ItemController(ItemGUI vista) {
        this.vista = vista;

        // Cargar Rubros en el desplegable (los rubros son del módulo de Rubros)
        vista.cargarRubrosCombo(RubroController.getRubros());

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
                agregarProducto(cod, desc, uni, precio, iva, rubroSeleccionado, lote, vto, stockActual, stockMin);
            } else {
                String mod = vista.getModalidad();
                int horas = Integer.parseInt(vista.getHoras());
                String req = vista.getRequisitos();
                agregarServicio(cod, desc, uni, precio, iva, rubroSeleccionado, mod, horas, req);
            }

            JOptionPane.showMessageDialog(vista, "Ítem guardado con éxito.");
            cargarTabla();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error en los datos. Revise números y fecha (YYYY-MM-DD).\nDetalle: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTabla() {
        List<ItemDTO> dtos = new ArrayList<>();
        for (Item item : getItems()) {

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

    // ============================================================
    // LÓGICA DE NEGOCIO DEL MÓDULO (antes en SistemaCompras)
    // ============================================================
    public static void agregarProducto(String cod, String desc, String uni, double precio,
                                       double iva, Rubro rubro, String lote, LocalDate vto, int stockActual, int stockMin) {
        Producto p = new Producto(contadorIdItems++, cod, desc, uni, precio, iva, rubro, lote, vto, stockActual, stockMin);
        items.add(p);
    }

    public static void agregarServicio(String cod, String desc, String uni, double precio,
                                       double iva, Rubro rubro, String mod, int horas, String req) {
        Servicio s = new Servicio(contadorIdItems++, cod, desc, uni, precio, iva, rubro, mod, horas, req);
        items.add(s);
    }

    public static List<Item> getItems() {
        return items;
    }

    public static Item buscarItemPorCodigo(String codigo) {
        for (Item i : items) {
            if (i.getCodigo().equalsIgnoreCase(codigo)) {
                return i;
            }
        }
        return null;
    }
}
