package movil.palermo.com.py.controlstockregreso.modelo;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by cromero on 18/11/2014.
 */
public class Vehiculo {
    @DatabaseField(id = true)
    private Integer id;
    @DatabaseField(index = true)
    private String marca;
    @DatabaseField
    private String chapa;

    public Vehiculo() {

    }

    public Vehiculo(Integer id, String marca, String chapa) {
        this.id = id;
        this.marca = marca;
        this.chapa = chapa;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getChapa() {
        return chapa;
    }

    public void setChapa(String chapa) {
        this.chapa = chapa;
    }
}
