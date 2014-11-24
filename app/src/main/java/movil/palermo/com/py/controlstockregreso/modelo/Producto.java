package movil.palermo.com.py.controlstockregreso.modelo;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by cromero on 14/11/2014.
 */


public class Producto {
    @DatabaseField(id = true)
    private Integer id;
    @DatabaseField(index = true)
    private String nombre;
    @DatabaseField
    private Integer unidadMedidadEstandar;
    @DatabaseField
    private String img;

    public Producto() {
    }

    public Producto(Integer id, String nombre, Integer unidadMedidadEstandar, String img) {
        this.id = id;
        this.nombre = nombre;
        this.unidadMedidadEstandar = unidadMedidadEstandar;
        this.img = img;
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

    public Integer getUnidadMedidadEstandar() {
        return unidadMedidadEstandar;
    }

    public void setUnidadMedidadEstandar(Integer unidadMedidadEstandar) {
        this.unidadMedidadEstandar = unidadMedidadEstandar;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
