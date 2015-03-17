package movil.palermo.com.py.controlstockregreso.modelo;

/**
 * Created by cromero on 17/03/2015.
 */
public class ReposicionSimple {
    private String uuid;
    private Integer sesionId;
    private Integer controlId;
    private Integer productoId;
    private Integer unidadmedidaId;
    private Integer cantidad;

    public ReposicionSimple(String uuid, Integer sesionId, Integer controlId, Integer productoId, Integer unidadmedidaId, Integer cantidad) {
        this.uuid = uuid;
        this.sesionId = sesionId;
        this.controlId = controlId;
        this.productoId = productoId;
        this.unidadmedidaId = unidadmedidaId;
        this.cantidad = cantidad;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getSesionId() {
        return sesionId;
    }

    public void setSesionId(Integer sesionId) {
        this.sesionId = sesionId;
    }

    public Integer getControlId() {
        return controlId;
    }

    public void setControlId(Integer controlId) {
        this.controlId = controlId;
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
}
