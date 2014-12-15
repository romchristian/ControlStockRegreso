package movil.palermo.com.py.controlstockregreso.modelo;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by cromero on 18/11/2014.
 */
public class Vehiculo implements Serializable{
    @DatabaseField(id = true)
    private Integer id;
    @DatabaseField(index = true)
    private String marca;
    @DatabaseField
    private String chapa;
    @DatabaseField
    private Integer numero;

    public Vehiculo() {

    }

    public Vehiculo(Integer id, String marca, String chapa) {
        this.id = id;
        this.marca = marca;
        this.chapa = chapa;
    }


    public Vehiculo(Integer id, String marca, String chapa, Integer numero) {
        this.id = id;
        this.marca = marca;
        this.chapa = chapa;
        this.numero = numero;
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

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }
}
