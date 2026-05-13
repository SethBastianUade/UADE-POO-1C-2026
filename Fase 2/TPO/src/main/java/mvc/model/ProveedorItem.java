package mvc.model;

import java.time.LocalDate;

public class ProveedorItem {
    private double precioAcordado;
    private LocalDate fechaAcuerdo;
    private boolean activo;
    private Item item;

    public ProveedorItem(Item item, double precioAcordado, LocalDate fechaAcuerdo) {
        this.item = item;
        this.precioAcordado = precioAcordado;
        this.fechaAcuerdo = fechaAcuerdo;
        this.activo = true;
    }

    public double getPrecioAcordado() { return precioAcordado; }
    public void setPrecioAcordado(double precioAcordado) { this.precioAcordado = precioAcordado; }
    public LocalDate getFechaAcuerdo() { return fechaAcuerdo; }
    public void setFechaAcuerdo(LocalDate fechaAcuerdo) { this.fechaAcuerdo = fechaAcuerdo; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
}
