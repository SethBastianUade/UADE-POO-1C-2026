# CLAUDE.md — Sistema de Gestión Integral de Compras
## TPO POO - UADE 1C 2026

> Este archivo es el punto de entrada para Claude Code.
> Contiene todo el contexto del proyecto para trabajar sin fricción.

---

## 1. Descripción General

TPO (Trabajo Práctico Obligatorio) de la materia **Programación Orientada a Objetos** — UADE, 1er cuatrimestre 2026.

**Sistema:** Gestión Integral de Compras, Contrataciones y Pagos para el "Sanatorio Privado Integral del Sur".

**Fases:**
- Fase I ✅ — Modelado OO y UML (diagramas de clases + secuencia, en `Fase I/`)
- Fase II ✅ — Implementación funcional en Java (en `Fase 2/TPO/`). Las 5 partes
  (modelo base, OC, Documentos, OP, Consultas) están implementadas y el proyecto
  compila sin errores. Ver §4 para el detalle y §11 para lo que falta pulir/testear.

---

## 2. Estructura del Proyecto

```
Fase 2/TPO/src/main/java/mvc/
├── Menu.java                        ← Punto de entrada / ventana principal (JDesktopPane)
├── model/                           ← Capa de dominio
│   ├── SistemaCompras.java          ← Singleton, fachada de negocio
│   ├── Item.java                    ← Abstracta
│   ├── Producto.java
│   ├── Servicio.java
│   ├── Proveedor.java
│   ├── Rubro.java
│   ├── ProveedorItem.java           ← Clase asociativa N:M Proveedor↔Item
│   ├── CertificadoExclusion.java
│   ├── DocumentoComercial.java      ← Abstracta
│   ├── Factura.java
│   ├── NotaDeDebito.java
│   ├── NotaDeCredito.java
│   ├── LineaDocumento.java
│   ├── OrdenDeCompra.java
│   ├── LineaOrdenCompra.java
│   ├── OrdenDePago.java
│   ├── DocumentoPago.java           ← Clase asociativa OP↔DocumentoComercial
│   ├── MedioDePago.java             ← Abstracta
│   ├── Efectivo.java
│   ├── TransferenciaBancaria.java
│   ├── ChequePropio.java
│   ├── ChequeDeTerceros.java
│   ├── Retencion.java
│   └── Usuario.java
├── enums/
│   ├── EstadoOrdenCompra.java       ← BORRADOR, PENDIENTE_APROBACION, EMITIDA, RECIBIDA_PARCIALMENTE, CERRADA, CANCELADA
│   ├── EstadoCancelacionDocumento.java ← PENDIENTE, PARCIALMENTE_CANCELADO, CANCELADO
│   ├── EstadoRegistroDocumento.java ← INGRESADO, OBSERVADO, APROBADO
│   ├── CondicionImpositiva.java     ← RESPONSABLE_INSCRIPTO, MONOTRIBUTISTA, EXENTO, NO_CATEGORIZADO
│   ├── RolUsuario.java              ← OPERADOR, SUPERVISOR
│   └── TipoImpuesto.java            ← IVA, GANANCIAS, IIBB
├── controller/
│   ├── LoginController.java
│   ├── RubroController.java
│   ├── ItemController.java
│   ├── ProveedorController.java
│   ├── CertificadoController.java
│   ├── PreciosAcordadosController.java
│   ├── OrdenDeCompraController.java          ← Parte 2
│   ├── DocumentoComercialController.java     ← Parte 3
│   ├── OrdenDePagoController.java            ← Parte 4
│   ├── ConsultaDocumentosController.java     ← Parte 5
│   ├── CuentaCorrienteController.java        ← Parte 5
│   ├── SeguimientoComprasController.java     ← Parte 5
│   ├── ComparacionPreciosController.java     ← Parte 5
│   └── ReportesFiscalesController.java        ← Parte 5
├── view/                            ← GUIs Swing (JInternalFrame)
│   ├── LoginGUI.java
│   ├── RubroGUI.java
│   ├── ItemGUI.java
│   ├── ProveedorGUI.java
│   ├── AsociarProveedorRubroGUI.java
│   ├── CertificadosProveedorGUI.java
│   ├── PreciosAcordadosGUI.java
│   ├── OrdenDeCompraGUI.java                 ← Parte 2
│   ├── DocumentoComercialGUI.java            ← Parte 3
│   ├── OrdenDePagoGUI.java                   ← Parte 4
│   ├── ConsultaDocumentosGUI.java            ← Parte 5
│   ├── CuentaCorrienteGUI.java               ← Parte 5
│   ├── SeguimientoComprasGUI.java            ← Parte 5
│   ├── ComparacionPreciosGUI.java            ← Parte 5
│   └── ReportesFiscalesGUI.java              ← Parte 5
└── dto/
    ├── RubroDTO.java
    ├── ItemDTO.java
    ├── ProveedorDTO.java
    ├── OrdenDeCompraDTO.java / LineaOrdenCompraDTO.java          ← Parte 2
    ├── DocumentoComercialDTO.java / LineaDocumentoDTO.java       ← Parte 3
    ├── OrdenDePagoDTO.java / RetencionDTO.java / MedioDePagoDTO.java  ← Parte 4
    ├── TotalDiarioDTO.java / MovimientoCuentaCorrienteDTO.java   ← Parte 5
    ├── ComparacionPrecioDTO.java                                 ← Parte 5
    └── TotalRetenidoDTO.java / LibroIVACompraDTO.java            ← Parte 5
```

---

## 3. Arquitectura: Patrón MVC + Singleton

El proyecto usa **MVC estricto** + **Singleton** para el modelo:

```
View (Swing GUI)  →  Controller  →  Model (SistemaCompras.getInstance())
                  ←  DTO         ←
```

- **Vista** nunca accede directamente al modelo. Recibe DTOs.
- **Controller** hace de intermediario: lee datos de la vista, llama a `SistemaCompras`, convierte a DTOs para la vista.
- **SistemaCompras** es el Singleton que actúa como fachada: tiene las listas en memoria y los métodos de negocio.
- **DTOs** son objetos planos (sin lógica) para transferir datos entre capa controller y vista.

---

## 4. Estado Actual de Implementación

### ✅ Implementado y funcionando (TPO completo, compila sin errores):

**Base (Parte 1):**
- Login con autenticación (usuarios hardcodeados en `SistemaCompras`)
- CRUD de Rubros, Items (Producto/Servicio con CardLayout), Proveedores
- Asociar/desasociar Rubros a un Proveedor
- Certificados de Exclusión por Proveedor
- Precios Acordados por Proveedor (relación ProveedorItem)
- `Proveedor.calcularDeudaActual(documentos)` → suma impactos de docs PENDIENTE/PARCIALMENTE_CANCELADO
- `Usuario.tienePermiso(accion)` → SUPERVISOR todo; OPERADOR todo salvo `APROBAR_OC` / `APROBAR_DOCUMENTO`
- Ventana principal con JDesktopPane y menú por módulos

**Órdenes de Compra (Parte 2):**
- GUI + Controller + alta en BORRADOR, carga de líneas
- Confirmación con control de límite de crédito → EMITIDA o PENDIENTE_APROBACION
- Aprobación por SUPERVISOR, cancelación

**Documentos Comerciales (Parte 3):**
- GUI (CardLayout Factura / ND / NC) + Controller
- Validación de amparo con OC + control de precios → APROBADO / OBSERVADO
- Aprobación de documentos OBSERVADOS por SUPERVISOR

**Órdenes de Pago (Parte 4):**
- GUI + Controller + selección de documentos a pagar y medios de pago
- Cálculo de retenciones (IVA/Ganancias/IIBB) respetando exclusiones y condición impositiva
- Emisión: aplica pagos a los documentos (actualiza estadoCancelacion)

**Consultas y Reportes (Parte 5):**
- Trazabilidad documental, Cuenta corriente, Seguimiento de compras/pagos,
  Comparación de precios, Reportes fiscales (retenciones + Libro IVA)
- Datos de prueba precargados (`SistemaCompras.cargarDatosDePrueba()`)

### 🐛 Bugs corregidos:
- `CertificadoController.java`: `CertificadosController(...)` (método con typo) ahora es el
  constructor real `CertificadoController(vista)` y `CertificadosProveedorGUI` lo instancia con `this`.

### 📐 Decisiones de diseño a confirmar con la cátedra:
- **Firma de `calcularDeudaActual(List<DocumentoComercial>)`**: recibe la lista por parámetro
  (el Proveedor no posee la lista global). Difiere del UML de Fase I (sin parámetros) → conviene
  actualizar el diagrama de clases.
- **NC resta deuda** vía polimorfismo: `DocumentoComercial.getImpactoDeuda()` (+saldo) sobrescrito
  en `NotaDeCredito` (−saldo). Afecta también el límite de crédito de las OC.
- **`NO_CATEGORIZADO`**: se le aplican las alícuotas de RESPONSABLE_INSCRIPTO (criterio conservador).
- **Medios de pago de la OP** deben cubrir el neto **exactamente** (±$0,01).
- **`getDocumentosPendientes`** excluye Notas de Crédito y documentos OBSERVADOS.

---

## 5. Modelo de Dominio — Clases Clave

### SistemaCompras (Singleton)
```java
// Listas en memoria (base de datos simulada)
List<OrdenDeCompra> ordenesDeCompra
List<Proveedor> proveedores
List<DocumentoComercial> documentosComerciales
List<OrdenDePago> ordenesDePago
List<Usuario> usuarios
List<Item> items
List<Rubro> rubros
Usuario usuarioLogueado

// Métodos base (Parte 1):
autenticarUsuario(user, pass) → Usuario
agregarRubro / buscarRubro / modificarRubro / cambiarEstadoRubro
agregarProducto / agregarServicio / getItems / buscarItemPorCodigo
agregarProveedor / buscarProveedorPorCuit / modificarProveedor / cambiarEstadoProveedor
asignarRubroAProveedor / desvincularRubroDeProveedor
agregarCertificadoAProveedor / registrarPrecioAcordado
calcularDeudaProveedor(p)   // delega en Proveedor.calcularDeudaActual(docs)

// Órdenes de Compra (Parte 2):
crearOrdenDeCompra(prov, fechaEntrega, operador) / agregarLineaOC / buscarOrdenDeCompra
confirmarOrdenDeCompra(oc) → EstadoOrdenCompra   // control de límite de crédito
aprobarOrdenDeCompra(oc, supervisor) / cancelarOrdenDeCompra(oc)
getOrdenesDeCompra() / getOrdenesDeCompra(p)

// Documentos Comerciales (Parte 3):
registrarFactura / registrarNotaDeDebito / registrarNotaDeCredito
agregarLineaDocumento / buscarDocumentoComercial / getFacturas(p)
validarDocumentoConOC(doc) → EstadoRegistroDocumento   // amparo OC + control de precios
aprobarDocumento(doc, supervisor)
getDocumentosComerciales() / getDocumentosComerciales(p)

// Órdenes de Pago (Parte 4):
getDocumentosPendientes(p) / calcularRetenciones(p, monto) → List<Retencion>
emitirOrdenDePago(prov, documentosPago, mediosPago, operador) → OrdenDePago
getOrdenesDePago() / getOrdenesDePago(p)

// Consultas (Parte 5):
getDocumentosPorPeriodo(desde, hasta, prov) / getDocumentosPendientes(p, diasAntiguedad)
buscarOrdenesDeCompra(estado, rubro, prov) / buscarOrdenesDePago(desde, hasta, tipoMedio, prov)
getProveedoresQueSuministran(item) / getRetencionesPorPeriodo(desde, hasta)

// Datos de prueba: cargarDatosDePrueba() (llamado desde el constructor)
```

### Herencia principal
```
Item (abstract)
  ├── Producto   [lote, fechaVencimiento, stockActual, stockMinimo]
  └── Servicio   [modalidadPrestacion, duracionEstimadaHoras, requisitosTecnicos]

DocumentoComercial (abstract)
  ├── Factura    [cae, fechaVencimientoCAE, baseImponibleIVA, montoIVA]
  ├── NotaDeDebito   [motivoDebito, facturaOrigen]
  └── NotaDeCredito  [motivoCredito, facturaOrigen]

MedioDePago (abstract)
  ├── Efectivo
  ├── TransferenciaBancaria  [nroReferencia, cuentaOrigen]
  ├── ChequePropio           [nroCheque, banco, fechaEmision, fechaVencimiento, firmante]
  └── ChequeDeTerceros       [nroCheque, banco, fechaEmision, fechaVencimiento, firmanteOriginal]
```

### Clases asociativas
- `ProveedorItem(item, precioAcordado, fechaAcuerdo)` — relación N:M Proveedor↔Item
- `DocumentoPago(documentoComercial, montoAplicado)` — relación OP↔DocumentoComercial

---

## 6. Reglas de Negocio Críticas (aún por implementar)

### Confirmación de OC — Control de Límite de Crédito
```
deudaActual = suma de documentos PENDIENTE o PARCIALMENTE_CANCELADO del proveedor
montoComprometido = deudaActual + totalNuevaOC
si montoComprometido ≤ proveedor.limiteDeudaAutorizado → estado = EMITIDA
sino → estado = PENDIENTE_APROBACION (requiere rol SUPERVISOR)
```

### Registro de Factura
```
1. Verificar que exista OC que ampare los ítems (o autorización SUPERVISOR)
2. Control de precios: precioFactura vs precioAcordadoOC
   - iguales → APROBADO
   - diferentes → OBSERVADO (requiere SUPERVISOR)
3. Verificar coherencia ítems con rubros del proveedor
```

### Generación de OP — Retenciones
```
Para cada tipo de impuesto (IVA, GANANCIAS, IIBB):
  si proveedor.tieneExclusionActiva(tipo, fechaHoy) → NO retener
  sino → calcular retención según CondicionImpositiva y porcentaje

Retencion.calcularMonto() = max(0, baseImponible - MNI) * (porcentaje / 100)
totalBruto = suma de DocumentoPago.montoAplicado
totalRetenido = suma de Retencion.montoRetenido
totalNeto = totalBruto - totalRetenido

Al emitir OP → aplicar DocumentoComercial.aplicarPago(monto) en cada doc asociado
  → actualiza estadoCancelacion automáticamente
```

---

## 7. Estándares de Codificación

### 7.1 Estructura general
- **Lenguaje:** Java (sin frameworks externos, sin Maven/Gradle)
- **GUI:** Swing — `JInternalFrame` dentro del `JDesktopPane` de `Menu.java`
- **Package raíz:** `mvc`
- **Subpackages:** `model` · `enums` · `controller` · `view` · `dto`

### 7.2 Nombrado

| Elemento | Convención | Ejemplo |
|---|---|---|
| Clases | PascalCase | `OrdenDeCompra`, `RubroController` |
| Interfaces | PascalCase + I (si aplica) | `IReportable` |
| Atributos | camelCase | `limiteDeudaAutorizado` |
| Métodos | camelCase, verbo primero | `calcularDeudaActual()`, `buscarRubro()` |
| Enums | UPPER_SNAKE_CASE | `PENDIENTE_APROBACION` |
| Constantes | UPPER_SNAKE_CASE | `PORCENTAJE_IVA` |
| Campos GUI | prefijo descriptivo + nombre | `txtCodigo`, `btnGuardar`, `cbCondicion`, `rbProducto` |
| Modelos de tabla | `modeloTabla` (siempre este nombre dentro de la GUI) | `modeloTabla` |

**Prefijos de campos Swing:**
- `txt` → `JTextField`
- `btn` → `JButton`
- `cb` → `JComboBox`
- `rb` → `JRadioButton`
- `chk` → `JCheckBox`
- `lbl` → `JLabel`
- `pnl` → `JPanel` (solo si se necesita referencia desde fuera del constructor)

### 7.3 Estructura interna de cada clase

**Orden de secciones (igual en todas las clases):**
```
1. Atributos privados
2. Constructor/es
3. Métodos de negocio (si es modelo) o métodos de acción (si es controller)
4. Getters y Setters
```

**Modelo de dominio:**
```java
public class Ejemplo {
    // 1. Atributos — todos privados, sin excepción
    private int id;
    private String nombre;

    // 2. Constructor completo
    public Ejemplo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // 3. Métodos de negocio — la lógica va acá, no en el controller
    public boolean estaActivo() { ... }

    // 4. Getters y Setters — uno por línea para getters simples
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
```

**DTO:**
```java
public class EjemploDTO {
    // Solo atributos + constructor + getters (sin setters si es de solo lectura)
    private String campo;

    public EjemploDTO(String campo) { this.campo = campo; }
    public String getCampo() { return campo; }
}
```

**GUI (JInternalFrame):**
```java
public class EjemploGUI extends JInternalFrame {
    // 1. Campos Swing (privados)
    private JTextField txtNombre;
    private JButton btnGuardar;
    private JTable tablaEjemplos;
    private DefaultTableModel modeloTabla;

    // 2. Constructor: configura ventana, llama inicializar*, instancia controller
    public EjemploGUI() {
        super("Título", true, true, true, true);
        setSize(800, 400);
        setLayout(new BorderLayout());
        inicializarFormulario();
        inicializarTabla();
        new EjemploController(this);  // el controller se conecta acá
    }

    // 3. Métodos de inicialización (privados)
    private void inicializarFormulario() { ... }
    private void inicializarTabla() { ... }

    // 4. Métodos públicos que usa el controller (get/set de campos, actualizar tabla)
    public String getNombreIngresado() { return txtNombre.getText().trim(); }
    public void limpiarFormulario() { txtNombre.setText(""); }
    public void actualizarTabla(List<EjemploDTO> lista) { ... }
    public void mostrarMensaje(String msg, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, msg, titulo, tipo);
    }
    // Getters de componentes que el controller necesita para suscribir eventos
    public JButton getBtnGuardar() { return btnGuardar; }
    public JTable getTablaEjemplos() { return tablaEjemplos; }
}
```

**Controller:**
```java
public class EjemploController {
    private EjemploGUI vista;
    private SistemaCompras sistema;

    // Constructor: obtiene instancia del sistema y suscribe todos los eventos
    public EjemploController(EjemploGUI vista) {
        this.vista = vista;
        this.sistema = SistemaCompras.getInstance();
        this.vista.getBtnGuardar().addActionListener(e -> guardar());
        // ... resto de suscripciones
        cargarTabla();  // poblar la tabla al abrir
    }

    // Métodos privados: uno por acción de negocio
    private void guardar() { ... }
    private void cargarTabla() { ... }
}
```

### 7.4 Tabla en GUIs — patrón estándar

Toda tabla sigue este patrón exacto:

```java
// En inicializarTabla():
String[] columnas = {"Col1", "Col2", "Estado"};
modeloTabla = new DefaultTableModel(columnas, 0) {
    @Override
    public boolean isCellEditable(int row, int column) { return false; }
};
tablaX = new JTable(modeloTabla);
tablaX.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
add(new JScrollPane(tablaX), BorderLayout.CENTER);

// En actualizarTabla(List<XDto> lista):
public void actualizarTabla(List<EjemploDTO> lista) {
    modeloTabla.setRowCount(0);  // limpiar siempre antes de cargar
    for (EjemploDTO dto : lista) {
        modeloTabla.addRow(new Object[]{ dto.getCampo1(), dto.getCampo2() });
    }
}
```

### 7.5 Métodos en SistemaCompras — secciones comentadas

Cada integrante agrega sus métodos dentro de una sección marcada para evitar conflictos de merge:

```java
// ============================================================
// MÓDULO: ÓRDENES DE COMPRA (Integrante B)
// ============================================================
public void crearOrdenDeCompra(...) { ... }
public EstadoOrdenCompra confirmarOrdenDeCompra(...) { ... }

// ============================================================
// MÓDULO: DOCUMENTOS COMERCIALES (Integrante C)
// ============================================================
public Factura registrarFactura(...) { ... }
```

### 7.6 Validaciones en controllers

El orden estándar de validación dentro de un método de guardar es:

```java
private void guardar() {
    // 1. Validar campos vacíos / formato
    String valor = vista.getCampo();
    if (valor.isEmpty()) {
        vista.mostrarMensaje("El campo es obligatorio.", "Error", JOptionPane.WARNING_MESSAGE);
        return;
    }
    // 2. Validar reglas de negocio (duplicados, límites, etc.)
    if (sistema.buscarX(valor) != null) {
        vista.mostrarMensaje("Ya existe un registro con ese valor.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    // 3. Ejecutar la operación
    sistema.agregarX(valor);
    // 4. Feedback + limpiar + refrescar tabla
    vista.limpiarFormulario();
    vista.mostrarMensaje("Guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    cargarTabla();
}
```

### 7.7 Patrón para agregar un módulo nuevo

Siempre en este orden:

```
1. model/     → agregar/modificar clases de dominio si hace falta
2. dto/       → crear XDTO con los campos que necesita la tabla
3. view/      → crear XGUI extends JInternalFrame
4. controller/→ crear XController (se instancia desde XGUI)
5. SistemaCompras.java → agregar métodos en la sección del módulo
6. Menu.java  → agregar JMenuItem que abra la XGUI
```

---

## 8. Principios de Diseño (según cátedra)

- **Alta cohesión:** cada clase tiene una única responsabilidad
- **Bajo acoplamiento:** las capas se comunican por interfaces definidas (DTOs, métodos públicos)
- **Encapsulamiento:** atributos privados siempre
- **Herencia justificada:** solo relación "es-un" real
- **Polimorfismo:**
  - `Item.getTipoItem()` → "PRODUCTO" / "SERVICIO"
  - `DocumentoComercial.getTipoDocumento()` → "FACTURA" / "NOTA_DE_DEBITO" / "NOTA_DE_CREDITO"
  - `DocumentoComercial.getImpactoDeuda()` → +saldo; `NotaDeCredito` lo sobrescribe con −saldo
  - `MedioDePago.getTipo()` → "EFECTIVO" / "TRANSFERENCIA_BANCARIA" / etc.
- **No God Class:** SistemaCompras es fachada/singleton válido en Fase 2, pero la lógica debe estar en las clases dueñas de los datos

---

## 9. Instrucciones para Claude Code

- Responder siempre en **español**
- Trabajar **paso a paso**, confirmando cada artefacto antes del siguiente
- Cuando se agregue un nuevo módulo, seguir el patrón: model → dto → view → controller → Menu.java
- Respetar el patrón MVC: la **vista no accede al modelo directamente**, siempre a través del controller
- Si hay que agregar lógica de negocio compleja, implementarla en la clase del modelo que "posee" el dato, no en el controller ni en SistemaCompras directamente
- Ante dudas de diseño, priorizar **alta cohesión** sobre conveniencia
- Justificar decisiones en términos de los principios OO de la cátedra cuando corresponda

---

## 10. Repo

```
https://github.com/SethBastianUade/UADE-POO-1C-2026
```
Branch principal: `main`
Estructura: `Fase I/` (UML) y `Fase 2/TPO/` (código Java)

---

## 11. Compilar, Ejecutar y Testear

### 11.1 Compilar y ejecutar (sin Maven/Gradle)
El proyecto es Java puro. No hay PATH global de `javac`; usar el JDK de IntelliJ:

```powershell
# JDK disponible en la máquina: C:\Users\feder\.jdks\openjdk-24.0.1\bin
$javac = "C:\Users\feder\.jdks\openjdk-24.0.1\bin\javac.exe"
$java  = "C:\Users\feder\.jdks\openjdk-24.0.1\bin\java.exe"
$src   = "Fase 2\TPO\src\main\java"
$out   = "$env:TEMP\poo_build"

# Compilar todo
$files = Get-ChildItem $src -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
& $javac -encoding UTF-8 -d $out $files

# Ejecutar (clase main: mvc.Menu$VentanaPrincipal)
& $java -cp $out 'mvc.Menu$VentanaPrincipal'
```
> Lo más cómodo es abrir el proyecto en IntelliJ y correr `Menu.java`.
> Usuarios de prueba: `admin/1234` (SUPERVISOR) · `operador/1234` (OPERADOR).

### 11.2 Estado de testing
- **No hay framework de tests** (no se usa JUnit; la cátedra no lo pide).
- La lógica crítica del modelo se validó con **clases de prueba descartables con `main()`**
  (compiladas contra `$out` y ejecutadas a mano) — patrón recomendado para verificar reglas sin GUI:
  ```java
  public class TestX {
      public static void main(String[] args) {
          SistemaCompras s = SistemaCompras.getInstance();
          // armar escenario con los métodos de negocio reales y verificar con asserts manuales
      }
  }
  ```
- Verificado así: cálculo de retenciones (alícuotas por condición, MNI, exclusiones vigentes/vencidas)
  y consistencia de los datos de prueba (estados de OC/Documentos/OP, deuda con NC restando).

### 11.3 Pendientes / mejoras sugeridas (no bloquean la entrega)

**Reglas de negocio que el TPO menciona y aún NO están implementadas:**
- [ ] **Coherencia ítems↔rubros del proveedor** al registrar factura (CLAUDE.md §6, paso 3):
      hoy `validarContraOC` solo valida amparo de OC + precio, no que el ítem pertenezca a un rubro
      asociado al proveedor. Encaja como chequeo extra en `DocumentoComercial.validarContraOC`.
- [ ] **Recepción de mercadería**: `LineaOrdenCompra` tiene `registrarRecepcion()` y la OC tiene
      `evaluarYActualizarEstado()` (RECIBIDA_PARCIALMENTE / CERRADA), pero no hay GUI que registre
      recepciones. Las OC nunca pasan a esos estados desde la interfaz.
- [ ] **Discriminación de IVA en ND/NC**: solo `Factura` tiene `baseImponibleIVA`/`montoIVA`.
      En el Libro IVA las notas figuran con IVA $0. Agregar esos campos al modelo si se requiere.

**Verificaciones funcionales a hacer a mano (GUI):**
- [ ] Flujo OC: crear → agregar líneas → confirmar (probar caso EMITIDA y caso PENDIENTE_APROBACION
      con proveedor de límite bajo) → aprobar como `admin`, verificar que `operador` recibe error.
- [ ] Flujo Documentos: factura con ítems/precios de la OC → APROBADO; con precio distinto o ítem
      sin OC → OBSERVADO → aprobar como `admin`. Registrar ND/NC eligiendo factura de origen.
- [ ] Flujo OP: tildar documentos, ver retenciones en vivo, cargar medios por el neto exacto, emitir
      y verificar que el documento pasa a PARCIALMENTE_CANCELADO / CANCELADO.
- [ ] Caso exclusión: cargar certificado de IVA vigente y confirmar que la fila IVA no retiene.
- [ ] Consultas: validar filtros de fecha vacíos vs. con rango, y que la cuenta corriente cierre
      (saldo final == deuda actual del label).

**Calidad de código / detalles menores:**
- [ ] Actualizar el **diagrama de clases de Fase I**: firma de `calcularDeudaActual(List)`,
      nuevos métodos (`getImpactoDeuda`, `calcularRetenciones`, `confirmar`, `validarContraOC`, etc.).
- [ ] `Producto` tiene control de stock (`stockActual`/`stockMinimo`) que no se descuenta al recibir.
- [ ] Validar duplicados de N° de OC/OP igual que se hace con documentos (hoy la numeración es
      autoincremental, así que no colisiona, pero no hay chequeo si se cargan a mano).
- [ ] `cargarDatosDePrueba()` se ejecuta siempre: comentar esa línea del constructor de
      `SistemaCompras` para la entrega final o para demostrar el sistema vacío.
