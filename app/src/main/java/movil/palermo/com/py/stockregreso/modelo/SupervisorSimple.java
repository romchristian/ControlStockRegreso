package movil.palermo.com.py.stockregreso.modelo;

/**
 * Created by cromero on 01/07/2015.
 */
public class SupervisorSimple {
    private int empresaId;
    private int SucursalId;
    private String nombre;
    private String userid;
    private String password;
    private String telefonoId;

    public int getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(int empresaId) {
        this.empresaId = empresaId;
    }

    public int getSucursalId() {
        return SucursalId;
    }

    public void setSucursalId(int sucursalId) {
        SucursalId = sucursalId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefonoId() {
        return telefonoId;
    }

    public void setTelefonoId(String telefonoId) {
        this.telefonoId = telefonoId;
    }
}
