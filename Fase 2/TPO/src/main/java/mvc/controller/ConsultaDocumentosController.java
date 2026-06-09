package mvc.controller;

import mvc.dto.DocumentoComercialDTO;
import mvc.dto.ProveedorDTO;
import mvc.dto.TotalDiarioDTO;
import mvc.model.DocumentoComercial;
import mvc.model.Proveedor;
import mvc.model.SistemaCompras;
import mvc.view.ConsultaDocumentosGUI;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsultaDocumentosController {
    private ConsultaDocumentosGUI vista;
    private SistemaCompras sistema;

    public ConsultaDocumentosController(ConsultaDocumentosGUI vista) {
        this.vista = vista;
        this.sistema = SistemaCompras.getInstance();

        this.vista.getBtnBuscar().addActionListener(e -> buscar());

        cargarComboProveedores();
        buscar(); // carga inicial sin filtros
    }

    private void buscar() {
        LocalDate desde, hasta;
        try {
            desde = vista.getDesde().isEmpty() ? null : LocalDate.parse(vista.getDesde());
            hasta = vista.getHasta().isEmpty() ? null : LocalDate.parse(vista.getHasta());
        } catch (DateTimeParseException ex) {
            vista.mostrarMensaje("Fechas inválidas (use AAAA-MM-DD o deje vacío).", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String cuit = vista.getCuitProveedorFiltro();
        Proveedor proveedor = (cuit != null) ? sistema.buscarProveedorPorCuit(cuit) : null;

        List<DocumentoComercial> documentos = sistema.getDocumentosPorPeriodo(desde, hasta, proveedor);

        // Detalle
        List<DocumentoComercialDTO> dtos = new ArrayList<>();
        double importeTotal = 0;
        for (DocumentoComercial doc : documentos) {
            dtos.add(new DocumentoComercialDTO(
                    doc.getNumeroDocumento(), doc.getTipoDocumento(), doc.getProveedor().getRazonSocial(),
                    doc.getFechaEmision().toString(), doc.getImporteTotal(), doc.getSaldoPendiente(),
                    doc.getEstadoCancelacion().toString(), doc.getEstadoRegistro().toString()
            ));
            importeTotal += doc.getImporteTotal();
        }
        vista.actualizarTabla(dtos);

        // Totales por día (TreeMap para que las fechas queden ordenadas)
        Map<LocalDate, List<DocumentoComercial>> porDia = new TreeMap<>();
        for (DocumentoComercial doc : documentos) {
            porDia.computeIfAbsent(doc.getFechaEmision(), k -> new ArrayList<>()).add(doc);
        }
        List<TotalDiarioDTO> totalesDia = new ArrayList<>();
        for (Map.Entry<LocalDate, List<DocumentoComercial>> entrada : porDia.entrySet()) {
            double subtotal = 0;
            for (DocumentoComercial doc : entrada.getValue()) {
                subtotal += doc.getImporteTotal();
            }
            totalesDia.add(new TotalDiarioDTO(entrada.getKey().toString(), entrada.getValue().size(), subtotal));
        }
        vista.actualizarTablaTotalesDia(totalesDia);

        vista.setResumen(String.format("Documentos en el período: %d — Importe total: $%.2f", documentos.size(), importeTotal));
    }

    private void cargarComboProveedores() {
        List<ProveedorDTO> proveedores = new ArrayList<>();
        for (Proveedor p : sistema.getProveedores()) {
            proveedores.add(new ProveedorDTO(p.getCuit(), p.getRazonSocial(),
                    p.getCondicionImpositiva().toString(), p.getLimiteDeudaAutorizado(), p.isActivo()));
        }
        vista.cargarComboProveedores(proveedores);
    }
}
