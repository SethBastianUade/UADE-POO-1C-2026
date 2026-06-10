package mvc.controller;

import mvc.enums.CondicionImpositiva;
import mvc.enums.TipoImpuesto;
import mvc.model.DocumentoPago;
import mvc.model.Factura;
import mvc.model.MedioDePago;
import mvc.model.OrdenDeCompra;
import mvc.model.Proveedor;
import mvc.model.Rubro;
import mvc.model.TransferenciaBancaria;
import mvc.model.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// ============================================================
// DATOS DE PRUEBA (Parte 5)
// Se cargan usando los métodos de negocio reales de cada controller
// para que todos los estados queden consistentes (OC emitidas,
// facturas validadas, OP con retenciones aplicadas, etc.)
// Se invoca desde el main de Menu.VentanaPrincipal.
// ============================================================
public final class DatosDePrueba {
    private static boolean yaCargados = false;

    private DatosDePrueba() {
        // Clase utilitaria: no se instancia
    }

    public static void cargar() {
        if (yaCargados) {
            return; // garantiza una única carga aunque se invoque dos veces
        }
        yaCargados = true;

        // Rubros e ítems
        RubroController.agregarRubro("INS", "Insumos Médicos");
        RubroController.agregarRubro("SRV", "Servicios Generales");
        Rubro rubroInsumos = RubroController.buscarRubro("INS");
        Rubro rubroServicios = RubroController.buscarRubro("SRV");
        ItemController.agregarProducto("P001", "Guantes de látex x100", "caja", 5000, 21, rubroInsumos,
                "L-001", LocalDate.now().plusMonths(18), 200, 50);
        ItemController.agregarProducto("P002", "Barbijos N95 x20", "caja", 8000, 21, rubroInsumos,
                "L-002", LocalDate.now().plusMonths(24), 150, 30);
        ItemController.agregarServicio("S001", "Mantenimiento de autoclaves", "hora", 15000, 21, rubroServicios,
                "Presencial", 4, "Técnico matriculado");

        // Proveedores: uno RI con exclusión de IVA vigente, otro monotributista
        ProveedorController.agregarProveedor("30-11111111-1", "Insumos del Sur S.A.", "Insumos del Sur", "Av. Mitre 1234",
                "11-4444-1111", "ventas@insumosdelsur.com", CondicionImpositiva.RESPONSABLE_INSCRIPTO,
                "901-111", LocalDate.of(2015, 3, 1), 500000);
        ProveedorController.agregarProveedor("20-22222222-2", "Servicios Médicos SRL", "SerMed", "Calle Falsa 742",
                "11-5555-2222", "info@sermed.com", CondicionImpositiva.MONOTRIBUTISTA,
                "902-222", LocalDate.of(2019, 8, 15), 200000);
        Proveedor p1 = ProveedorController.buscarProveedorPorCuit("30-11111111-1");
        Proveedor p2 = ProveedorController.buscarProveedorPorCuit("20-22222222-2");
        ProveedorController.asignarRubroAProveedor(p1.getCuit(), "INS");
        ProveedorController.asignarRubroAProveedor(p2.getCuit(), "SRV");
        ProveedorController.registrarPrecioAcordado(p1.getCuit(), "P001", 4800);
        ProveedorController.registrarPrecioAcordado(p1.getCuit(), "P002", 7900);
        ProveedorController.registrarPrecioAcordado(p2.getCuit(), "P001", 4950); // mismo ítem, otro precio (comparación)
        ProveedorController.agregarCertificadoAProveedor(p1.getCuit(), "EXCL-IVA-001", TipoImpuesto.IVA,
                LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(5));

        Usuario admin = LoginController.buscarUsuario("admin");

        // OC emitida de p1 (total 271000 <= límite 500000)
        OrdenDeCompra oc1 = OrdenDeCompraController.crearOrdenDeCompra(p1, LocalDate.now().plusDays(7), admin);
        OrdenDeCompraController.agregarLineaOC(oc1, ItemController.buscarItemPorCodigo("P001"), 40, 4800);
        OrdenDeCompraController.agregarLineaOC(oc1, ItemController.buscarItemPorCodigo("P002"), 10, 7900);
        OrdenDeCompraController.confirmarOrdenDeCompra(oc1); // EMITIDA

        // Factura de p1 amparada por la OC (queda APROBADA)
        Factura f1 = DocumentoComercialController.registrarFactura(p1, "FC-0001-00001234",
                LocalDate.now().minusDays(5), 327910,
                "CAE12345678901", LocalDate.now().plusDays(10), 271000, 56910);
        DocumentoComercialController.agregarLineaDocumento(f1, ItemController.buscarItemPorCodigo("P001"), 40, 4800, 21);
        DocumentoComercialController.agregarLineaDocumento(f1, ItemController.buscarItemPorCodigo("P002"), 10, 7900, 21);
        DocumentoComercialController.validarDocumentoConOC(f1);

        // Factura de p2 sin OC que la ampare (queda OBSERVADA)
        Factura f2 = DocumentoComercialController.registrarFactura(p2, "FC-0002-00000077",
                LocalDate.now().minusDays(3), 18150,
                "CAE98765432109", LocalDate.now().plusDays(10), 15000, 3150);
        DocumentoComercialController.agregarLineaDocumento(f2, ItemController.buscarItemPorCodigo("S001"), 1, 15000, 21);
        DocumentoComercialController.validarDocumentoConOC(f2);

        // NC de p1 sobre la factura (resta deuda)
        DocumentoComercialController.registrarNotaDeCredito(p1, "NC-0001-00000005", LocalDate.now().minusDays(2),
                5875, "Descuento por pronto pago", f1);

        // OC de p2 que supera su límite de crédito (queda PENDIENTE_APROBACION)
        OrdenDeCompra oc2 = OrdenDeCompraController.crearOrdenDeCompra(p2, LocalDate.now().plusDays(5), admin);
        OrdenDeCompraController.agregarLineaOC(oc2, ItemController.buscarItemPorCodigo("S001"), 20, 15000);
        OrdenDeCompraController.confirmarOrdenDeCompra(oc2);

        // OP parcial de la factura de p1: bruto 200000.
        // p1 es RI con exclusión de IVA vigente: retiene Ganancias 1500 + IIBB 3750
        // -> neto 194750, pagado por transferencia. f1 queda PARCIALMENTE_CANCELADO.
        List<DocumentoPago> documentosPago = new ArrayList<>();
        documentosPago.add(new DocumentoPago(f1, 200000));
        List<MedioDePago> medios = new ArrayList<>();
        medios.add(new TransferenciaBancaria(1, 194750, "TRF-000123", "CTA-SANATORIO-01"));
        OrdenDePagoController.emitirOrdenDePago(p1, documentosPago, medios, admin);
    }
}
