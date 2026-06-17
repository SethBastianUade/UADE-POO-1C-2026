package mvc.model;
import mvc.enums.CondicionImpositiva;
import mvc.enums.EstadoCancelacionDocumento;
import mvc.enums.TipoImpuesto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Proveedor {
    // Alícuotas de retención según condición impositiva (regla de negocio del TPO)
    private static final double PORC_IVA_RESP_INSCRIPTO = 10.5;
    private static final double PORC_GANANCIAS_RESP_INSCRIPTO = 3.0;
    private static final double PORC_IIBB_RESP_INSCRIPTO = 2.5;
    private static final double PORC_IIBB_MONOTRIBUTISTA = 1.0;
    // Mínimos no imponibles por impuesto (IVA no tiene MNI)
    private static final double MNI_GANANCIAS = 150000.0;
    private static final double MNI_IIBB = 50000.0;

    private int idProveedor;
    private String cuit;
    private String razonSocial;
    private String nombreComercial;
    private String domicilio;
    private String telefono;
    private String correoElectronico;
    private CondicionImpositiva condicionImpositiva;
    private String nroInscripcionIIBB;
    private LocalDate fechaInicioActividades;
    private double limiteDeudaAutorizado;
    private boolean activo;

    
    private List<Rubro> rubros;
    private List<CertificadoExclusion> certificados;
    private List<ProveedorItem> preciosAcordados = new java.util.ArrayList<>();

    // Constructor
    public Proveedor(int idProveedor, String cuit, String razonSocial, String nombreComercial,
                     String domicilio, String telefono, String correoElectronico,
                     CondicionImpositiva condicionImpositiva, String nroInscripcionIIBB,
                     LocalDate fechaInicioActividades, double limiteDeudaAutorizado) {
        this.idProveedor = idProveedor;
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.nombreComercial = nombreComercial;
        this.domicilio = domicilio;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.condicionImpositiva = condicionImpositiva;
        this.nroInscripcionIIBB = nroInscripcionIIBB;
        this.fechaInicioActividades = fechaInicioActividades;
        this.limiteDeudaAutorizado = limiteDeudaAutorizado;
        this.activo = true;

        this.rubros = new ArrayList<>();
        this.certificados = new ArrayList<>();
    }

    // Valida formato y dígito verificador del CUIT (regla de §2.1 del enunciado).
    // Acepta el CUIT con o sin guiones/espacios; debe quedar en 11 dígitos.
    public static boolean cuitEsValido(String cuit) {
        if (cuit == null) {
            return false;
        }
        String soloDigitos = cuit.replaceAll("[^0-9]", "");
        if (soloDigitos.length() != 11) {
            return false;
        }
        int[] pesos = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
        int suma = 0;
        for (int i = 0; i < 10; i++) {
            suma += (soloDigitos.charAt(i) - '0') * pesos[i];
        }
        int resto = suma % 11;
        int dv = 11 - resto;
        if (dv == 11) {
            dv = 0;
        } else if (dv == 10) {
            return false; // dígito verificador 10 no es válido
        }
        return dv == (soloDigitos.charAt(10) - '0');
    }

    // Métodos de negocio
    // Recibe la lista de documentos del sistema porque el Proveedor no la posee;
    // la regla de qué documentos forman su deuda sí es responsabilidad del Proveedor.
    public double calcularDeudaActual(List<DocumentoComercial> documentos) {
        double deuda = 0.0;
        for (DocumentoComercial doc : documentos) {
            boolean esDeEsteProveedor = doc.getProveedor() == this;
            boolean estaImpago = doc.getEstadoCancelacion() == EstadoCancelacionDocumento.PENDIENTE
                    || doc.getEstadoCancelacion() == EstadoCancelacionDocumento.PARCIALMENTE_CANCELADO;
            if (esDeEsteProveedor && estaImpago) {
                // Impacto polimórfico: facturas/ND suman su saldo pendiente,
                // las notas de crédito lo restan
                deuda += doc.getImpactoDeuda();
            }
        }
        return deuda;
    }

    public double calcularMontoComprometido(List<DocumentoComercial> documentos, double montoNuevaOC) {
        return calcularDeudaActual(documentos) + montoNuevaOC;
    }

    // Cálculo de retenciones para un pago a este proveedor.
    // El Proveedor es quien posee la condición impositiva y los certificados
    // de exclusión, por eso la regla vive acá y no en el controller.
    public List<Retencion> calcularRetenciones(double baseImponible, LocalDate fecha) {
        List<Retencion> retenciones = new ArrayList<>();
        for (TipoImpuesto tipo : TipoImpuesto.values()) {
            // PASO 1: si hay certificado de exclusión vigente, NO se retiene este impuesto
            if (tieneExclusionActiva(tipo, fecha)) {
                continue;
            }
            // PASO 2: alícuota según la condición impositiva del proveedor
            double porcentaje = porcentajeRetencion(tipo);
            if (porcentaje <= 0) {
                continue; // no corresponde retener este impuesto
            }
            // PASO 3: la Retencion aplica el MNI: max(0, base - MNI) * (porcentaje / 100)
            retenciones.add(new Retencion(tipo, baseImponible, porcentaje, mniPara(tipo)));
        }
        return retenciones;
    }

    private double porcentajeRetencion(TipoImpuesto tipo) {
        switch (condicionImpositiva) {
            case MONOTRIBUTISTA:
                return (tipo == TipoImpuesto.IIBB) ? PORC_IIBB_MONOTRIBUTISTA : 0.0;
            case EXENTO:
                return 0.0;
            // RESPONSABLE_INSCRIPTO; a CONSUMIDOR_FINAL se le aplican las
            // mismas alícuotas máximas (criterio conservador)
            default:
                switch (tipo) {
                    case IVA: return PORC_IVA_RESP_INSCRIPTO;
                    case GANANCIAS: return PORC_GANANCIAS_RESP_INSCRIPTO;
                    case IIBB: return PORC_IIBB_RESP_INSCRIPTO;
                    default: return 0.0;
                }
        }
    }

    private double mniPara(TipoImpuesto tipo) {
        switch (tipo) {
            case GANANCIAS: return MNI_GANANCIAS;
            case IIBB: return MNI_IIBB;
            default: return 0.0; // IVA no tiene mínimo no imponible
        }
    }

    // Getters y Setters
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public String getCuit() { return cuit; }
    public void setCuit(String cuit) { this.cuit = cuit; }

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public CondicionImpositiva getCondicionImpositiva() { return condicionImpositiva; }
    public void setCondicionImpositiva(CondicionImpositiva condicionImpositiva) { this.condicionImpositiva = condicionImpositiva; }

    public String getNroInscripcionIIBB() { return nroInscripcionIIBB; }
    public void setNroInscripcionIIBB(String nroInscripcionIIBB) { this.nroInscripcionIIBB = nroInscripcionIIBB; }

    public LocalDate getFechaInicioActividades() { return fechaInicioActividades; }
    public void setFechaInicioActividades(LocalDate fechaInicioActividades) { this.fechaInicioActividades = fechaInicioActividades; }

    public double getLimiteDeudaAutorizado() { return limiteDeudaAutorizado; }
    public void setLimiteDeudaAutorizado(double limiteDeudaAutorizado) { this.limiteDeudaAutorizado = limiteDeudaAutorizado; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public List<Rubro> getRubros() { return rubros; }public List<Rubro> getRubrosAsociados() {
        return rubros;
    }

    public boolean agregarRubro(Rubro rubro) {
        // Evitamos agregar el mismo rubro dos veces
        if (!rubros.contains(rubro)) {
            rubros.add(rubro);
            return true;
        }
        return false;
    }
    public boolean quitarRubro(Rubro rubro) {
        return rubros.remove(rubro);
    }

    public List<CertificadoExclusion> getCertificados() {
        return certificados;
    }

    public void agregarCertificado(CertificadoExclusion certificado) {
        this.certificados.add(certificado);
    }

    public boolean tieneExclusionActiva(TipoImpuesto tipo, LocalDate fecha) {
        for (CertificadoExclusion cert : certificados) {
            if (cert.getTipoImpuesto() == tipo && cert.estaVigente(fecha)) {
                return true;
            }
        }
        return false;
    }

    public List<ProveedorItem> getPreciosAcordados() {
        return preciosAcordados;
    }

    public ProveedorItem getPrecioAcordadoPara(Item item) {
        for (ProveedorItem pi : preciosAcordados) {
            if (pi.getItem().getCodigo().equalsIgnoreCase(item.getCodigo())) {
                return pi;
            }
        }
        return null;
    }

    public void acordarPrecioItem(Item item, double precio) {
        // Si ya existía un acuerdo para ese ítem, lo actualizamos
        for (ProveedorItem pi : preciosAcordados) {
            if (pi.getItem().getCodigo().equals(item.getCodigo())) {
                pi.setPrecioAcordado(precio);
                return;
            }
        }
        // Si no existía, creamos uno nuevo
        preciosAcordados.add(new ProveedorItem(item, precio, LocalDate.now()));
    }

}
