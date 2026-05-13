package mvc.model;

public class LineaOrdenCompra {
    private int idLineaOrdenCompra;
    private double cantidad;
    private double precioUnitarioAcordado;
    private double cantidadRecibida;
    private Item item;

    public LineaOrdenCompra(int idLineaOrdenCompra, Item item, double cantidad, double precioUnitarioAcordado) {
        this.idLineaOrdenCompra = idLineaOrdenCompra;
        this.item = item;
        this.cantidad = cantidad;
        this.precioUnitarioAcordado = precioUnitarioAcordado;
        this.cantidadRecibida = 0;
    }

    public double getSubtotal() { return cantidad * precioUnitarioAcordado; }

    public boolean estaCompletamenteRecibida() { return cantidadRecibida >= cantidad; }

    public double getCantidadPendiente() { return cantidad - cantidadRecibida; }

    public void registrarRecepcion(double cantidad) {
        this.cantidadRecibida += cantidad;
    }

    public void actualizarCantidadRecibida(double cantidad) { this.cantidadRecibida = cantidad; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    public int getIdLineaOrdenCompra() { return idLineaOrdenCompra; }
    public void setIdLineaOrdenCompra(int idLineaOrdenCompra) { this.idLineaOrdenCompra = idLineaOrdenCompra; }
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public double getPrecioUnitarioAcordado() { return precioUnitarioAcordado; }
    public void setPrecioUnitarioAcordado(double precioUnitarioAcordado) { this.precioUnitarioAcordado = precioUnitarioAcordado; }
    public double getCantidadRecibida() { return cantidadRecibida; }
    public void setCantidadRecibida(double cantidadRecibida) { this.cantidadRecibida = cantidadRecibida; }
}
