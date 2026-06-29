# Resumen del Proyecto — Sistema de Gestión Integral de Compras

**Materia:** Programación Orientada a Objetos — UADE, 1C 2026
**Caso:** Gestión de Compras, Contrataciones y Pagos del *Sanatorio Privado Integral del Sur*

---

## ¿Qué hace?

Sistema de escritorio (Java + Swing) que administra el ciclo completo de compras de un
sanatorio: desde el alta de proveedores e ítems, pasando por las órdenes de compra y la
recepción de mercadería, hasta la facturación, el pago con cálculo de retenciones
impositivas y los reportes fiscales.

## Módulos

1. **Catálogos** — Rubros e Ítems (Productos / Servicios).
2. **Proveedores** — alta con validación de CUIT, rubros asociados, certificados de
   exclusión impositiva y precios acordados.
3. **Compras** — Órdenes de Compra (con control de límite de crédito) y Recepción de
   mercadería (actualiza stock y estado de la OC).
4. **Facturación y Pagos** — Documentos comerciales (Factura / Nota de Débito / Nota de
   Crédito) y Órdenes de Pago con retenciones de IVA, Ganancias e IIBB.
5. **Consultas y Reportes** — trazabilidad documental, cuenta corriente, seguimiento de
   compras y pagos, comparación de precios y reportes fiscales (Libro IVA).

## Arquitectura (en una línea)

**MVC estricto sin fachada central:** `Vista (Swing) → Controller del módulo → Modelo`,
y de vuelta mediante **DTOs**. Cada controller es dueño de los datos de su módulo (listas
`static` que simulan la base de datos) y las reglas de negocio viven en la clase del
modelo que posee el dato.

## Conceptos OO aplicados (resumen)

| Concepto | Dónde |
|---|---|
| **Herencia** | `Item → Producto/Servicio`; `DocumentoComercial → Factura/NotaDébito/NotaCrédito`; `MedioDePago → Efectivo/Transferencia/Cheques` |
| **Polimorfismo** | `getImpactoDeuda()` (la Nota de Crédito lo sobrescribe en negativo); `getBaseImponible()`/`getMontoIVA()` (la Factura usa el IVA sellado por AFIP); `getTipoDocumento()` / `getTipoItem()` |
| **Encapsulamiento** | Todos los atributos `private`; la lógica vive en la clase dueña del dato (`Proveedor.calcularRetenciones`, `DocumentoComercial.validarContraOC`) |
| **Clases abstractas** | `Item`, `DocumentoComercial`, `MedioDePago` |
| **Clases asociativas** | `ProveedorItem` (N:M Proveedor↔Item con precio acordado); `DocumentoPago` (OP↔Documento con monto aplicado) |
| **Singleton** | **No se usa** (se evaluó y se reemplazó por estado `static` por módulo — ver doc detallada) |

## Cómo correrlo

Abrir el proyecto en IntelliJ y ejecutar `Menu.java` (clase `mvc.Menu$VentanaPrincipal`).
Usuarios de prueba: `admin / 1234` (SUPERVISOR) · `operador / 1234` (OPERADOR).

> Documentación completa con ejemplos de código en **`DOCUMENTACION.md`**.
