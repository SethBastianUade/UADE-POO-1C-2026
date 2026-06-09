package mvc.dto;

public class TotalRetenidoDTO {
    private String tipoImpuesto;
    private int cantidadRetenciones;
    private double totalRetenido;

    public TotalRetenidoDTO(String tipoImpuesto, int cantidadRetenciones, double totalRetenido) {
        this.tipoImpuesto = tipoImpuesto;
        this.cantidadRetenciones = cantidadRetenciones;
        this.totalRetenido = totalRetenido;
    }

    public String getTipoImpuesto() { return tipoImpuesto; }
    public int getCantidadRetenciones() { return cantidadRetenciones; }
    public double getTotalRetenido() { return totalRetenido; }
}
