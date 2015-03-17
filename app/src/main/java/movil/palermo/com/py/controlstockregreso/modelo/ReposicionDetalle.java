package movil.palermo.com.py.controlstockregreso.modelo;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by jcolman on 02/03/2015.
 */
public class ReposicionDetalle implements Serializable {
   public static final String COL_CONTROL = "control_id";

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(foreign = true,columnName = "control_id",foreignAutoCreate = true,foreignAutoRefresh = true)
    private Control control;
    @DatabaseField
    private Integer orden;
    @DatabaseField(foreign = true,columnName = "producto_id",foreignAutoCreate = true,foreignAutoRefresh = true)
    private Producto producto;
    @DatabaseField(foreign = true,columnName = "unidad_medida_id",foreignAutoCreate = true,foreignAutoRefresh = true)
    private UnidadMedida unidadMedida;
    @DatabaseField
    private Integer cantidad;
    @DatabaseField
    private String uuid;


    public ReposicionDetalle() {

    }

    public ReposicionDetalle(Control control, Integer orden, Producto producto, UnidadMedida unidadMedida, Integer cantidad) {
        this.control = control;
        this.orden = orden;
        this.producto = producto;
        this.unidadMedida = unidadMedida;
        this.cantidad = cantidad;
        String key = control.getSesion().getId()+""+ control.getId()+""+id+""+control.getSesion().getResponsable()+""+control.getSesion().getFechaControl()+""+producto.getId()+""+unidadMedida.getId()+"";
        this.uuid = UUID.nameUUIDFromBytes(key.getBytes()).toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Control getControl() {
        return control;
    }

    public void setControl(Control control) {
        this.control = control;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}