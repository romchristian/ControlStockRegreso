package movil.palermo.com.py.stockregreso.modelo;

/**
 * Created by cromero on 31/12/2014.
 */
public class ResponseReposicion {
    private int controlId;
    private int id;
    private boolean exito;

    public ResponseReposicion() {
    }

    public ResponseReposicion(int controlId, int id,boolean exito) {
        this.controlId = controlId;
        this.id = id;
        this.exito = exito;
    }

    public int getControlId() {
        return controlId;
    }

    public void setControlId(int controlId) {
        this.controlId = controlId;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
