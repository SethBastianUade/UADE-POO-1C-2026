package mvc.dto;

public class OrdenDeCompraDTO {
    private String numeroOC;
    private String fechaEmision;
    private String razonSocialProveedor;
    private double totalBruto;
    private String estado;
    private String operador;

    public OrdenDeCompraDTO(String numeroOC, String fechaEmision, String razonSocialProveedor,
                            double totalBruto, String estado, String operador) {
        this.numeroOC = numeroOC;
        this.fechaEmision = fechaEmision;
        this.razonSocialProveedor = razonSocialProveedor;
        this.totalBruto = totalBruto;
        this.estado = estado;
        this.operador = operador;
    }

    public String getNumeroOC() { return numeroOC; }
    public String getFechaEmision() { return fechaEmision; }
    public String getRazonSocialProveedor() { return razonSocialProveedor; }
    public double getTotalBruto() { return totalBruto; }
    public String getEstado() { return estado; }
    public String getOperador() { return operador; }
}
