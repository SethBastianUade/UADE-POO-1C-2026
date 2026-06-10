package mvc.controller;

import mvc.dto.LibroIVACompraDTO;
import mvc.dto.TotalRetenidoDTO;
import mvc.enums.TipoImpuesto;
import mvc.model.DocumentoComercial;
import mvc.model.Factura;
import mvc.model.Retencion;
import mvc.view.ReportesFiscalesGUI;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ReportesFiscalesController {
    private ReportesFiscalesGUI vista;

    public ReportesFiscalesController(ReportesFiscalesGUI vista) {
        this.vista = vista;

        this.vista.getBtnGenerar().addActionListener(e -> generar());

        generar(); // carga inicial sin filtros
    }

    private void generar() {
        LocalDate desde, hasta;
        try {
            desde = vista.getDesde().isEmpty() ? null : LocalDate.parse(vista.getDesde());
            hasta = vista.getHasta().isEmpty() ? null : LocalDate.parse(vista.getHasta());
        } catch (DateTimeParseException ex) {
            vista.mostrarMensaje("Fechas inválidas (use AAAA-MM-DD o deje vacío).", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }
        generarTotalesRetenciones(desde, hasta);
        generarLibroIVA(desde, hasta);
    }

    private void generarTotalesRetenciones(LocalDate desde, LocalDate hasta) {
        List<Retencion> retenciones = OrdenDePagoController.getRetencionesPorPeriodo(desde, hasta);

        List<TotalRetenidoDTO> dtos = new ArrayList<>();
        double totalGeneral = 0;
        // Una fila por impuesto, aunque no haya retenciones del tipo
        for (TipoImpuesto tipo : TipoImpuesto.values()) {
            int cantidad = 0;
            double total = 0;
            for (Retencion r : retenciones) {
                if (r.getTipoImpuesto() == tipo) {
                    cantidad++;
                    total += r.getMontoRetenido();
                }
            }
            dtos.add(new TotalRetenidoDTO(tipo.toString(), cantidad, total));
            totalGeneral += total;
        }
        vista.actualizarTablaRetenciones(dtos);
        vista.setTotalRetenciones(String.format("Total retenido en el período: $%.2f", totalGeneral));
    }

    private void generarLibroIVA(LocalDate desde, LocalDate hasta) {
        List<LibroIVACompraDTO> dtos = new ArrayList<>();
        double totalBase = 0, totalIVA = 0, totalGeneral = 0;

        for (DocumentoComercial doc : DocumentoComercialController.getDocumentosPorPeriodo(desde, hasta, null)) {
            double base, iva, total;
            if (doc instanceof Factura) {
                Factura factura = (Factura) doc;
                base = factura.getBaseImponibleIVA();
                iva = factura.getMontoIVA();
                total = factura.getImporteTotal();
            } else if (doc.getTipoDocumento().equals("NOTA_DE_CREDITO")) {
                // Las NC restan en el libro; el modelo no discrimina su IVA
                base = -doc.getImporteTotal();
                iva = 0;
                total = -doc.getImporteTotal();
            } else {
                base = doc.getImporteTotal();
                iva = 0;
                total = doc.getImporteTotal();
            }
            dtos.add(new LibroIVACompraDTO(
                    doc.getFechaEmision().toString(), doc.getProveedor().getCuit(),
                    doc.getProveedor().getRazonSocial(), doc.getTipoDocumento(),
                    doc.getNumeroDocumento(), base, iva, total
            ));
            totalBase += base;
            totalIVA += iva;
            totalGeneral += total;
        }
        vista.actualizarTablaLibroIVA(dtos);
        vista.setTotalesLibro(String.format("Totales del período — Base: $%.2f | IVA: $%.2f | Total: $%.2f",
                totalBase, totalIVA, totalGeneral));
    }
}
