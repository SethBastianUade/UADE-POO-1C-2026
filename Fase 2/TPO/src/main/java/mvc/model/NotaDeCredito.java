package mvc.model;

import java.time.LocalDate;

public class NotaDeCredito extends DocumentoComercial {
    private String motivoCredito;
    private Factura facturaOrigen;

    public NotaDeCredito(int idDocumento, String numeroDocumento, LocalDate fechaEmision,
                         double importeTotal, Proveedor proveedor,
                         String motivoCredito, Factura facturaOrigen) {
        super(idDocumento, numeroDocumento, fechaEmision, importeTotal, proveedor);
        this.motivoCredito = motivoCredito;
        this.facturaOrigen = facturaOrigen;
    }

    @Override
    public String getTipoDocumento() { return "NOTA_DE_CREDITO"; }

    @Override
    public double getImpactoDeuda() { return -getSaldoPendiente(); }

    public String getMotivoCredito() { return motivoCredito; }
    public void setMotivoCredito(String motivoCredito) { this.motivoCredito = motivoCredito; }
    public Factura getFacturaOrigen() { return facturaOrigen; }
    public void setFacturaOrigen(Factura facturaOrigen) { this.facturaOrigen = facturaOrigen; }
}
