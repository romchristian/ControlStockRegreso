package movil.palermo.com.py.stockregreso.modelo;

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
    @DatabaseField(foreign = true,columnName = "vehiculo_id",canBeNull = true,foreignAutoCreate = true,foreignAutoRefresh = true)
    private Vehiculo vehiculo;
    @DatabaseField
    private String estado;

    public Conductor(){

    }

    public Conductor(Integer id, String nombre, Integer ci, Vehiculo vehiculo) {
        this.id = id;
        this.nombre = nombre;
        this.ci = ci;
        this.estado = "N";
        this.vehiculo = vehiculo;
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

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

}
