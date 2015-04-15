package movil.palermo.com.py.stockregreso.modelo;

import java.util.Date;
import java.util.UUID;

/**
 * Created by cromero on 18/03/2015.
 */
public class SesionSimple {
    private String uuid;
    private Date fechaControl;
    private String responsable;
    private ControlSimple controlSimple;

    public SesionSimple(Sesion s) {
        String key = s.getId()+""+s.getFechaControl()+""+s.getResponsable();
        this.uuid = UUID.nameUUIDFromBytes(key.getBytes()).toString();
        this.fechaControl = s.getFechaControl();
        this.responsable = s.getResponsable();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getFechaControl() {
        return fechaControl;
    }

    public void setFechaControl(Date fechaControl) {
        this.fechaControl = fechaControl;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public ControlSimple getControlSimple() {
        return controlSimple;
    }

    public void setControlSimple(ControlSimple controlSimple) {
        this.controlSimple = controlSimple;
    }
}
