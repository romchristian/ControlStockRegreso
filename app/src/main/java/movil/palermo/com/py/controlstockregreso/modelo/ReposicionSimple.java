package movil.palermo.com.py.controlstockregreso.modelo;

import java.util.UUID;

/**
 * Created by cromero on 17/03/2015.
 */
public class ReposicionSimple {
    private String uuid;
    private String controluuid;
    private Integer productoId;
    private Integer unidadmedidaId;
    private Integer cantidad;
    private Integer id;
    private Integer controlid;

    public ReposicionSimple(ReposicionDetalle rd) {
        Control c = rd.getControl();

        this.productoId = rd.getProducto().getId();
        this.unidadmedidaId = rd.getUnidadMedida().getId();
        this.cantidad = rd.getCantidad();

        String key = c.getUuid()+""+rd.getId()+""+productoId+""+unidadmedidaId;

        this.controluuid = c.getUuid();
        this.uuid = UUID.nameUUIDFromBytes(key.getBytes()).toString();
        this.id = rd.getId();
        this.controlid = rd.getControl().getId();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public Integer getUnidadmedidaId() {
        return unidadmedidaId;
    }

    public void setUnidadmedidaId(Integer unidadmedidaId) {
        this.unidadmedidaId = unidadmedidaId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getControluuid() {
        return controluuid;
    }

    public void setControluuid(String controluuid) {
        this.controluuid = controluuid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getControlid() {
        return controlid;
    }

    public void setControlid(Integer controlid) {
        this.controlid = controlid;
    }
}
