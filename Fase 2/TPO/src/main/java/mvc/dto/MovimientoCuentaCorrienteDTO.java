package mvc.dto;

public class MovimientoCuentaCorrienteDTO {
    private String fecha;
    private String tipo;
    private String numeroDocumento;
    private double debe;
    private double haber;
    private double saldo;

    public MovimientoCuentaCorrienteDTO(String fecha, String tipo, String numeroDocumento,
                                        double debe, double haber, double saldo) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.numeroDocumento = numeroDocumento;
        this.debe = debe;
        this.haber = haber;
        this.saldo = saldo;
    }

    public String getFecha() { return fecha; }
    public String getTipo() { return tipo; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public double getDebe() { return debe; }
    public double getHaber() { return haber; }
    public double getSaldo() { return saldo; }
}
