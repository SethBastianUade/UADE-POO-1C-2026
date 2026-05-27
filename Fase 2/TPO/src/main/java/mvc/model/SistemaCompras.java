package mvc.model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import mvc.enums.RolUsuario;

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
}
