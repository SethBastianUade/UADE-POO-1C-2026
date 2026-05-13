package mvc.model;

public class DocumentoPago {
    private double montoAplicado;
    private DocumentoComercial documentoComercial;

    public DocumentoPago(DocumentoComercial documentoComercial, double montoAplicado) {
        this.documentoComercial = documentoComercial;
        this.montoAplicado = montoAplicado;
    }

    public double getMontoAplicado() { return montoAplicado; }
    public void setMontoAplicado(double montoAplicado) { this.montoAplicado = montoAplicado; }
    public DocumentoComercial getDocumentoComercial() { return documentoComercial; }
    public void setDocumentoComercial(DocumentoComercial documentoComercial) { this.documentoComercial = documentoComercial; }
}
