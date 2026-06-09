package mvc.dto;

public class TotalDiarioDTO {
    private String fecha;
    private int cantidad;
    private double importeTotal;

    public TotalDiarioDTO(String fecha, int cantidad, double importeTotal) {
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.importeTotal = importeTotal;
    }

    public String getFecha() { return fecha; }
    public int getCantidad() { return cantidad; }
    public double getImporteTotal() { return importeTotal; }
}
