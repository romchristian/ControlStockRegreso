package movil.palermo.com.py.controlstockregreso.modelo;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by cromero on 24/11/2014.
 */


public class ProductoUM implements Serializable {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private Integer productoId;
    @DatabaseField
    private Integer unidadMedidaId;
    @DatabaseField
    private Integer cantidad;

    public ProductoUM() {
    }

    public ProductoUM(Integer productoId, Integer unidadMedidaId, Integer cantidad) {
        this.productoId = productoId;
        this.unidadMedidaId = unidadMedidaId;
        this.cantidad = cantidad;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
