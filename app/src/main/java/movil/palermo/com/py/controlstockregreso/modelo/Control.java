package movil.palermo.com.py.controlstockregreso.modelo;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by cromero on 24/11/2014.
 */
public class Control implements Serializable{

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(foreign = true, columnName = "sesion_id",canBeNull = false,foreignAutoCreate = true,foreignAutoRefresh = true)
    private Sesion sesion;
    @DatabaseField(foreign = true, columnName = "vendedor_id",canBeNull = false,foreignAutoCreate = true,foreignAutoRefresh = true)
    private Vendedor vendedor;
    @DatabaseField(foreign = true, columnName = "conductor_id",canBeNull = false,foreignAutoCreate = true,foreignAutoRefresh = true)
    private Conductor conductor;
    @DatabaseField(foreign = true, columnName = "vehiculo_id",canBeNull = false,foreignAutoCreate = true,foreignAutoRefresh = true)
    private Vehiculo vehiculo;
    @DatabaseField
    private String vehiculoChapa;
    @DatabaseField
    private Integer km;
    @DatabaseField
    private Date fechaControl;



    public Control() {
    }

    public Control(Sesion sesion, Vendedor vendedor, Conductor conductor, Vehiculo vehiculo, String vehiculoChapa, Integer km) {
        this.sesion = sesion;
        this.vendedor = vendedor;
        this.conductor = conductor;
        this.vehiculo = vehiculo;
        this.vehiculoChapa = vehiculoChapa;
        this.km = km;
        this.fechaControl = sesion!=null?sesion.getFechaControl()!=null?sesion.getFechaControl():new Date():new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Sesion getSesion() {
        return sesion;
    }

    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public String getVehiculoChapa() {
        return vehiculoChapa;
    }

    public void setVehiculoChapa(String vehiculoChapa) {
        this.vehiculoChapa = vehiculoChapa;
    }

    public Integer getKm() {
        return km;
    }

    public void setKm(Integer km) {
        this.km = km;
    }

    public Date getFechaControl() {
        return fechaControl;
    }

    public void setFechaControl(Date fechaControl) {
        this.fechaControl = fechaControl;
    }
}
