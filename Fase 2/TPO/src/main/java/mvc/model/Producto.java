package mvc.model;

import java.time.LocalDate;

public class Producto extends Item {
    private String lote;
    private LocalDate fechaVencimiento;
    private int stockActual;
    private int stockMinimo;

    public Producto(int idItem, String codigo, String descripcion, String unidadMedida,
                    double precioUnitarioBase, double alicuotaIVA, Rubro rubro,
                    String lote, LocalDate fechaVencimiento, int stockActual, int stockMinimo) {
        super(idItem, codigo, descripcion, unidadMedida, precioUnitarioBase, alicuotaIVA,rubro);
        this.lote = lote;
        this.fechaVencimiento = fechaVencimiento;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
    }

    @Override
    public String getTipoItem() { return "PRODUCTO"; }

    public boolean requiereReposicion() {
        return stockActual <= stockMinimo;
    }

    public void incrementarStock(double cantidad) {
        if (cantidad > 0) {
            this.stockActual += cantidad;
        }
    }

    public void setStockActual(int nuevoStock) { this.stockActual = nuevoStock; }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public int getStockActual() { return stockActual; }
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
}
