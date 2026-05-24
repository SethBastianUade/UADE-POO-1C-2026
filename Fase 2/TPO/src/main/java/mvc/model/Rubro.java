package mvc.model;

public class Rubro {
    private int idRubro;
    private String codigo;
    private String descripcion;
    private boolean activo;

    public Rubro(int idRubro, String codigo, String descripcion) {
        this.idRubro = idRubro;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.activo = true;
    }

    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public int getIdRubro() { return idRubro; }
    public void setIdRubro(int idRubro) { this.idRubro = idRubro; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    @Override
    public String toString() {
        return codigo + " - " + descripcion;
    }
}
