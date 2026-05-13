package mvc.model;
import mvc.enums.TipoImpuesto;

import java.time.LocalDate;

public class CertificadoExclusion {
    private int idCertificado;
    private String numeroCertificado;
    private TipoImpuesto tipoImpuesto;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    public CertificadoExclusion(int idCertificado, String numeroCertificado,
                                TipoImpuesto tipoImpuesto, LocalDate fechaDesde, LocalDate fechaHasta) {
        this.idCertificado = idCertificado;
        this.numeroCertificado = numeroCertificado;
        this.tipoImpuesto = tipoImpuesto;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
    }

    public boolean estaVigente(LocalDate fecha) {
        return !fecha.isBefore(fechaDesde) && !fecha.isAfter(fechaHasta);
    }

    public int getIdCertificado() { return idCertificado; }
    public void setIdCertificado(int idCertificado) { this.idCertificado = idCertificado; }
    public String getNumeroCertificado() { return numeroCertificado; }
    public void setNumeroCertificado(String numeroCertificado) { this.numeroCertificado = numeroCertificado; }
    public TipoImpuesto getTipoImpuesto() { return tipoImpuesto; }
    public void setTipoImpuesto(TipoImpuesto tipoImpuesto) { this.tipoImpuesto = tipoImpuesto; }
    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }
    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }
}