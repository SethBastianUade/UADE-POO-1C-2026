package mvc.model;

public class TransferenciaBancaria extends MedioDePago {
    private String nroReferencia;
    private String cuentaOrigen;

    public TransferenciaBancaria(int idMedioPago, double importe,
                                 String nroReferencia, String cuentaOrigen) {
        super(idMedioPago, importe);
        this.nroReferencia = nroReferencia;
        this.cuentaOrigen = cuentaOrigen;
    }

    @Override
    public String getTipo() { return "TRANSFERENCIA_BANCARIA"; }

    public String getNroReferencia() { return nroReferencia; }
    public void setNroReferencia(String nroReferencia) { this.nroReferencia = nroReferencia; }
    public String getCuentaOrigen() { return cuentaOrigen; }
    public void setCuentaOrigen(String cuentaOrigen) { this.cuentaOrigen = cuentaOrigen; }
}
