package mvc.dto;

public class LineaOrdenCompraDTO {
    private String codigoItem;
    private String descripcionItem;
    private double cantidad;
    private double precioUnitario;
    private double subtotal;

    public LineaOrdenCompraDTO(String codigoItem, String descripcionItem,
                               double cantidad, double precioUnitario, double subtotal) {
        this.codigoItem = codigoItem;
        this.descripcionItem = descripcionItem;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public String getCodigoItem() { return codigoItem; }
    public String getDescripcionItem() { return descripcionItem; }
    public double getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return subtotal; }
}
