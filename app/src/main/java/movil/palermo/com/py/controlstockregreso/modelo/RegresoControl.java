package movil.palermo.com.py.controlstockregreso.modelo;

import java.util.Date;

/**
 * Created by cromero on 13/04/2015.
 */
public class RegresoControl {
    private String uuid;

    private String sesionuuid;

    private Date fechaControl;

    private Integer conductorId;

    private Integer vendedorId;

    private Integer depositoId;

    private Integer vehiculoId;

    private String vehiculoChapa;

    private String estado;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSesionuuid() {
        return sesionuuid;
    }

    public void setSesionuuid(String sesionuuid) {
        this.sesionuuid = sesionuuid;
    }

    public Date getFechaControl() {
        return fechaControl;
    }

    public void setFechaControl(Date fechaControl) {
        this.fechaControl = fechaControl;
    }

    public Integer getConductorId() {
        return conductorId;
    }

    public void setConductorId(Integer conductorId) {
        this.conductorId = conductorId;
    }

    public Integer getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Integer vendedorId) {
        this.vendedorId = vendedorId;
    }

    public Integer getDepositoId() {
        return depositoId;
    }

    public void setDepositoId(Integer depositoId) {
        this.depositoId = depositoId;
    }

    public Integer getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(Integer vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public String getVehiculoChapa() {
        return vehiculoChapa;
    }

    public void setVehiculoChapa(String vehiculoChapa) {
        this.vehiculoChapa = vehiculoChapa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
