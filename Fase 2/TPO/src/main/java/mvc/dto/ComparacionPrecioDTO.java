package mvc.dto;

public class ComparacionPrecioDTO {
    private String razonSocialProveedor;
    private String cuit;
    private double precioAcordado;
    private String fechaAcuerdo;

    public ComparacionPrecioDTO(String razonSocialProveedor, String cuit,
                                double precioAcordado, String fechaAcuerdo) {
        this.razonSocialProveedor = razonSocialProveedor;
        this.cuit = cuit;
        this.precioAcordado = precioAcordado;
        this.fechaAcuerdo = fechaAcuerdo;
    }

    public String getRazonSocialProveedor() { return razonSocialProveedor; }
    public String getCuit() { return cuit; }
    public double getPrecioAcordado() { return precioAcordado; }
    public String getFechaAcuerdo() { return fechaAcuerdo; }
}
