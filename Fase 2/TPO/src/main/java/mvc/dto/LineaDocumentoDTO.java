package mvc.dto;

public class LineaDocumentoDTO {
    private String codigoItem;
    private String descripcionItem;
    private double cantidad;
    private double precioUnitario;
    private double alicuotaIVA;
    private double subtotal;

    public LineaDocumentoDTO(String codigoItem, String descripcionItem, double cantidad,
                             double precioUnitario, double alicuotaIVA, double subtotal) {
        this.codigoItem = codigoItem;
        this.descripcionItem = descripcionItem;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.alicuotaIVA = alicuotaIVA;
        this.subtotal = subtotal;
    }

    public String getCodigoItem() { return codigoItem; }
    public String getDescripcionItem() { return descripcionItem; }
    public double getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getAlicuotaIVA() { return alicuotaIVA; }
    public double getSubtotal() { return subtotal; }
}
