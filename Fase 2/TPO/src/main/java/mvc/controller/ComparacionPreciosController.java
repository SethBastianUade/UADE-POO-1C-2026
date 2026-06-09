package mvc.controller;

import mvc.dto.ComparacionPrecioDTO;
import mvc.dto.ItemDTO;
import mvc.model.Item;
import mvc.model.Proveedor;
import mvc.model.ProveedorItem;
import mvc.model.SistemaCompras;
import mvc.view.ComparacionPreciosGUI;

import java.util.ArrayList;
import java.util.List;

public class ComparacionPreciosController {
    private ComparacionPreciosGUI vista;
    private SistemaCompras sistema;

    public ComparacionPreciosController(ComparacionPreciosGUI vista) {
        this.vista = vista;
        this.sistema = SistemaCompras.getInstance();

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
        Item item = sistema.buscarItemPorCodigo(codigo);
        if (item == null) {
            return;
        }

        List<ComparacionPrecioDTO> dtos = new ArrayList<>();
        for (Proveedor proveedor : sistema.getProveedoresQueSuministran(item)) {
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
        for (Item i : sistema.getItems()) {
            items.add(new ItemDTO(i.getCodigo(), i.getDescripcion(), i.getTipoItem(),
                    i.getRubro() != null ? i.getRubro().getDescripcion() : "",
                    i.getPrecioUnitarioBase(), i.isActivo(), "-"));
        }
        vista.cargarComboItems(items);
    }
}
