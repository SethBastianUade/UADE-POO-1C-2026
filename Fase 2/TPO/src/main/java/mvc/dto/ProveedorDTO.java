package mvc.dto;

public class ProveedorDTO {
    private String cuit;
    private String razonSocial;
    private String condicionImpositiva;
    private double limiteDeuda;
    private boolean activo;

    public ProveedorDTO(String cuit, String razonSocial, String condicionImpositiva, double limiteDeuda, boolean activo) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.condicionImpositiva = condicionImpositiva;
        this.limiteDeuda = limiteDeuda;
        this.activo = activo;
    }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getCondicionImpositiva() { return condicionImpositiva; }
    public void setCondicionImpositiva(String condicionImpositiva) { this.condicionImpositiva = condicionImpositiva; }

    public double getLimiteDeuda() { return limiteDeuda; }
    public void setLimiteDeuda(double limiteDeuda) { this.limiteDeuda = limiteDeuda; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

}
