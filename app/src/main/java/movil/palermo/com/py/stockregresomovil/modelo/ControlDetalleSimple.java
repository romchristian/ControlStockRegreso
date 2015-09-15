package movil.palermo.com.py.stockregresomovil.modelo;

import java.util.UUID;

/**
 * Created by cromero on 18/03/2015.
 */
public class ControlDetalleSimple {
    private String uuid;
    private String controlUuid;
    private Integer orden;
    private Integer productoId;
    private Integer unidadMedidaId;
    private Integer cantidad;

    public ControlDetalleSimple(String controlKey, ControlDetalle cd) {
        this.orden = cd.getOrden();
        this.productoId = cd.getProducto().getId();
        this.unidadMedidaId = cd.getUnidadMedida().getId();
        this.cantidad = cd.getCantidad();
        String key = controlKey+""+cd.getId()+""+productoId+""+unidadMedidaId;
        this.uuid = UUID.nameUUIDFromBytes(key.getBytes()).toString();
        this.controlUuid = UUID.nameUUIDFromBytes(controlKey.getBytes()).toString();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getControlUuid() {
        return controlUuid;
    }

    public void setControlUuid(String controlUuid) {
        this.controlUuid = controlUuid;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public Integer getUnidadMedidaId() {
        return unidadMedidaId;
    }

    public void setUnidadMedidaId(Integer unidadMedidaId) {
        this.unidadMedidaId = unidadMedidaId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
