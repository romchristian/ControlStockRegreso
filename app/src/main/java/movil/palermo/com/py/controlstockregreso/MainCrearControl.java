package movil.palermo.com.py.controlstockregreso;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import movil.palermo.com.py.controlstockregreso.modelo.Conductor;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;
import movil.palermo.com.py.controlstockregreso.modelo.Vehiculo;
import movil.palermo.com.py.controlstockregreso.modelo.Vendedor;
import movil.palermo.com.py.controlstockregreso.util.UtilJson;


//import py.com.palermo.movil.movilcontrolstockretorno.RequestCodes;

public class MainCrearControl extends ActionBarActivity implements View.OnClickListener {
    public static final int AGREGAR_VENDEDOR = 101;
    public static final int AGREGAR_CHOFER = 102;
    public static final int AGREGAR_MOVIL = 103;
    public static final int CARGAR_PRODUCTOS = 104;

    private Button bttnCargarProductos, bttnFinalizarControl;
    private TextView txtVendedorValue, txtChoferValue, txtMovilValue;
    private ImageView searchVendedor, searchChofer, searchMovil;
    private FrameLayout recuadroVendedor, recuadroChofer, recuadroMovil;


    ProgressDialog p;

    Intent intentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();
        actionBar.setTitle("Chofer: Jose Colman");
        actionBar.setSubtitle("Resp: Christian Romero");

        setContentView(R.layout.activity_main_crear_control);

        // okImg = (ImageView) findViewById(R.id.ok_img);
        recuadroVendedor = (FrameLayout) findViewById(R.id.recuadroVendedor);
        recuadroChofer = (FrameLayout) findViewById(R.id.recuadroChofer);
        recuadroMovil = (FrameLayout) findViewById(R.id.recuadroMovil);
        txtVendedorValue = (TextView) findViewById(R.id.txtVwVendedorValue);
        txtChoferValue = (TextView) findViewById(R.id.txtVwChoferValue);
        txtMovilValue = (TextView) findViewById(R.id.txtVwMovilValue);


        searchVendedor = (ImageView) findViewById(R.id.search_vendedor);
        searchChofer = (ImageView) findViewById(R.id.search_chofer);
        searchMovil = (ImageView) findViewById(R.id.search_movil);
        bttnCargarProductos = (Button) findViewById(R.id.bttnCargarProductos);
        bttnFinalizarControl = (Button) findViewById(R.id.bttnFinalizarControl);


        searchVendedor.setOnClickListener(this);
        searchChofer.setOnClickListener(this);
        searchMovil.setOnClickListener(this);
        bttnCargarProductos.setOnClickListener(this);
        bttnFinalizarControl.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_crear_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_actualizar:

                final ImageView okImg = (ImageView) findViewById(R.id.ok_img);

                p = new ProgressDialog(this);
                p.setMessage("Actualizando...");
                p.setCancelable(false);
                p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                p.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        okImg.setVisibility(View.VISIBLE);

                        Animation fadeOut = new AlphaAnimation(1f, 0f);
                        fadeOut.setInterpolator(new AccelerateInterpolator());
                        fadeOut.setDuration(1000);

                        fadeOut.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                okImg.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        okImg.startAnimation(fadeOut);
                    }
                });

                p.show();

                p.setProgress(0);
                new Thread(new Task()).start();

                break;

            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_vendedor:
                intentMain = new Intent(this, ListaVendedor.class);
                startActivityForResult(intentMain, AGREGAR_VENDEDOR);
                break;
            case R.id.search_chofer:
                intentMain = new Intent(this, ListaChofer.class);
                startActivityForResult(intentMain, AGREGAR_CHOFER);
                break;
            case R.id.search_movil:
                intentMain = new Intent(this, ListaMovil.class);
                startActivityForResult(intentMain, AGREGAR_MOVIL);
                break;
            case R.id.bttnCargarProductos:
                intentMain = new Intent(this, ListaProductos.class);
                startActivityForResult(intentMain, CARGAR_PRODUCTOS);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle resultado = data.getExtras();
            String dato = resultado.getString("RESULTADO");
            switch (requestCode) {
                case AGREGAR_VENDEDOR:
                    txtVendedorValue.setText(dato);
                    recuadroVendedor.setBackgroundResource(R.drawable.recuadro_seleccionado);
                    break;
                case AGREGAR_CHOFER:
                    txtChoferValue.setText(dato);
                    recuadroChofer.setBackgroundResource(R.drawable.recuadro_seleccionado);
                    //txtVwChofer.setText("Chofer: " + dato);
                    //Toast.makeText(this, "Resultado: " + dato, Toast.LENGTH_SHORT).show();
                    break;
                case AGREGAR_MOVIL:
                    txtMovilValue.setText(dato);
                    recuadroMovil.setBackgroundResource(R.drawable.recuadro_seleccionado);
                    //txtVwMovil.setText("Móvil: " + dato);
                    //Toast.makeText(this, "Resultado: " + dato, Toast.LENGTH_SHORT).show();
                    break;
                case CARGAR_PRODUCTOS:
                    //Toast.makeText(this, "Resultado: " + dato, Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "No se seleccionó un vendedor", Toast.LENGTH_SHORT).show();
            }
        }
    }


    class Task implements Runnable {


        @Override
        public void run() {

          for(int i =0; i<100;i++ ){
              p.setProgress(i);
              try {
                  Thread.sleep(50);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          }
            p.dismiss();

        }

    }
}
