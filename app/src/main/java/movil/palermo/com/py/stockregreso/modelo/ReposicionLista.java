package movil.palermo.com.py.stockregreso.modelo;

import java.util.List;

/**
 * Created by jcolman on 11/06/2015.
 */
public class ReposicionLista {

    private List<ReposicionSimple> reposiciones;
    private String telefonoId;

    public ReposicionLista (){

    }

    public ReposicionLista(List<ReposicionSimple> reposiciones, String telefonoId) {
        this.reposiciones = reposiciones;
        this.telefonoId = telefonoId;
    }

    public List<ReposicionSimple> getReposiciones() {
        return reposiciones;
    }

    public void setReposiciones(List<ReposicionSimple> reposiciones) {
        this.reposiciones = reposiciones;
    }

    public String getTelefonoId() {
        return telefonoId;
    }

    public void setTelefonoId(String telefonoId) {
        this.telefonoId = telefonoId;
    }

}
