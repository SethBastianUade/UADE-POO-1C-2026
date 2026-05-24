package mvc.model;

public abstract class Item {
    private int idItem;
    private String codigo;
    private String descripcion;
    private String unidadMedida;
    private double precioUnitarioBase;
    private double alicuotaIVA;
    private boolean activo;
    private Rubro rubro;

    public Item(int idItem, String codigo, String descripcion, String unidadMedida,
                double precioUnitarioBase, double alicuotaIVA, Rubro rubro) {
        this.idItem = idItem;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.unidadMedida = unidadMedida;
        this.precioUnitarioBase = precioUnitarioBase;
        this.alicuotaIVA = alicuotaIVA;
        this.rubro = rubro;
        this.activo = true;
    }

    public abstract String getTipoItem();
    public double calcularPrecioConIVA() {
        return precioUnitarioBase * (1 + alicuotaIVA / 100);
    }
    public int getIdItem() { return idItem; }
    public void setIdItem(int idItem) { this.idItem = idItem; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Rubro getRubro() { return rubro; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public double getPrecioUnitarioBase() { return precioUnitarioBase; }
    public void setPrecioUnitarioBase(double precioUnitarioBase) { this.precioUnitarioBase = precioUnitarioBase; }
    public double getAlicuotaIVA() { return alicuotaIVA; }
    public void setAlicuotaIVA(double alicuotaIVA) { this.alicuotaIVA = alicuotaIVA; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    
}
