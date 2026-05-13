package mvc.model;

import java.time.LocalDate;

public class ChequeDeTerceros extends MedioDePago {
    private String nroCheque;
    private String banco;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String firmanteOriginal;

    public ChequeDeTerceros(int idMedioPago, double importe, String nroCheque, String banco,
                            LocalDate fechaEmision, LocalDate fechaVencimiento, String firmanteOriginal) {
        super(idMedioPago, importe);
        this.nroCheque = nroCheque;
        this.banco = banco;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.firmanteOriginal = firmanteOriginal;
    }

    @Override
    public String getTipo() { return "CHEQUE_DE_TERCEROS"; }

    public String getNroCheque() { return nroCheque; }
    public void setNroCheque(String nroCheque) { this.nroCheque = nroCheque; }
    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public String getFirmanteOriginal() { return firmanteOriginal; }
    public void setFirmanteOriginal(String firmanteOriginal) { this.firmanteOriginal = firmanteOriginal; }
}
