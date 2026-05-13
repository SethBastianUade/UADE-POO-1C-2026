package mvc.model;

import mvc.enums.TipoImpuesto;

public class Retencion {
    private TipoImpuesto tipoImpuesto;
    private double baseImponible;
    private double porcentaje;
    private double montoRetenido;
    private double mniAplicado;

    public Retencion(TipoImpuesto tipoImpuesto, double baseImponible,
                     double porcentaje, double mniAplicado) {
        this.tipoImpuesto = tipoImpuesto;
        this.baseImponible = baseImponible;
        this.porcentaje = porcentaje;
        this.mniAplicado = mniAplicado;
        this.montoRetenido = calcularMonto();
    }

    public double calcularMonto() {
        double base = Math.max(0, baseImponible - mniAplicado);
        return base * (porcentaje / 100);
    }

    public TipoImpuesto getTipoImpuesto() { return tipoImpuesto; }
    public void setTipoImpuesto(TipoImpuesto tipoImpuesto) { this.tipoImpuesto = tipoImpuesto; }
    public double getBaseImponible() { return baseImponible; }
    public void setBaseImponible(double baseImponible) { this.baseImponible = baseImponible; }
    public double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(double porcentaje) { this.porcentaje = porcentaje; }
    public double getMontoRetenido() { return montoRetenido; }
    public void setMontoRetenido(double montoRetenido) { this.montoRetenido = montoRetenido; }
    public double getMniAplicado() { return mniAplicado; }
    public void setMniAplicado(double mniAplicado) { this.mniAplicado = mniAplicado; }
}
