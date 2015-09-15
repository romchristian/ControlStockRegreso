package movil.palermo.com.py.stockregresomovil.modelo;

/**
 * Created by jcolman on 07/01/2015.
 */
public class ResponseLogin {
    private String nombre;
    private boolean exito;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }
}
