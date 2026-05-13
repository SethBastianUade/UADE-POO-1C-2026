package mvc.model;

import java.time.LocalDate;

public class ChequePropio extends MedioDePago {
    private String nroCheque;
    private String banco;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String firmante;

    public ChequePropio(int idMedioPago, double importe, String nroCheque, String banco,
                        LocalDate fechaEmision, LocalDate fechaVencimiento, String firmante) {
        super(idMedioPago, importe);
        this.nroCheque = nroCheque;
        this.banco = banco;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.firmante = firmante;
    }

    @Override
    public String getTipo() { return "CHEQUE_PROPIO"; }

    public String getNroCheque() { return nroCheque; }
    public void setNroCheque(String nroCheque) { this.nroCheque = nroCheque; }
    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public String getFirmante() { return firmante; }
    public void setFirmante(String firmante) { this.firmante = firmante; }
}
