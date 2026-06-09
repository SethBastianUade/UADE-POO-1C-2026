package mvc.dto;

public class OrdenDePagoDTO {
    private String numeroOP;
    private String razonSocialProveedor;
    private String fechaEmision;
    private double totalBruto;
    private double totalRetenido;
    private double totalNeto;
    private String operador;

    public OrdenDePagoDTO(String numeroOP, String razonSocialProveedor, String fechaEmision,
                          double totalBruto, double totalRetenido, double totalNeto, String operador) {
        this.numeroOP = numeroOP;
        this.razonSocialProveedor = razonSocialProveedor;
        this.fechaEmision = fechaEmision;
        this.totalBruto = totalBruto;
        this.totalRetenido = totalRetenido;
        this.totalNeto = totalNeto;
        this.operador = operador;
    }

    public String getNumeroOP() { return numeroOP; }
    public String getRazonSocialProveedor() { return razonSocialProveedor; }
    public String getFechaEmision() { return fechaEmision; }
    public double getTotalBruto() { return totalBruto; }
    public double getTotalRetenido() { return totalRetenido; }
    public double getTotalNeto() { return totalNeto; }
    public String getOperador() { return operador; }
}
