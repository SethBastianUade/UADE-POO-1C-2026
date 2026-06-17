package mvc.dto;

public class RecepcionLineaDTO {
    private int idLinea;
    private String codigo;
    private String descripcion;
    private double cantidadPedida;
    private double cantidadRecibida;
    private double cantidadPendiente;

    public RecepcionLineaDTO(int idLinea, String codigo, String descripcion,
                             double cantidadPedida, double cantidadRecibida, double cantidadPendiente) {
        this.idLinea = idLinea;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidadPedida = cantidadPedida;
        this.cantidadRecibida = cantidadRecibida;
        this.cantidadPendiente = cantidadPendiente;
    }

    public int getIdLinea() { return idLinea; }
    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public double getCantidadPedida() { return cantidadPedida; }
    public double getCantidadRecibida() { return cantidadRecibida; }
    public double getCantidadPendiente() { return cantidadPendiente; }
}
