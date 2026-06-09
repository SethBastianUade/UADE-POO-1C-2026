package mvc.dto;

public class LibroIVACompraDTO {
    private String fecha;
    private String cuit;
    private String razonSocialProveedor;
    private String tipoDocumento;
    private String numeroDocumento;
    private double baseImponible;
    private double montoIVA;
    private double total;

    public LibroIVACompraDTO(String fecha, String cuit, String razonSocialProveedor, String tipoDocumento,
                             String numeroDocumento, double baseImponible, double montoIVA, double total) {
        this.fecha = fecha;
        this.cuit = cuit;
        this.razonSocialProveedor = razonSocialProveedor;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.baseImponible = baseImponible;
        this.montoIVA = montoIVA;
        this.total = total;
    }

    public String getFecha() { return fecha; }
    public String getCuit() { return cuit; }
    public String getRazonSocialProveedor() { return razonSocialProveedor; }
    public String getTipoDocumento() { return tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public double getBaseImponible() { return baseImponible; }
    public double getMontoIVA() { return montoIVA; }
    public double getTotal() { return total; }
}
