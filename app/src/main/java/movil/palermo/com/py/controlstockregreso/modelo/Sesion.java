package movil.palermo.com.py.controlstockregreso.modelo;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by cromero on 24/11/2014.
 */
public class Sesion {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private Date fechaControl;
    @DatabaseField
    private String responsable;


    public Sesion() {
    }

    public Sesion(Date fechaControl, String responsable) {
        this.fechaControl = fechaControl;
        this.responsable = responsable;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
