package movil.palermo.com.py.stockregresomovil.modelo;

import java.io.Serializable;

/**
 * Created by cromero on 03/12/2014.
 */
public class ProductoResumen implements Serializable{

    private int id;
    private String nombre;
    private Integer kit;
    private int cantRemisionCajas;
    private int cantRemisionGruesas;
    private int cantRemisionCajetillas;
    private int cantRemisionUnidad;
    private int cantCajas;
    private int cantGruesas;
    private int cantCajetillas;
    private int cantUnidad;
    private int esDevolucion;

    public ProductoResumen() {

    }

    public ProductoResumen(String[] row) {
        this.id = Integer.valueOf(row[0]==null?"0":row[0]);
        this.nombre = row[1];
        this.kit = Integer.valueOf(row[2]==null?"0":row[2]);
        this.cantCajas = Integer.valueOf(row[3]==null?"0":row[3]);
        this.cantGruesas = Integer.valueOf(row[4]==null?"0":row[4]);
        this.cantCajetillas = Integer.valueOf(row[5]==null?"0":row[5]);
        this.cantUnidad =  Integer.valueOf(row[6]==null?"0":row[6]);
        this.esDevolucion =  Integer.valueOf(row[7]==null?"0":row[7]);

    }

    public int getEsDevolucion() {
        return esDevolucion;
    }

    public void setEsDevolucion(int esDevolucion) {
        this.esDevolucion = esDevolucion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantRemisionCajas() {
        return cantRemisionCajas;
    }

    public void setCantRemisionCajas(int cantRemisionCajas) {
        this.cantRemisionCajas = cantRemisionCajas;
    }

    public int getCantRemisionGruesas() {
        return cantRemisionGruesas;
    }

    public void setCantRemisionGruesas(int cantRemisionGruesas) {
        this.cantRemisionGruesas = cantRemisionGruesas;
    }

    public int getCantRemisionCajetillas() {
        return cantRemisionCajetillas;
    }

    public void setCantRemisionCajetillas(int cantRemisionCajetillas) {
        this.cantRemisionCajetillas = cantRemisionCajetillas;
    }

    public int getCantRemisionUnidad() {
        return cantRemisionUnidad;
    }

    public void setCantRemisionUnidad(int cantRemisionUnidad) {
        this.cantRemisionUnidad = cantRemisionUnidad;
    }

    public int getCantCajas() {
        return cantCajas;
    }

    public int getGruesasPorCaja(){
        return (getCantCajas()*50);
    }

    public void setCantCajas(int cantCajas) {
        this.cantCajas = cantCajas;
    }

    public int getCantGruesas() {
        return cantGruesas;
    }

    public void setCantGruesas(int cantGruesas) {
        this.cantGruesas = cantGruesas;
    }

    public int getCantCajetillas() {
        return cantCajetillas;
    }

    public void setCantCajetillas(int cantCajetillas) {
        this.cantCajetillas = cantCajetillas;
    }

    public int getCantUnidad() {
        return cantUnidad;
    }

    public void setCantUnidad(int cantUnidad) {
        this.cantUnidad = cantUnidad;
    }

    public Integer getKit() {
        return kit;
    }

    public void setKit(Integer kit) {
        this.kit = kit;
    }
}
