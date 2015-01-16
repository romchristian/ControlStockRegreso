package movil.palermo.com.py.controlstockregreso.modelo;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by cromero on 18/11/2014.
 */
public class Conductor implements Serializable{
    @DatabaseField(id = true)
    private Integer id;
    @DatabaseField(index = true)
    private String nombre;
    @DatabaseField
    private Integer ci;
    @DatabaseField
    private String estado;

    public Conductor() {
    }

    public Conductor(Integer id, String nombre, Integer ci) {
        this.id = id;
        this.nombre = nombre;
        this.ci = ci;
        this.estado = "N";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCi() {
        return ci;
    }

    public void setCi(Integer ci) {
        this.ci = ci;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
