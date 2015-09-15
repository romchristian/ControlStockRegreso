package movil.palermo.com.py.stockregresomovil.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by cromero on 18/03/2015.
 */
public class ControlSimple {
    private String uuid;
    private String sesionUuid;
    private Integer vendedorId;
    private Integer conductorId;
    private Integer vehiculosId;
    private Integer depositoId;
    private String vehiculoChapa;
    private Date fechaControl;
    private String estado;
    private Integer id;
    private List<ControlDetalleSimple> detalles;

    public ControlSimple(Control c) {
        this.id = c.getId();
        Sesion s = c.getSesion();
        String skey = s.getId() + "" + s.getFechaControl() + "" + s.getResponsable();
        String key = skey+""+c.getId()+""+c.getVendedor().getId()+""+c.getConductor().getId()+""+c.getVehiculo().getId()+""+c.getFechaControl();
        this.sesionUuid = UUID.nameUUIDFromBytes(skey.getBytes()).toString();
        this.uuid = UUID.nameUUIDFromBytes(key.getBytes()).toString();
        this.vendedorId = c.getVendedor().getId();
        this.depositoId = c.getVendedor().getDepositoId();
        this.conductorId = c.getConductor().getId();
        this.vehiculosId = c.getVehiculo().getId();
        this.vehiculoChapa = c.getVehiculoChapa();
        this.fechaControl = c.getFechaControl();
        this.estado = c.getEstado();
        this.detalles = new ArrayList<>();
        for(ControlDetalle cd : c.getDetalles()){
            ControlDetalleSimple cds = new ControlDetalleSimple(key,cd);
            this.detalles.add(cds);
        }
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSesionUuid() {
        return sesionUuid;
    }

    public void setSesionUuid(String sesionUuid) {
        this.sesionUuid = sesionUuid;
    }

    public Integer getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Integer vendedorId) {
        this.vendedorId = vendedorId;
    }

    public Integer getConductorId() {
        return conductorId;
    }

    public void setConductorId(Integer conductorId) {
        this.conductorId = conductorId;
    }

    public Integer getVehiculosId() {
        return vehiculosId;
    }

    public void setVehiculosId(Integer vehiculosId) {
        this.vehiculosId = vehiculosId;
    }

    public String getVehiculoChapa() {
        return vehiculoChapa;
    }

    public void setVehiculoChapa(String vehiculoChapa) {
        this.vehiculoChapa = vehiculoChapa;
    }

    public Date getFechaControl() {
        return fechaControl;
    }

    public void setFechaControl(Date fechaControl) {
        this.fechaControl = fechaControl;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getDepositoId() {
        return depositoId;
    }

    public void setDepositoId(Integer depositoId) {
        this.depositoId = depositoId;
    }

    public List<ControlDetalleSimple> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<ControlDetalleSimple> detalles) {
        this.detalles = detalles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
