package mvc.dto;

public class RetencionDTO {
    private String tipoImpuesto;
    private double baseImponible;
    private double porcentaje;
    private double montoRetenido;
    private String conExclusion;

    public RetencionDTO(String tipoImpuesto, double baseImponible, double porcentaje,
                        double montoRetenido, String conExclusion) {
        this.tipoImpuesto = tipoImpuesto;
        this.baseImponible = baseImponible;
        this.porcentaje = porcentaje;
        this.montoRetenido = montoRetenido;
        this.conExclusion = conExclusion;
    }

    public String getTipoImpuesto() { return tipoImpuesto; }
    public double getBaseImponible() { return baseImponible; }
    public double getPorcentaje() { return porcentaje; }
    public double getMontoRetenido() { return montoRetenido; }
    public String getConExclusion() { return conExclusion; }
}
