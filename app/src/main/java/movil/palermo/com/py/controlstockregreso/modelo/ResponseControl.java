package movil.palermo.com.py.controlstockregreso.modelo;

/**
 * Created by cromero on 31/12/2014.
 */
public class ResponseControl {
    private int controlId;
    private boolean exito;

    public ResponseControl() {
    }

    public ResponseControl(int controlId, boolean exito) {
        this.controlId = controlId;
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
}
