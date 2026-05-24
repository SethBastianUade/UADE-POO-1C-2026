package mvc.model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    // Variable auxiliar para autoincrementar el ID de los rubros
    private int contadorIdRubros = 1;
    private int contadorIdItems = 1;

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
    }

    // 4. Método para obtener la instancia 
    // Usamos "synchronized" para que sea seguro si dos ventanas lo piden al mismo tiempo exacto
    public static synchronized SistemaCompras getInstance() {
        if (instanciaUnica == null) {
            instanciaUnica = new SistemaCompras();
        }
        return instanciaUnica;
    }

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
    // Alta de Producto
    public void agregarProducto(String cod, String desc, String uni, double precio, 
                                double iva, Rubro rubro, String lote, LocalDate vto,int stockActual, int stockMin) {
        Producto p = new Producto(contadorIdItems++, cod, desc, uni, precio, iva, rubro, lote, vto, stockActual, stockMin);
        items.add(p);
    }
    // Alta de Servicio
    public void agregarServicio(String cod, String desc, String uni, double precio, 
                                double iva, Rubro rubro, String mod, int horas, String req) {
        Servicio s = new Servicio(contadorIdItems++, cod, desc, uni, precio, iva, rubro, mod, horas, req);
        items.add(s);
    }

    public List<Item> getItems() {
        return items;
    }
}
