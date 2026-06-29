# Documentación Detallada — Sistema de Gestión Integral de Compras

**Materia:** Programación Orientada a Objetos — UADE, 1er cuatrimestre 2026
**Caso de negocio:** Gestión Integral de Compras, Contrataciones y Pagos del
*Sanatorio Privado Integral del Sur*
**Tecnología:** Java puro (sin frameworks, sin Maven/Gradle) + interfaz gráfica Swing

---

## 1. Objetivo del sistema

Modelar e implementar el circuito administrativo de compras de un sanatorio: un
**operador** registra proveedores, ítems y órdenes de compra; el sistema controla el
**límite de crédito** de cada proveedor, valida las **facturas** contra las órdenes,
calcula las **retenciones impositivas** al emitir las **órdenes de pago** y produce las
**consultas y reportes fiscales** (entre ellos el Libro IVA Compras). Un **supervisor**
tiene atribuciones extra para aprobar operaciones que superan los controles.

---

## 2. Arquitectura: MVC con controllers de módulo

El sistema usa **Modelo–Vista–Controlador estricto**, sin una clase central que concentre
todo (sin *God Class*). El flujo es siempre el mismo:

```
   ┌──────────────┐   eventos    ┌────────────────────┐   llamadas   ┌──────────────┐
   │  Vista       │ ───────────▶ │  Controller módulo │ ───────────▶ │  Modelo      │
   │  (Swing GUI) │ ◀─────────── │  (orquesta + datos)│ ◀─────────── │  (dominio)   │
   └──────────────┘    DTOs      └────────────────────┘   objetos    └──────────────┘
```

Reglas de la arquitectura:

- **La vista nunca accede al modelo.** Recibe y muestra **DTOs** (objetos planos sin
  lógica). Las vistas se instancian solo desde la capa de vista (Menú / GUIs padre).
- **El controller tiene dos roles:** (a) una instancia por GUI que suscribe los eventos
  y arma los DTOs, y (b) los **datos del módulo + métodos de negocio como `static`**,
  compartidos por toda la aplicación. Las listas `static` en memoria **simulan la base de
  datos**.
- **El modelo es dueño de sus reglas.** La lógica vive en la entidad que posee el dato
  (`OrdenDeCompra.confirmar`, `Proveedor.calcularRetenciones`,
  `DocumentoComercial.validarContraOC`); el controller solo orquesta.

### Propiedad de los datos por módulo

| Controller | Datos que posee |
|---|---|
| `LoginController` | usuarios + usuario logueado |
| `RubroController` | rubros |
| `ItemController` | ítems |
| `ProveedorController` | proveedores (con certificados y precios acordados adentro) |
| `OrdenDeCompraController` | órdenes de compra |
| `DocumentoComercialController` | documentos comerciales |
| `OrdenDePagoController` | órdenes de pago |

Los controllers de consulta (Parte 5) **no poseen datos**: leen de los controllers dueños.
Cuando un módulo necesita un dato ajeno, llama al método `static` del controller dueño
(ej.: `ProveedorController.buscarProveedorPorCuit(cuit)`), nunca duplica la lista.

### Estructura de paquetes

```
mvc/
├── Menu.java          ← punto de entrada (JDesktopPane con menú por módulos)
├── model/             ← entidades de dominio + reglas de negocio
├── enums/             ← estados y categorías (EstadoOrdenCompra, CondicionImpositiva, …)
├── controller/        ← un controller por módulo (dueño de datos + lógica static)
├── view/              ← GUIs Swing (JInternalFrame)
└── dto/               ← objetos de transferencia vista↔controller
```

---

## 3. Conceptos de POO aplicados (con código real del proyecto)

### 3.1 Encapsulamiento

Todos los atributos son `private` y se exponen mediante getters/setters. Más importante:
**la lógica de negocio vive en la clase que posee el dato**, no en el controller. Ejemplo:
la validación de CUIT (formato + dígito verificador) es responsabilidad del `Proveedor`.

```java
// model/Proveedor.java
public static boolean cuitEsValido(String cuit) {
    if (cuit == null) return false;
    String soloDigitos = cuit.replaceAll("[^0-9]", "");
    if (soloDigitos.length() != 11) return false;
    int[] pesos = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
    int suma = 0;
    for (int i = 0; i < 10; i++) suma += (soloDigitos.charAt(i) - '0') * pesos[i];
    int resto = suma % 11;
    int dv = 11 - resto;
    if (dv == 11) dv = 0;
    else if (dv == 10) return false;
    return dv == (soloDigitos.charAt(10) - '0');
}
```

El controller solo orquesta: valida y delega la regla al modelo.

```java
// controller/ProveedorController.java
if (!Proveedor.cuitEsValido(cuit)) {
    vista.mostrarMensaje("El CUIT no es válido (formato o dígito verificador).",
                         "Error", JOptionPane.ERROR_MESSAGE);
    return;
}
```

### 3.2 Herencia (clases abstractas + relación "es-un")

Tres jerarquías, cada una con una **clase abstracta** base que factoriza lo común:

```
Item (abstract)                     DocumentoComercial (abstract)      MedioDePago (abstract)
 ├── Producto                        ├── Factura                        ├── Efectivo
 └── Servicio                        ├── NotaDeDebito                   ├── TransferenciaBancaria
                                     └── NotaDeCredito                  ├── ChequePropio
                                                                        └── ChequeDeTerceros
```

`Item` declara lo compartido y deja abstracto lo que cada subtipo define:

```java
// model/Item.java
public abstract class Item {
    private int idItem;
    private String codigo;
    private double precioUnitarioBase;
    private double alicuotaIVA;
    // ...
    public abstract String getTipoItem();          // cada subclase responde su tipo
    public double calcularPrecioConIVA() {           // lógica común heredada
        return precioUnitarioBase * (1 + alicuotaIVA / 100);
    }
}
```

`NotaDeCredito` extiende `DocumentoComercial` agregando sus atributos propios y reusando
todo el comportamiento de la base mediante `super(...)`:

```java
// model/NotaDeCredito.java
public class NotaDeCredito extends DocumentoComercial {
    private String motivoCredito;
    private Factura facturaOrigen;

    public NotaDeCredito(int id, String numero, LocalDate fecha, double importe,
                         Proveedor proveedor, String motivoCredito, Factura facturaOrigen) {
        super(id, numero, fecha, importe, proveedor);   // reusa el constructor base
        this.motivoCredito = motivoCredito;
        this.facturaOrigen = facturaOrigen;
    }
    // ...
}
```

### 3.3 Polimorfismo

**Caso 1 — La Nota de Crédito resta deuda.** La base define el impacto positivo; la NC lo
sobrescribe en negativo. El cálculo de deuda del proveedor recorre los documentos sin
preguntar de qué tipo es cada uno:

```java
// model/DocumentoComercial.java (base)
public double getImpactoDeuda() { return getSaldoPendiente(); }   // +saldo

// model/NotaDeCredito.java (sobrescritura)
@Override
public double getImpactoDeuda() { return -getSaldoPendiente(); }  // −saldo
```

```java
// model/Proveedor.java — el cálculo es polimórfico: no usa instanceof
public double calcularDeudaActual(List<DocumentoComercial> documentos) {
    double deuda = 0.0;
    for (DocumentoComercial doc : documentos) {
        if (doc.getProveedor() == this && estaImpago(doc)) {
            deuda += doc.getImpactoDeuda();   // factura/ND suman, NC resta
        }
    }
    return deuda;
}
```

**Caso 2 — IVA discriminado.** La base lo deriva de las líneas; la `Factura` lo sobrescribe
con el importe sellado por AFIP. El reporte del Libro IVA llama siempre al mismo método:

```java
// model/DocumentoComercial.java (base) — derivado de las líneas
public double getMontoIVA() {
    double iva = 0.0;
    for (LineaDocumento linea : lineas) iva += linea.getSubtotal() * linea.getAlicuotaIVA() / 100;
    return iva;
}

// model/Factura.java (sobrescritura) — usa el IVA sellado
@Override
public double getMontoIVA() { return montoIVA; }
```

```java
// controller/ReportesFiscalesController.java — Libro IVA, sin instanceof
double signo = doc.getTipoDocumento().equals("NOTA_DE_CREDITO") ? -1 : 1;
double base  = signo * doc.getBaseImponible();
double iva   = signo * doc.getMontoIVA();
```

**Caso 3 — Métodos abstractos como contrato.** `getTipoDocumento()` / `getTipoItem()` /
`getTipo()` (de `MedioDePago`) obligan a cada subclase a identificarse, reemplazando largas
cadenas de `if/instanceof`.

### 3.4 Clases asociativas (relaciones N:M con atributos)

Cuando una relación tiene datos propios, se modela como clase intermedia:

- **`ProveedorItem`** — relación *Proveedor ↔ Item* con el **precio acordado** y la fecha
  del acuerdo. Un proveedor mantiene su lista de precios acordados:

```java
// model/Proveedor.java
public void acordarPrecioItem(Item item, double precio) {
    for (ProveedorItem pi : preciosAcordados) {
        if (pi.getItem().getCodigo().equals(item.getCodigo())) {
            pi.setPrecioAcordado(precio);   // ya existía → actualiza
            return;
        }
    }
    preciosAcordados.add(new ProveedorItem(item, precio, LocalDate.now()));
}
```

- **`DocumentoPago`** — relación *Orden de Pago ↔ Documento Comercial* con el **monto
  aplicado** a cada documento.

### 3.5 Enumerados (estados y categorías de dominio)

Los estados y categorías son `enum` (no `String` ni `int` mágicos), lo que da seguridad de
tipos: `EstadoOrdenCompra`, `EstadoCancelacionDocumento`, `EstadoRegistroDocumento`,
`CondicionImpositiva`, `RolUsuario`, `TipoImpuesto`. El cálculo de retenciones itera sobre
los valores del enum:

```java
// model/Proveedor.java
for (TipoImpuesto tipo : TipoImpuesto.values()) {
    if (tieneExclusionActiva(tipo, fecha)) continue;          // certificado vigente → no retiene
    double porcentaje = porcentajeRetencion(tipo);            // según CondicionImpositiva
    if (porcentaje <= 0) continue;
    retenciones.add(new Retencion(tipo, baseImponible, porcentaje, mniPara(tipo)));
}
```

### 3.6 Singleton — patrón evaluado y **deliberadamente descartado**

> **Importante para la defensa:** el proyecto **no usa Singleton**. Vale la pena explicar
> *por qué*, porque es una decisión de diseño consciente.

En una versión anterior existía una fachada `SistemaCompras` implementada como **Singleton**
(una única instancia global que concentraba todas las listas y reglas). Se la **eliminó**
porque degeneraba en una *God Class*: violaba la **alta cohesión** (una sola clase sabía de
proveedores, compras, documentos y pagos a la vez) y generaba **alto acoplamiento** (todo el
sistema dependía de ella).

**En su lugar** se distribuyó el estado en cada controller de módulo como miembros `static`.
Esto conserva la única ventaja real que buscábamos del Singleton — **un único punto de
acceso compartido a los datos del módulo durante la ejecución** — pero respetando la
cohesión: cada controller solo conoce lo suyo.

```java
// controller/OrdenDeCompraController.java
public class OrdenDeCompraController {
    // Único conjunto de OC compartido por toda la app (simula la tabla de la BD).
    private static final List<OrdenDeCompra> ordenesDeCompra = new ArrayList<>();
    private static int contadorIdOC = 1;
    // ...métodos de negocio static que operan SOLO sobre las OC...
}
```

La clase de carga de datos `DatosDePrueba` usa un **constructor privado** para impedir que
se la instancie (es una clase utilitaria de solo métodos `static`); es un idiom relacionado
pero **no** es Singleton (no expone una instancia única).

> Si la cátedra exigiera un Singleton literal, el lugar natural sería un componente
> transversal de configuración o de numeración de comprobantes; hoy esa necesidad no existe,
> así que aplicar el patrón sería sobre-ingeniería.

---

## 4. Principios de diseño (según cátedra)

- **Alta cohesión:** cada clase tiene una única responsabilidad (un controller por módulo,
  una entidad por concepto del dominio).
- **Bajo acoplamiento:** las capas se comunican por contratos definidos (DTOs y métodos
  `static` públicos), no por dependencias directas al estado interno.
- **Encapsulamiento:** atributos siempre privados; reglas en la clase dueña del dato.
- **Herencia justificada:** solo donde hay una relación "es-un" real
  (una Factura *es-un* DocumentoComercial).
- **Polimorfismo en lugar de `instanceof`:** el comportamiento que varía por subtipo se
  resuelve por sobrescritura (deuda, IVA, tipo de documento).
- **Sin God Class:** no hay fachada central; el conocimiento está repartido.

---

## 5. Flujos de negocio principales

### 5.1 Orden de Compra — control de límite de crédito
1. Se crea en estado `BORRADOR` y se le cargan líneas (ítem, cantidad, precio).
2. Al **confirmar**: `montoComprometido = deudaActual del proveedor + total de la nueva OC`.
   - Si `≤ límite autorizado` → **EMITIDA**.
   - Si lo supera → **PENDIENTE_APROBACION** (requiere un `SUPERVISOR`).

### 5.2 Recepción de mercadería
Sobre una OC `EMITIDA` se registran las cantidades recibidas por línea. Si el ítem es un
`Producto`, se **incrementa el stock**. La OC pasa a `RECIBIDA_PARCIALMENTE` o `CERRADA`
según si todas las líneas se completaron.

### 5.3 Registro de Documento Comercial
Al registrar una Factura se valida **contra las OC del proveedor**:
- cada línea debe estar **amparada** por una OC emitida con el **mismo precio**;
- el ítem debe pertenecer a un **rubro asociado** al proveedor (coherencia de conceptos).
Si todo coincide → `APROBADO`; si hay diferencia de precio o falta amparo → `OBSERVADO`
(lo debe aprobar un `SUPERVISOR`).

### 5.4 Orden de Pago — retenciones
Se seleccionan documentos a pagar y medios de pago. Por cada impuesto (IVA, Ganancias,
IIBB): si el proveedor tiene un **certificado de exclusión vigente**, no se retiene; si no,
se calcula `max(0, base − MNI) × porcentaje`, donde el porcentaje depende de la
`CondicionImpositiva`. Al emitir la OP se aplica el pago a cada documento, que actualiza su
estado de cancelación (`PARCIALMENTE_CANCELADO` / `CANCELADO`).

---

## 6. Roles y permisos

`Usuario.tienePermiso(accion)`: el **SUPERVISOR** puede todo; el **OPERADOR** puede todo
salvo `APROBAR_OC` y `APROBAR_DOCUMENTO`. Así se separan las tareas operativas de las de
control.

---

## 7. Cómo compilar y ejecutar

**Recomendado:** abrir el proyecto en IntelliJ y ejecutar `Menu.java`
(clase `mvc.Menu$VentanaPrincipal`).

**Por línea de comandos** (Java puro, sin Maven/Gradle), usando el JDK de IntelliJ:

```powershell
$jdk   = Get-ChildItem "$env:USERPROFILE\.jdks" -Directory |
         Where-Object { Test-Path "$($_.FullName)\bin\javac.exe" } |
         Select-Object -First 1 -ExpandProperty FullName
$src   = "Fase 2\TPO\src\main\java"
$out   = "$env:TEMP\poo_build"
$files = Get-ChildItem $src -Recurse -Filter *.java | Select-Object -ExpandProperty FullName
& "$jdk\bin\javac.exe" -encoding UTF-8 -d $out $files
& "$jdk\bin\java.exe" -cp $out 'mvc.Menu$VentanaPrincipal'
```

**Usuarios de prueba:** `admin / 1234` (SUPERVISOR) · `operador / 1234` (OPERADOR).
Los datos de ejemplo se cargan al iniciar desde `DatosDePrueba.cargar()`.

---

## 8. Mapa rápido de archivos clave

| Necesitás ver… | Archivo |
|---|---|
| Herencia + polimorfismo de documentos | `model/DocumentoComercial.java`, `Factura.java`, `NotaDeCredito.java` |
| Validación de CUIT + retenciones | `model/Proveedor.java` |
| Control de límite de crédito | `model/OrdenDeCompra.java`, `controller/OrdenDeCompraController.java` |
| Cálculo del Libro IVA (polimorfismo) | `controller/ReportesFiscalesController.java` |
| Datos `static` por módulo (en vez de Singleton) | cualquier `*Controller.java` (sección de negocio) |
