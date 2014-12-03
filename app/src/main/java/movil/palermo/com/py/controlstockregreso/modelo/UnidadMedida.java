package movil.palermo.com.py.controlstockregreso.modelo;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by cromero on 24/11/2014.
 */

public class UnidadMedida implements Serializable {
    @DatabaseField(id = true)
    private Integer id;
    @DatabaseField(index = true)
    private String nombre;

    public UnidadMedida() {
    }

    public UnidadMedida(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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
}
