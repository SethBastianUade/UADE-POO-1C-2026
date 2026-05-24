package mvc.dto;

public class RubroDTO {

    // 1. Atributos privados
    private String codigo;
    private String descripcion;
    private boolean activo;

    // 2. Constructor con todos los parámetros
    public RubroDTO(String codigo, String descripcion, boolean activo) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    // 3. Getters y Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Nota: Para los booleanos, el getter tradicionalmente se llama "is" en lugar de "get"
    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}

