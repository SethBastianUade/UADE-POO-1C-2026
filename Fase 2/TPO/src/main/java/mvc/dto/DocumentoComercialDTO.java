package mvc.dto;

public class DocumentoComercialDTO {
    private String numeroDocumento;
    private String tipoDocumento;
    private String razonSocialProveedor;
    private String fechaEmision;
    private double importeTotal;
    private double saldoPendiente;
    private String estadoCancelacion;
    private String estadoRegistro;

    public DocumentoComercialDTO(String numeroDocumento, String tipoDocumento, String razonSocialProveedor,
                                 String fechaEmision, double importeTotal, double saldoPendiente,
                                 String estadoCancelacion, String estadoRegistro) {
        this.numeroDocumento = numeroDocumento;
        this.tipoDocumento = tipoDocumento;
        this.razonSocialProveedor = razonSocialProveedor;
        this.fechaEmision = fechaEmision;
        this.importeTotal = importeTotal;
        this.saldoPendiente = saldoPendiente;
        this.estadoCancelacion = estadoCancelacion;
        this.estadoRegistro = estadoRegistro;
    }

    public String getNumeroDocumento() { return numeroDocumento; }
    public String getTipoDocumento() { return tipoDocumento; }
    public String getRazonSocialProveedor() { return razonSocialProveedor; }
    public String getFechaEmision() { return fechaEmision; }
    public double getImporteTotal() { return importeTotal; }
    public double getSaldoPendiente() { return saldoPendiente; }
    public String getEstadoCancelacion() { return estadoCancelacion; }
    public String getEstadoRegistro() { return estadoRegistro; }
}
