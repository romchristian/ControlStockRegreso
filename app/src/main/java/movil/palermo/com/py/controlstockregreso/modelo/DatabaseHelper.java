package movil.palermo.com.py.controlstockregreso.modelo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

import movil.palermo.com.py.controlstockregreso.R;

/**
 * Created by cromero on 18/11/2014.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "stock.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 3;

    private RuntimeExceptionDao<Vendedor, Integer> vendedorDao = null;
    private RuntimeExceptionDao<Conductor, Integer> conductorDao = null;
    private RuntimeExceptionDao<Vehiculo, Integer> vehiculoDao = null;
    private RuntimeExceptionDao<UnidadMedida, Integer> unidadMedidaDao = null;
    private RuntimeExceptionDao<Producto, Integer> productoDao = null;
    private RuntimeExceptionDao<Control, Integer> controlDao = null;
    private RuntimeExceptionDao<ControlDetalle, Integer> controlDetalleDao = null;
    private RuntimeExceptionDao<Sesion, Integer> sesionDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Producto.class);
            Log.i(DatabaseHelper.class.getName(), "Tabla Producto creada!");
            TableUtils.createTable(connectionSource, Vehiculo.class);
            Log.i(DatabaseHelper.class.getName(), "Tabla Vehiculo creada!");
            TableUtils.createTable(connectionSource, Conductor.class);
            Log.i(DatabaseHelper.class.getName(), "Tabla Conductor creada!");
            TableUtils.createTable(connectionSource, Vendedor.class);
            Log.i(DatabaseHelper.class.getName(), "Tabla Vendedor creada!");
            TableUtils.createTable(connectionSource, UnidadMedida.class);
            Log.i(DatabaseHelper.class.getName(), "Tabla UnidadMedida creada!");
            TableUtils.createTable(connectionSource, Sesion.class);
            Log.i(DatabaseHelper.class.getName(), "Tabla Session creada!");
            TableUtils.createTable(connectionSource, Control.class);
            Log.i(DatabaseHelper.class.getName(), "Tabla Control creada!");
            TableUtils.createTable(connectionSource, ControlDetalle.class);
            Log.i(DatabaseHelper.class.getName(), "Tabla ControlDetalle creada!");


        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

            Log.i(DatabaseHelper.class.getName(), "onUpgrade");


            switch (newVersion) {
                case 2:
                   getVehiculoDao().executeRaw("Alter table vehiculo add column numero text");
                    break;
                case 3:
                    getProductoDao().executeRaw("Alter table producto add column orden int");
                    getProductoDao().executeRaw("Alter table producto add column kit int");
                default:
                    break;
            }


    }


    public RuntimeExceptionDao<Producto, Integer> getProductoDao() {
        if (productoDao == null) {
            productoDao = getRuntimeExceptionDao(Producto.class);
        }
        return productoDao;
    }


    public RuntimeExceptionDao<Vehiculo, Integer> getVehiculoDao() {
        if (vehiculoDao == null) {
            vehiculoDao = getRuntimeExceptionDao(Vehiculo.class);
        }
        return vehiculoDao;
    }

    public RuntimeExceptionDao<Vendedor, Integer> getVendedorDao() {
        if (vehiculoDao == null)
            vendedorDao = getRuntimeExceptionDao(Vendedor.class);
        return vendedorDao;
    }

    public RuntimeExceptionDao<Conductor, Integer> getConductorDao() {
        if(conductorDao == null)
            conductorDao = getRuntimeExceptionDao(Conductor.class);
        return conductorDao;
    }

    public RuntimeExceptionDao<UnidadMedida, Integer> getUnidadMedidaDao() {
        if(unidadMedidaDao == null)
            unidadMedidaDao = getRuntimeExceptionDao(UnidadMedida.class);
        return unidadMedidaDao;
    }

    public RuntimeExceptionDao<Control, Integer> getControlDao() {
        if(controlDao == null)
            controlDao = getRuntimeExceptionDao(Control.class);
        return controlDao;
    }

    public RuntimeExceptionDao<ControlDetalle, Integer> getControlDetalleDao() {
        if(controlDetalleDao == null)
            controlDetalleDao = getRuntimeExceptionDao(ControlDetalle.class);
        return controlDetalleDao;
    }

    public RuntimeExceptionDao<Sesion, Integer> getSesionDao() {
        if (sesionDao == null)
            sesionDao = getRuntimeExceptionDao(Sesion.class);
        return sesionDao;
    }

    @Override
    public void close() {
        super.close();
        vendedorDao = null;
        conductorDao = null;
        vehiculoDao = null;
        productoDao = null;
        unidadMedidaDao = null;
        controlDao = null;
        controlDetalleDao = null;
        sesionDao = null;
    }


    public void extraerBD(String packagename){
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();




                String currentDBPath = "/data/data/movil.palermo.com.py.controlstockregreso/databases/"+DATABASE_NAME;
                Log.d("DatabaseHelper","PATHHHHHHHHHHHHHHHHHHHHHHHHHH: " + currentDBPath);
                Log.d("DatabaseHelper","destino: " + sd.getAbsolutePath());
                String backupDBPath = "stock.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                //if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                //}

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
