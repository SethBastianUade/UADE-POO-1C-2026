package mvc.model;

public abstract class MedioDePago {
    private int idMedioPago;
    private double importe;

    public MedioDePago(int idMedioPago, double importe) {
        this.idMedioPago = idMedioPago;
        this.importe = importe;
    }

    public abstract String getTipo();

    public double getImporte() { return importe; }
    public void setImporte(double importe) { this.importe = importe; }
    public int getIdMedioPago() { return idMedioPago; }
    public void setIdMedioPago(int idMedioPago) { this.idMedioPago = idMedioPago; }
}
