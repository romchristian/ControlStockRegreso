package movil.palermo.com.py.stockregreso.modelo;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by cromero on 18/11/2014.
 */
public class Vendedor implements Serializable{
    @DatabaseField(id = true)
    private Integer id;
    @DatabaseField(index = true)
    private String nombre;
    @DatabaseField
    private Integer depositoId;
    @DatabaseField(foreign = true,columnName = "conductor_id",canBeNull = true,foreignAutoCreate = true,foreignAutoRefresh = true)
    private Conductor conductor;
    @DatabaseField
    private String estado;

    public Vendedor() {
    }

    public Vendedor(Integer id, String nombre, Integer depositoId, Conductor conductor) {
        this.id = id;
        this.nombre = nombre;
        this.depositoId = depositoId;
        this.conductor = conductor;
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

    public Integer getDepositoId() {
        return depositoId;
    }

    public void setDepositoId(Integer depositoId) {
        this.depositoId = depositoId;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
