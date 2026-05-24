package mvc.dto;

public class ItemDTO {
    private String codigo;
    private String descripcion;
    private String tipo; 
    private String rubroDescripcion;
    private double precio;
    private boolean activo;
    private String stockActual;

    public ItemDTO(String codigo, String descripcion, String tipo, String rubroDescripcion, double precio, boolean activo, String stockActual) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.rubroDescripcion = rubroDescripcion;
        this.precio = precio;
        this.activo = activo;
        this.stockActual = stockActual;
    }

    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public String getTipo() { return tipo; }
    public String getRubroDescripcion() { return rubroDescripcion; }
    public double getPrecio() { return precio; }
    public boolean isActivo() { return activo; }
    public String getStockActual() { return stockActual; }

}
