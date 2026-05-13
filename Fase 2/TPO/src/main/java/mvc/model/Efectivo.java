package mvc.model;

public class Efectivo extends MedioDePago {

    public Efectivo(int idMedioPago, double importe) {
        super(idMedioPago, importe);
    }

    @Override
    public String getTipo() { return "EFECTIVO"; }
}
