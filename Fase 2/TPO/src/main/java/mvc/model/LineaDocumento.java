package mvc.model;

public class LineaDocumento {
    private int idLinea;
    private double cantidad;
    private double precioUnitario;
    private double alicuotaIVA;
    private Item item;

    public LineaDocumento(int idLinea, Item item, double cantidad,
                          double precioUnitario, double alicuotaIVA) {
        this.idLinea = idLinea;
        this.item = item;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.alicuotaIVA = alicuotaIVA;
    }

    public double getSubtotal() { return cantidad * precioUnitario; }

    public double getSubtotalConIVA() { return getSubtotal() * (1 + alicuotaIVA / 100); }

    public int getIdLinea() { return idLinea; }
    public void setIdLinea(int idLinea) { this.idLinea = idLinea; }
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public double getAlicuotaIVA() { return alicuotaIVA; }
    public void setAlicuotaIVA(double alicuotaIVA) { this.alicuotaIVA = alicuotaIVA; }
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
}
