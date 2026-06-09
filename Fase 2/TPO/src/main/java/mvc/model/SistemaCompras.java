package mvc.model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import mvc.enums.CondicionImpositiva;
import mvc.enums.EstadoOrdenCompra;
import mvc.enums.EstadoRegistroDocumento;
import mvc.enums.RolUsuario;
import mvc.enums.TipoImpuesto;

public class SistemaCompras {
    // 1. La variable estática que guardará la única instancia de la clase
    private static SistemaCompras instanciaUnica;

    // 2. Las listas que simulan la base de datos (según tu diagrama UML)
    private List<OrdenDeCompra> ordenesDeCompra;
    private List<Proveedor> proveedores;
    private List<DocumentoComercial> documentosComerciales;
    private List<OrdenDePago> ordenesDePago;
    private List<Usuario> usuarios;
    private List<Item> items;
    private List<Rubro> rubros;
    private Usuario usuarioLogueado; // Para saber quién está usando el sistema en cada momento

    // Variable auxiliar para autoincrementar el ID de los rubros
    private int contadorIdRubros = 1;
    private int contadorIdItems = 1;
    private int contadorIdProveedores = 1;
    private int contadorIdOC = 1;
    private int contadorIdDocumentos = 1;
    private int contadorIdOP = 1;

    // 3. Constructor 
    private SistemaCompras() {
        // Inicializamos todas las listas vacías para evitar NullPointerException
        ordenesDeCompra = new ArrayList<>();
        proveedores = new ArrayList<>();
        documentosComerciales = new ArrayList<>();
        ordenesDePago = new ArrayList<>();
        usuarios = new ArrayList<>();
        items = new ArrayList<>();
        rubros = new ArrayList<>();
        usuarios.add(new Usuario("admin", "1234", "Juan", "Pérez", RolUsuario.SUPERVISOR));
        usuarios.add(new Usuario("operador", "1234", "María", "Gómez", RolUsuario.OPERADOR));

        cargarDatosDePrueba(); // comentar esta línea para arrancar el sistema vacío
    }
    
    public static synchronized SistemaCompras getInstance() {
        if (instanciaUnica == null) {
            instanciaUnica = new SistemaCompras();
        }
        return instanciaUnica;
    }

    public Usuario autenticarUsuario(String nombreUsuario, String password) {
        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equals(nombreUsuario) && 
                u.getPasswordHash().equals(password) && 
                u.isActivo()) {
                
                this.usuarioLogueado = u;
                return u; // Retorna el usuario si los datos coinciden
            }
        }
        return null; // Retorna nulo si falla
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    // 4. Método para obtener la instancia 
 

    // Métodos para la gestión de rubros
    public List<Rubro> getRubros() {
        return rubros; // Devuelve la lista real para que el controlador la lea
    }
    public void agregarRubro(String codigo, String descripcion) {
        Rubro nuevoRubro = new Rubro(contadorIdRubros, codigo, descripcion);
        rubros.add(nuevoRubro);
        contadorIdRubros++;
    }
    public Rubro buscarRubro(String codigo) {
        for (Rubro r : rubros) {
            if (r.getCodigo().equalsIgnoreCase(codigo)) {
                return r;
            }
        }
        return null; // Si no lo encuentra
    }
    public boolean modificarRubro(String codigo, String nuevaDescripcion) {
        Rubro rubro = buscarRubro(codigo);
        if (rubro != null) {
            rubro.setDescripcion(nuevaDescripcion);
            return true;
        }
        return false;
    }
    public boolean cambiarEstadoRubro(String codigo) {
        Rubro rubro = buscarRubro(codigo);
        if (rubro != null) {
            rubro.setActivo(!rubro.isActivo()); // Si es true pasa a false, y viceversa
            return true;
        }
        return false;
    }
    public void agregarProducto(String cod, String desc, String uni, double precio, 
                                double iva, Rubro rubro, String lote, LocalDate vto,int stockActual, int stockMin) {
        Producto p = new Producto(contadorIdItems++, cod, desc, uni, precio, iva, rubro, lote, vto, stockActual, stockMin);
        items.add(p);
    }
    public void agregarServicio(String cod, String desc, String uni, double precio, 
                                double iva, Rubro rubro, String mod, int horas, String req) {
        Servicio s = new Servicio(contadorIdItems++, cod, desc, uni, precio, iva, rubro, mod, horas, req);
        items.add(s);
    }
    public List<Item> getItems() {
        return items;
    }
    public void agregarProveedor(String cuit, String razonSocial, String nombreComercial, String domicilio, String telefono, 
                                 String correo, CondicionImpositiva condicion,String nroInscripcionIIBB,LocalDate fechaInicioActividades, double limite) {
        Proveedor p = new Proveedor(contadorIdProveedores++,cuit, razonSocial,  nombreComercial,
                     domicilio, telefono, correo,condicion,  nroInscripcionIIBB, fechaInicioActividades, limite);
        proveedores.add(p);
    }

    public Proveedor buscarProveedorPorCuit(String cuit) {
        for (Proveedor p : proveedores) {
            if (p.getCuit().equals(cuit)) {
                return p;
            }
        }
        return null;
    }

    public boolean modificarProveedor(String cuit, String razonSocial, String telefono, 
                                      String correo, CondicionImpositiva condicion, double limite) {
        Proveedor p = buscarProveedorPorCuit(cuit);
        if (p != null) {
            p.setRazonSocial(razonSocial);
            p.setTelefono(telefono);
            p.setCorreoElectronico(correo);
            p.setCondicionImpositiva(condicion);
            p.setLimiteDeudaAutorizado(limite);
            return true;
        }
        return false;
    }

    public boolean cambiarEstadoProveedor(String cuit) {
        Proveedor p = buscarProveedorPorCuit(cuit);
        if (p != null) {
            p.setActivo(!p.isActivo());
            return true;
        }
        return false;
    }

    public List<Proveedor> getProveedores() {
        return proveedores;
    }

    public boolean asignarRubroAProveedor(String cuitProveedor, String codigoRubro) {

        Proveedor p = buscarProveedorPorCuit(cuitProveedor);
        Rubro r = buscarRubro(codigoRubro); 

        if (p != null && r != null) {
            return p.agregarRubro(r);
        }
        return false;
    }

    public boolean desvincularRubroDeProveedor(String cuitProveedor, String codigoRubro) {
        Proveedor p = buscarProveedorPorCuit(cuitProveedor);
        Rubro r = buscarRubro(codigoRubro);

        if (p != null && r != null) {
            return p.quitarRubro(r);
        }
        return false;
    }

    public void agregarCertificadoAProveedor(String cuit, String numero, TipoImpuesto tipo, LocalDate desde, LocalDate hasta) {
        Proveedor p = buscarProveedorPorCuit(cuit);
        if (p != null) {
            // Un id autoincremental simple para el certificado
            int idCert = p.getCertificados().size() + 1;
            CertificadoExclusion nuevo = new CertificadoExclusion(idCert, numero, tipo, desde, hasta);
            p.agregarCertificado(nuevo);
        }
    }

    public void registrarPrecioAcordado(String cuit, String codigoItem, double precio) {
        Proveedor p = buscarProveedorPorCuit(cuit);
        // Buscamos el ítem en la lista global de ítems del sistema
        Item itemEncontrado = null;
        for (Item i : items) {
            if (i.getCodigo().equalsIgnoreCase(codigoItem)) {
                itemEncontrado = i;
                break;
            }
        }

        if (p != null && itemEncontrado != null) {
            p.acordarPrecioItem(itemEncontrado, precio);
        }
    }

    // ============================================================
    // DATOS DE PRUEBA (Parte 5)
    // Se cargan usando los métodos de negocio reales para que todos
    // los estados queden consistentes (OC emitidas, facturas validadas,
    // OP con retenciones aplicadas, etc.)
    // ============================================================
    private void cargarDatosDePrueba() {
        // Rubros e ítems
        agregarRubro("INS", "Insumos Médicos");
        agregarRubro("SRV", "Servicios Generales");
        Rubro rubroInsumos = buscarRubro("INS");
        Rubro rubroServicios = buscarRubro("SRV");
        agregarProducto("P001", "Guantes de látex x100", "caja", 5000, 21, rubroInsumos,
                "L-001", LocalDate.now().plusMonths(18), 200, 50);
        agregarProducto("P002", "Barbijos N95 x20", "caja", 8000, 21, rubroInsumos,
                "L-002", LocalDate.now().plusMonths(24), 150, 30);
        agregarServicio("S001", "Mantenimiento de autoclaves", "hora", 15000, 21, rubroServicios,
                "Presencial", 4, "Técnico matriculado");

        // Proveedores: uno RI con exclusión de IVA vigente, otro monotributista
        agregarProveedor("30-11111111-1", "Insumos del Sur S.A.", "Insumos del Sur", "Av. Mitre 1234",
                "11-4444-1111", "ventas@insumosdelsur.com", CondicionImpositiva.RESPONSABLE_INSCRIPTO,
                "901-111", LocalDate.of(2015, 3, 1), 500000);
        agregarProveedor("20-22222222-2", "Servicios Médicos SRL", "SerMed", "Calle Falsa 742",
                "11-5555-2222", "info@sermed.com", CondicionImpositiva.MONOTRIBUTISTA,
                "902-222", LocalDate.of(2019, 8, 15), 200000);
        Proveedor p1 = buscarProveedorPorCuit("30-11111111-1");
        Proveedor p2 = buscarProveedorPorCuit("20-22222222-2");
        asignarRubroAProveedor(p1.getCuit(), "INS");
        asignarRubroAProveedor(p2.getCuit(), "SRV");
        registrarPrecioAcordado(p1.getCuit(), "P001", 4800);
        registrarPrecioAcordado(p1.getCuit(), "P002", 7900);
        registrarPrecioAcordado(p2.getCuit(), "P001", 4950); // mismo ítem, otro precio (comparación)
        agregarCertificadoAProveedor(p1.getCuit(), "EXCL-IVA-001", TipoImpuesto.IVA,
                LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(5));

        Usuario admin = usuarios.get(0);

        // OC emitida de p1 (total 271000 <= límite 500000)
        OrdenDeCompra oc1 = crearOrdenDeCompra(p1, LocalDate.now().plusDays(7), admin);
        agregarLineaOC(oc1, buscarItemPorCodigo("P001"), 40, 4800);
        agregarLineaOC(oc1, buscarItemPorCodigo("P002"), 10, 7900);
        confirmarOrdenDeCompra(oc1); // EMITIDA

        // Factura de p1 amparada por la OC (queda APROBADA)
        Factura f1 = registrarFactura(p1, "FC-0001-00001234", LocalDate.now().minusDays(5), 327910,
                "CAE12345678901", LocalDate.now().plusDays(10), 271000, 56910);
        agregarLineaDocumento(f1, buscarItemPorCodigo("P001"), 40, 4800, 21);
        agregarLineaDocumento(f1, buscarItemPorCodigo("P002"), 10, 7900, 21);
        validarDocumentoConOC(f1);

        // Factura de p2 sin OC que la ampare (queda OBSERVADA)
        Factura f2 = registrarFactura(p2, "FC-0002-00000077", LocalDate.now().minusDays(3), 18150,
                "CAE98765432109", LocalDate.now().plusDays(10), 15000, 3150);
        agregarLineaDocumento(f2, buscarItemPorCodigo("S001"), 1, 15000, 21);
        validarDocumentoConOC(f2);

        // NC de p1 sobre la factura (resta deuda)
        registrarNotaDeCredito(p1, "NC-0001-00000005", LocalDate.now().minusDays(2),
                5875, "Descuento por pronto pago", f1);

        // OC de p2 que supera su límite de crédito (queda PENDIENTE_APROBACION)
        OrdenDeCompra oc2 = crearOrdenDeCompra(p2, LocalDate.now().plusDays(5), admin);
        agregarLineaOC(oc2, buscarItemPorCodigo("S001"), 20, 15000);
        confirmarOrdenDeCompra(oc2);

        // OP parcial de la factura de p1: bruto 200000.
        // p1 es RI con exclusión de IVA vigente: retiene Ganancias 1500 + IIBB 3750
        // -> neto 194750, pagado por transferencia. f1 queda PARCIALMENTE_CANCELADO.
        List<DocumentoPago> documentosPago = new ArrayList<>();
        documentosPago.add(new DocumentoPago(f1, 200000));
        List<MedioDePago> medios = new ArrayList<>();
        medios.add(new TransferenciaBancaria(1, 194750, "TRF-000123", "CTA-SANATORIO-01"));
        emitirOrdenDePago(p1, documentosPago, medios, admin);
    }

    // ============================================================
    // MÓDULO: ÓRDENES DE COMPRA (Parte 2)
    // ============================================================
    public OrdenDeCompra crearOrdenDeCompra(Proveedor proveedor, LocalDate fechaEntregaEsperada, Usuario operador) {
        String numero = String.format("OC-%05d", contadorIdOC);
        OrdenDeCompra oc = new OrdenDeCompra(contadorIdOC, numero, LocalDate.now(), fechaEntregaEsperada, operador, proveedor);
        contadorIdOC++;
        ordenesDeCompra.add(oc);
        return oc;
    }

    public OrdenDeCompra buscarOrdenDeCompra(String numeroOC) {
        for (OrdenDeCompra oc : ordenesDeCompra) {
            if (oc.getNumeroOC().equalsIgnoreCase(numeroOC)) {
                return oc;
            }
        }
        return null;
    }

    public Item buscarItemPorCodigo(String codigo) {
        for (Item i : items) {
            if (i.getCodigo().equalsIgnoreCase(codigo)) {
                return i;
            }
        }
        return null;
    }

    public void agregarLineaOC(OrdenDeCompra oc, Item item, double cantidad, double precioAcordado) {
        LineaOrdenCompra linea = new LineaOrdenCompra(oc.getLineas().size() + 1, item, cantidad, precioAcordado);
        oc.agregarLinea(linea);
    }

    // Regla de límite de crédito: el sistema aporta los documentos (dato global)
    // y delega el cálculo y la transición de estado en el Proveedor y la OC
    public EstadoOrdenCompra confirmarOrdenDeCompra(OrdenDeCompra oc) {
        double deudaActual = oc.getProveedor().calcularDeudaActual(documentosComerciales);
        return oc.confirmar(deudaActual);
    }

    public boolean aprobarOrdenDeCompra(OrdenDeCompra oc, Usuario supervisor) {
        return oc.aprobar(supervisor);
    }

    public boolean cancelarOrdenDeCompra(OrdenDeCompra oc) {
        return oc.cancelar();
    }

    // ============================================================
    // MÓDULO: DOCUMENTOS COMERCIALES (Parte 3)
    // ============================================================
    public Factura registrarFactura(Proveedor proveedor, String numeroDoc, LocalDate fecha, double importe,
                                    String cae, LocalDate fechaCae, double baseIVA, double montoIVA) {
        Factura factura = new Factura(contadorIdDocumentos++, numeroDoc, fecha, importe, proveedor,
                cae, fechaCae, baseIVA, montoIVA);
        documentosComerciales.add(factura);
        return factura;
    }

    public NotaDeDebito registrarNotaDeDebito(Proveedor proveedor, String numeroDoc, LocalDate fecha, double importe,
                                              String motivo, Factura facturaOrigen) {
        NotaDeDebito nota = new NotaDeDebito(contadorIdDocumentos++, numeroDoc, fecha, importe, proveedor,
                motivo, facturaOrigen);
        documentosComerciales.add(nota);
        return nota;
    }

    public NotaDeCredito registrarNotaDeCredito(Proveedor proveedor, String numeroDoc, LocalDate fecha, double importe,
                                                String motivo, Factura facturaOrigen) {
        NotaDeCredito nota = new NotaDeCredito(contadorIdDocumentos++, numeroDoc, fecha, importe, proveedor,
                motivo, facturaOrigen);
        documentosComerciales.add(nota);
        return nota;
    }

    public void agregarLineaDocumento(DocumentoComercial doc, Item item, double cantidad,
                                      double precioUnitario, double alicuota) {
        LineaDocumento linea = new LineaDocumento(doc.getLineas().size() + 1, item, cantidad, precioUnitario, alicuota);
        doc.agregarLinea(linea);
    }

    // Validación de amparo con OC + control de precios: el sistema aporta las OC
    // del proveedor (dato global) y delega la regla en el propio documento
    public EstadoRegistroDocumento validarDocumentoConOC(DocumentoComercial doc) {
        return doc.validarContraOC(getOrdenesDeCompra(doc.getProveedor()));
    }

    public boolean aprobarDocumento(DocumentoComercial doc, Usuario supervisor) {
        return doc.aprobar(supervisor);
    }

    public DocumentoComercial buscarDocumentoComercial(String numeroDocumento) {
        for (DocumentoComercial doc : documentosComerciales) {
            if (doc.getNumeroDocumento().equalsIgnoreCase(numeroDocumento)) {
                return doc;
            }
        }
        return null;
    }

    public List<Factura> getFacturas(Proveedor proveedor) {
        List<Factura> resultado = new ArrayList<>();
        for (DocumentoComercial doc : documentosComerciales) {
            if (doc instanceof Factura && doc.getProveedor() == proveedor) {
                resultado.add((Factura) doc);
            }
        }
        return resultado;
    }

    // ============================================================
    // MÓDULO: ÓRDENES DE PAGO (Parte 4)
    // ============================================================
    // Documentos del proveedor que se pueden pagar: facturas y notas de débito
    // con saldo pendiente. Se excluyen las notas de crédito (no se "pagan")
    // y los documentos OBSERVADOS (requieren aprobación de un supervisor antes)
    public List<DocumentoComercial> getDocumentosPendientes(Proveedor proveedor) {
        List<DocumentoComercial> resultado = new ArrayList<>();
        for (DocumentoComercial doc : documentosComerciales) {
            boolean esPagable = !doc.getTipoDocumento().equals("NOTA_DE_CREDITO");
            boolean tieneSaldo = !doc.estaCancelado() && doc.getSaldoPendiente() > 0;
            boolean noObservado = doc.getEstadoRegistro() != EstadoRegistroDocumento.OBSERVADO;
            if (doc.getProveedor() == proveedor && esPagable && tieneSaldo && noObservado) {
                resultado.add(doc);
            }
        }
        return resultado;
    }

    // Delegación: el Proveedor es quien conoce su condición impositiva
    // y sus certificados de exclusión vigentes
    public List<Retencion> calcularRetenciones(Proveedor proveedor, double montoTotal) {
        return proveedor.calcularRetenciones(montoTotal, LocalDate.now());
    }

    public OrdenDePago emitirOrdenDePago(Proveedor proveedor, List<DocumentoPago> documentosPago,
                                         List<MedioDePago> mediosPago, Usuario operador) {
        String numero = String.format("OP-%05d", contadorIdOP);
        OrdenDePago op = new OrdenDePago(contadorIdOP, numero, LocalDate.now(), operador, proveedor);
        contadorIdOP++;

        for (DocumentoPago dp : documentosPago) {
            op.agregarDocumentoPago(dp);
        }
        // Las retenciones se calculan sobre el total bruto de la OP,
        // aplicando primero las exclusiones vigentes del proveedor
        for (Retencion retencion : proveedor.calcularRetenciones(op.calcularTotalBruto(), LocalDate.now())) {
            op.agregarRetencion(retencion);
        }
        for (MedioDePago medio : mediosPago) {
            op.agregarMedioDePago(medio);
        }

        op.emitir(); // aplica los pagos a los documentos y fija los totales
        ordenesDePago.add(op);
        return op;
    }

    public List<OrdenDePago> getOrdenesDePago(Proveedor proveedor) {
        List<OrdenDePago> resultado = new ArrayList<>();
        for (OrdenDePago op : ordenesDePago) {
            if (op.getProveedor() == proveedor) {
                resultado.add(op);
            }
        }
        return resultado;
    }

    // ============================================================
    // MÓDULO: CONSULTAS Y REPORTES (Parte 5)
    // En todos los filtros, null significa "sin filtro"
    // ============================================================
    public List<DocumentoComercial> getDocumentosPorPeriodo(LocalDate desde, LocalDate hasta, Proveedor proveedorFiltro) {
        List<DocumentoComercial> resultado = new ArrayList<>();
        for (DocumentoComercial doc : documentosComerciales) {
            boolean cumpleDesde = (desde == null) || !doc.getFechaEmision().isBefore(desde);
            boolean cumpleHasta = (hasta == null) || !doc.getFechaEmision().isAfter(hasta);
            boolean cumpleProveedor = (proveedorFiltro == null) || doc.getProveedor() == proveedorFiltro;
            if (cumpleDesde && cumpleHasta && cumpleProveedor) {
                resultado.add(doc);
            }
        }
        return resultado;
    }

    public List<DocumentoComercial> getDocumentosPendientes(Proveedor proveedor, int diasMinimosAntiguedad) {
        List<DocumentoComercial> resultado = new ArrayList<>();
        LocalDate fechaLimite = LocalDate.now().minusDays(diasMinimosAntiguedad);
        for (DocumentoComercial doc : getDocumentosPendientes(proveedor)) {
            if (!doc.getFechaEmision().isAfter(fechaLimite)) {
                resultado.add(doc);
            }
        }
        return resultado;
    }

    public List<OrdenDeCompra> buscarOrdenesDeCompra(EstadoOrdenCompra estadoFiltro, Rubro rubroFiltro, Proveedor proveedorFiltro) {
        List<OrdenDeCompra> resultado = new ArrayList<>();
        for (OrdenDeCompra oc : ordenesDeCompra) {
            boolean cumpleEstado = (estadoFiltro == null) || oc.getEstado() == estadoFiltro;
            boolean cumpleRubro = (rubroFiltro == null) || oc.incluyeRubro(rubroFiltro);
            boolean cumpleProveedor = (proveedorFiltro == null) || oc.getProveedor() == proveedorFiltro;
            if (cumpleEstado && cumpleRubro && cumpleProveedor) {
                resultado.add(oc);
            }
        }
        return resultado;
    }

    public List<OrdenDePago> buscarOrdenesDePago(LocalDate desde, LocalDate hasta, String tipoMedioFiltro, Proveedor proveedorFiltro) {
        List<OrdenDePago> resultado = new ArrayList<>();
        for (OrdenDePago op : ordenesDePago) {
            boolean cumpleDesde = (desde == null) || !op.getFechaEmision().isBefore(desde);
            boolean cumpleHasta = (hasta == null) || !op.getFechaEmision().isAfter(hasta);
            boolean cumpleMedio = (tipoMedioFiltro == null) || op.usaMedioDePago(tipoMedioFiltro);
            boolean cumpleProveedor = (proveedorFiltro == null) || op.getProveedor() == proveedorFiltro;
            if (cumpleDesde && cumpleHasta && cumpleMedio && cumpleProveedor) {
                resultado.add(op);
            }
        }
        return resultado;
    }

    public List<Proveedor> getProveedoresQueSuministran(Item item) {
        List<Proveedor> resultado = new ArrayList<>();
        for (Proveedor p : proveedores) {
            if (p.getPrecioAcordadoPara(item) != null) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    // Retenciones de todas las OP emitidas dentro del período
    public List<Retencion> getRetencionesPorPeriodo(LocalDate desde, LocalDate hasta) {
        List<Retencion> resultado = new ArrayList<>();
        for (OrdenDePago op : buscarOrdenesDePago(desde, hasta, null, null)) {
            resultado.addAll(op.getRetenciones());
        }
        return resultado;
    }

    // ============================================================
    // MÓDULO: MÉTODOS AUXILIARES COMUNES (Parte 1)
    // Getters que necesitan los controllers de OC, Documentos y OP
    // ============================================================
    public List<DocumentoComercial> getDocumentosComerciales() {
        return documentosComerciales;
    }

    public List<DocumentoComercial> getDocumentosComerciales(Proveedor proveedor) {
        List<DocumentoComercial> resultado = new ArrayList<>();
        for (DocumentoComercial doc : documentosComerciales) {
            if (doc.getProveedor() == proveedor) {
                resultado.add(doc);
            }
        }
        return resultado;
    }

    public List<OrdenDeCompra> getOrdenesDeCompra() {
        return ordenesDeCompra;
    }

    public List<OrdenDeCompra> getOrdenesDeCompra(Proveedor proveedor) {
        List<OrdenDeCompra> resultado = new ArrayList<>();
        for (OrdenDeCompra oc : ordenesDeCompra) {
            if (oc.getProveedor() == proveedor) {
                resultado.add(oc);
            }
        }
        return resultado;
    }

    public List<OrdenDePago> getOrdenesDePago() {
        return ordenesDePago;
    }

    // Atajo para los controllers: delega el cálculo en el Proveedor (dueño de la regla)
    public double calcularDeudaProveedor(Proveedor proveedor) {
        return proveedor.calcularDeudaActual(documentosComerciales);
    }
}
