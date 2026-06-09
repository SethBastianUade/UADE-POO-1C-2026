package mvc.dto;

public class MedioDePagoDTO {
    private String tipo;
    private String detalle;
    private double importe;

    public MedioDePagoDTO(String tipo, String detalle, double importe) {
        this.tipo = tipo;
        this.detalle = detalle;
        this.importe = importe;
    }

    public String getTipo() { return tipo; }
    public String getDetalle() { return detalle; }
    public double getImporte() { return importe; }
}
