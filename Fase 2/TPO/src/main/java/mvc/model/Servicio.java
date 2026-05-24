package mvc.model;

public class Servicio extends Item {
    private String modalidadPrestacion;
    private int duracionEstimadaHoras;
    private String requisitosTecnicos;

    public Servicio(int idItem, String codigo, String descripcion, String unidadMedida,
                    double precioUnitarioBase, double alicuotaIVA,Rubro rubro,
                    String modalidadPrestacion, int duracionEstimadaHoras, String requisitosTecnicos) {
        super(idItem, codigo, descripcion, unidadMedida, precioUnitarioBase, alicuotaIVA,rubro);
        this.modalidadPrestacion = modalidadPrestacion;
        this.duracionEstimadaHoras = duracionEstimadaHoras;
        this.requisitosTecnicos = requisitosTecnicos;
    }

    @Override
    public String getTipoItem() { return "SERVICIO"; }

    public String getModalidadPrestacion() { return modalidadPrestacion; }
    public void setModalidadPrestacion(String modalidadPrestacion) { this.modalidadPrestacion = modalidadPrestacion; }
    public int getDuracionEstimadaHoras() { return duracionEstimadaHoras; }
    public void setDuracionEstimadaHoras(int duracionEstimadaHoras) { this.duracionEstimadaHoras = duracionEstimadaHoras; }
    public String getRequisitosTecnicos() { return requisitosTecnicos; }
    public void setRequisitosTecnicos(String requisitosTecnicos) { this.requisitosTecnicos = requisitosTecnicos; }
}
