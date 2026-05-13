package mvc.model;

import java.time.LocalDate;

public class NotaDeDebito extends DocumentoComercial {
    private String motivoDebito;
    private Factura facturaOrigen;

    public NotaDeDebito(int idDocumento, String numeroDocumento, LocalDate fechaEmision,
                        double importeTotal, Proveedor proveedor,
                        String motivoDebito, Factura facturaOrigen) {
        super(idDocumento, numeroDocumento, fechaEmision, importeTotal, proveedor);
        this.motivoDebito = motivoDebito;
        this.facturaOrigen = facturaOrigen;
    }

    @Override
    public String getTipoDocumento() { return "NOTA_DE_DEBITO"; }

    public String getMotivoDebito() { return motivoDebito; }
    public void setMotivoDebito(String motivoDebito) { this.motivoDebito = motivoDebito; }
    public Factura getFacturaOrigen() { return facturaOrigen; }
    public void setFacturaOrigen(Factura facturaOrigen) { this.facturaOrigen = facturaOrigen; }
}
