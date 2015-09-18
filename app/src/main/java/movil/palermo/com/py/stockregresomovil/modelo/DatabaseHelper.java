package movil.palermo.com.py.stockregresomovil.modelo;

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

import movil.palermo.com.py.stockregresomovil.R;

/**
 * Created by cromero on 18/11/2014.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "stock.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 2;


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
            TableUtils.createTable(connectionSource, ReposicionDetalle.class);
            Log.i(DatabaseHelper.class.getName(), "Tabla ReposicionDetalle creada!");
            TableUtils.createTable(connectionSource, ProductoUM.class);
            Log.i(DatabaseHelper.class.getName(), "Tabla Producto creada!");


        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
    }


    public RuntimeExceptionDao<Producto, Integer> getProductoDao() {
        return getRuntimeExceptionDao(Producto.class);
    }


    public RuntimeExceptionDao<Vehiculo, Integer> getVehiculoDao() {
        return getRuntimeExceptionDao(Vehiculo.class);
    }

    public RuntimeExceptionDao<Vendedor, Integer> getVendedorDao() {
        return getRuntimeExceptionDao(Vendedor.class);
    }



    public RuntimeExceptionDao<Conductor, Integer> getConductorDao() {
        return getRuntimeExceptionDao(Conductor.class);
    }

    public RuntimeExceptionDao<UnidadMedida, Integer> getUnidadMedidaDao() {
        return getRuntimeExceptionDao(UnidadMedida.class);
    }

    public RuntimeExceptionDao<ProductoUM, Integer> getProductoUMDao() {
        return getRuntimeExceptionDao(ProductoUM.class);
    }

    public RuntimeExceptionDao<Control, Integer> getControlDao() {
        return getRuntimeExceptionDao(Control.class);
    }

    public RuntimeExceptionDao<ControlDetalle, Integer> getControlDetalleDao() {

        return getRuntimeExceptionDao(ControlDetalle.class);
    }

    public RuntimeExceptionDao<ReposicionDetalle, Integer> getReposicionDetalleDao() {

        return getRuntimeExceptionDao(ReposicionDetalle.class);
    }

    public RuntimeExceptionDao<Sesion, Integer> getSesionDao() {
        return getRuntimeExceptionDao(Sesion.class);
    }

    @Override
    public void close() {
        super.close();
    }


    public void extraerBD(String packagename){
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();




                String currentDBPath = "/data/data/movil.palermo.com.py.stockregresomovil/databases/"+DATABASE_NAME;
                //String currentDBPath = "/storage/sdcard0/Download/"+DATABASE_NAME;
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
