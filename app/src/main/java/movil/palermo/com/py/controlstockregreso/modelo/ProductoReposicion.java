package movil.palermo.com.py.controlstockregreso.modelo;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by jcolman on 12/02/2015.
 */
public class ProductoReposicion implements Serializable {

    private int id;
    private String nombre;
    private Integer kit;
    private int cantReposicionCajas;
    private int cantReposicionGruesas;
    private int cantReposicionCajetillas;
    private int cantReposicionUnidad;
    private int cantCajas;
    private int cantGruesas;
    private int cantCajetillas;
    private int cantUnidad;
    private int cajetillasPorCaja;
    private int cajetillasPorGruesa;
    private int gruesasPorCaja;



    public ProductoReposicion(String[] row, String[] cajetillasXCajaRow) {
        this.id = Integer.valueOf(row[0]==null?"0":row[0]);
        this.nombre = row[1];
        this.kit = Integer.valueOf(row[2]==null?"0":row[2]);
        this.cantCajas = Integer.valueOf(row[3]==null?"0":row[3]);
        this.cantGruesas = Integer.valueOf(row[4]==null?"0":row[4]);
        this.cantCajetillas = Integer.valueOf(row[5]==null?"0":row[5]);
        this.cantUnidad =  Integer.valueOf(row[6]==null?"0":row[6]);
        this.cantReposicionGruesas = Integer.valueOf(row[7]==null?"0":row[7]);
        this.cantReposicionUnidad = Integer.valueOf(row[8]==null?"0":row[8]);
        this.cajetillasPorCaja = Integer.valueOf(cajetillasXCajaRow==null?"1":cajetillasXCajaRow[0]) * this.cantCajas;
        //Log.d("CajetillasXCaja", "FILA RESULTADO DE CONSULTA " + cajetillasPorCaja);
        this.gruesasPorCaja = this.cantCajas*50;
        //Log.d("GruesasXCaja", "FILA RESULTADO DE CONSULTA " + gruesasPorCaja);
        //this.cajetillasPorGruesa= (this.cantCajas * this.cajetillasPorCaja)/(this.cantGruesas *this.gruesasPorCaja);
        this.cajetillasPorGruesa= this.cantGruesas * (Integer.valueOf(cajetillasXCajaRow==null?"1":cajetillasXCajaRow[0])/50);
        //Log.d("CajetillasXGruesa", "FILA RESULTADO DE CONSULTA " + cajetillasPorGruesa);
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

    public int getCantReposicionCajas() {
        return cantReposicionCajas;
    }

    public void setCantReposicionCajas(int cantReposicionCajas) {
        this.cantReposicionCajas = cantReposicionCajas;
    }

    public int getCantReposicionGruesas() {
        return cantReposicionGruesas;
    }

    public void setCantReposicionGruesas(int cantReposicionGruesas) {
        this.cantReposicionGruesas = cantReposicionGruesas;
    }

    public int getCantReposicionCajetillas() {
        return cantReposicionCajetillas;
    }

    public void setCantReposicionCajetillas(int cantReposicionCajetillas) {
        this.cantReposicionCajetillas = cantReposicionCajetillas;
    }

    public int getCantReposicionUnidad() {
        return cantReposicionUnidad;
    }

    public void setCantReposicionUnidad(int cantReposicionUnidad) {
        this.cantReposicionUnidad = cantReposicionUnidad;
    }

    public int getCantCajas() {
        return cantCajas;
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
    public int getCajetillasPorCaja() {
        return cajetillasPorCaja;
    }

    public void setCajetillasPorCaja(int cajetillasPorCaja) {
        this.cajetillasPorCaja = cajetillasPorCaja;
    }

    public int getCajetillasPorGruesa() {
        return cajetillasPorGruesa;
    }

    public void setCajetillasPorGruesa(int cajetillasPorGruesa) {
        this.cajetillasPorGruesa = cajetillasPorGruesa;
    }

    public int getGruesasPorCaja() {
        return gruesasPorCaja;
    }

    public void setGruesasPorCaja(int gruesasPorCaja) {
        this.gruesasPorCaja = gruesasPorCaja;
    }
}
