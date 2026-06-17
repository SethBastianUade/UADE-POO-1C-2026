package mvc.model;

import java.time.LocalDate;

public class Factura extends DocumentoComercial {
    private String cae;
    private LocalDate fechaVencimientoCAE;
    private double baseImponibleIVA;
    private double montoIVA;

    public Factura(int idDocumento, String numeroDocumento, LocalDate fechaEmision,
                   double importeTotal, Proveedor proveedor,
                   String cae, LocalDate fechaVencimientoCAE,
                   double baseImponibleIVA, double montoIVA) {
        super(idDocumento, numeroDocumento, fechaEmision, importeTotal, proveedor);
        this.cae = cae;
        this.fechaVencimientoCAE = fechaVencimientoCAE;
        this.baseImponibleIVA = baseImponibleIVA;
        this.montoIVA = montoIVA;
    }

    @Override
    public String getTipoDocumento() { return "FACTURA"; }

    // La factura usa el IVA sellado por AFIP, no el derivado de las líneas.
    // getMontoIVA() ya está más abajo (devuelve montoIVA) y también sobrescribe al base.
    @Override
    public double getBaseImponible() { return baseImponibleIVA; }

    public String getCae() { return cae; }
    public void setCae(String cae) { this.cae = cae; }
    public LocalDate getFechaVencimientoCAE() { return fechaVencimientoCAE; }
    public void setFechaVencimientoCAE(LocalDate fechaVencimientoCAE) { this.fechaVencimientoCAE = fechaVencimientoCAE; }
    public double getBaseImponibleIVA() { return baseImponibleIVA; }
    public void setBaseImponibleIVA(double baseImponibleIVA) { this.baseImponibleIVA = baseImponibleIVA; }
    @Override
    public double getMontoIVA() { return montoIVA; }
    public void setMontoIVA(double montoIVA) { this.montoIVA = montoIVA; }
}
