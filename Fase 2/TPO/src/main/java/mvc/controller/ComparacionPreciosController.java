package mvc.controller;

import mvc.dto.ComparacionPrecioDTO;
import mvc.dto.ItemDTO;
import mvc.model.Item;
import mvc.model.Proveedor;
import mvc.model.ProveedorItem;
import mvc.view.ComparacionPreciosGUI;

import java.util.ArrayList;
import java.util.List;

public class ComparacionPreciosController {
    private ComparacionPreciosGUI vista;

    public ComparacionPreciosController(ComparacionPreciosGUI vista) {
        this.vista = vista;

        this.vista.getCbItem().addActionListener(e -> comparar());

        cargarComboItems();
        comparar();
    }

    private void comparar() {
        String codigo = vista.getCodigoItemSeleccionado();
        if (codigo == null) {
            vista.actualizarTabla(new ArrayList<>());
            return;
        }
        Item item = ItemController.buscarItemPorCodigo(codigo);
        if (item == null) {
            return;
        }

        List<ComparacionPrecioDTO> dtos = new ArrayList<>();
        for (Proveedor proveedor : ProveedorController.getProveedoresQueSuministran(item)) {
            ProveedorItem acuerdo = proveedor.getPrecioAcordadoPara(item);
            dtos.add(new ComparacionPrecioDTO(
                    proveedor.getRazonSocial(), proveedor.getCuit(),
                    acuerdo.getPrecioAcordado(), acuerdo.getFechaAcuerdo().toString()
            ));
        }
        vista.actualizarTabla(dtos);
    }

    private void cargarComboItems() {
        List<ItemDTO> items = new ArrayList<>();
        for (Item i : ItemController.getItems()) {
            items.add(new ItemDTO(i.getCodigo(), i.getDescripcion(), i.getTipoItem(),
                    i.getRubro() != null ? i.getRubro().getDescripcion() : "",
                    i.getPrecioUnitarioBase(), i.isActivo(), "-"));
        }
        vista.cargarComboItems(items);
    }
}
