package movil.palermo.com.py.stockregreso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.json.JSONException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import info.vividcode.android.zxing.CaptureActivity;
import info.vividcode.android.zxing.CaptureResult;
import movil.palermo.com.py.stockregreso.custom.ControlListAdapter;
import movil.palermo.com.py.stockregreso.custom.GsonRequest;
import movil.palermo.com.py.stockregreso.modelo.Control;
import movil.palermo.com.py.stockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.stockregreso.modelo.EstadoControl;
import movil.palermo.com.py.stockregreso.modelo.ReposicionDetalle;
import movil.palermo.com.py.stockregreso.modelo.ReposicionLista;
import movil.palermo.com.py.stockregreso.modelo.ReposicionSimple;
import movil.palermo.com.py.stockregreso.modelo.ResponseReposicion;
import movil.palermo.com.py.stockregreso.util.UtilJson;


public class ListaReposicionFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView listaReposicion;
    private List<Control> controlList = new ArrayList<Control>();
    private ControlListAdapter adapter;
    private DatabaseHelper databaseHelper;
    private ProgressDialog pDialog;
    private TextView txtVwNuevoControl;
    private ImageView imgVwflecha;
    private Control controlSeleccionado;
    public static final int REPOSICION = 101;
    private RuntimeExceptionDao<ReposicionDetalle, Integer> reposcionDetalleDao;
    private UtilJson utilJson;




    View rootView;

    public static ListaReposicionFragment newInstance(int sectionNumber) {
        ListaReposicionFragment fragment = new ListaReposicionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ListaReposicionFragment() {

    }

    @Override
    public void onResume() {
        recargaLista();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_lista_control, container, false);
        listaReposicion = (ListView) rootView.findViewById(R.id.listaControl);
        txtVwNuevoControl = (TextView) rootView.findViewById(R.id.txtVwNuevoControl);
        imgVwflecha = (ImageView) rootView.findViewById(R.id.imgFlecha);

        utilJson = new UtilJson(rootView.getContext());

        utilJson.setControlesAdapter(new ControlListAdapter(getActivity(), utilJson.getControles()));

        listaReposicion.setOnItemClickListener(this);

        listaReposicion.setAdapter(utilJson.getControlesAdapter());


        databaseHelper = new DatabaseHelper(getActivity());
        reposcionDetalleDao = databaseHelper.getReposicionDetalleDao();
        recargaLista();

        return rootView;
    }

    public void recargaLista() {
        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();


        utilJson.recargaControles();


        listaReposicion.setVisibility(View.VISIBLE);
        txtVwNuevoControl.setVisibility(View.GONE);
        imgVwflecha.setVisibility(View.GONE);

        hidePDialog();
    }


    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, View view, final int position, long l) {

        controlSeleccionado = (Control) adapterView.getItemAtPosition(position);

        Intent i = new Intent(rootView.getContext(), ListaReposicionProducto.class);
        i.putExtra("CONTROL", (Control) controlSeleccionado);
        startActivityForResult(i, REPOSICION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == CaptureActivity.RESULT_OK) {

            switch (requestCode) {
                case REPOSICION:
                    Object obj = data.getSerializableExtra("RESULTADO");

                    if (obj != null && obj instanceof Control) {

                        //envio reposiciones
                        //Control c = (Control) obj;
                        utilJson.enviarDatos();//enviarDatos(c);
                    }
                    break;
                case 1:
                    CaptureResult res = CaptureResult.parseResultIntent(data);
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(rootView.getContext(), notification);
                        r.play();
                        Vibrator v = (Vibrator) rootView.getContext().getSystemService(Context.VIBRATOR_SERVICE);

                        v.vibrate(1000);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Toast.makeText(rootView.getContext(), res.getContents() + " (" + res.getFormatName() + ")", Toast.LENGTH_LONG).show();
                    if (res.getContents().toString().equalsIgnoreCase("ventastp2014")) {

                        Intent i = new Intent(rootView.getContext(), MainCrearControlActivity.class);
                        i.putExtra("CONTROL", controlSeleccionado);
                        controlSeleccionado.setEstadoDescarga("N");
                        controlSeleccionado.setEstado(EstadoControl.MODIFICADO.toString());
                        startActivity(i);
                    } else {
                        Toast.makeText(rootView.getContext(), "Código de verificación incorrecto", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

        }

    }


    private void enviarDatos(Control c) {
        UtilJson util = new UtilJson((Activity) rootView.getContext());
        if (util.estaConectado()) {
            probarServicio(c);
        }
    }

    private void probarServicio(final Control c) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, (new UtilJson(rootView.getContext()).prefUrl) + "/test",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if (response != null && response.compareTo("true") == 0) {
                            try {
                                insertaReposicion(c);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    private void insertaReposicion(final Control c) throws JSONException {

        List<ReposicionDetalle> lista = null;
        try {
            lista = reposcionDetalleDao.queryBuilder()
                    .where().eq(ReposicionDetalle.COL_CONTROL, c)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (lista != null && !lista.isEmpty()) {

            List<ReposicionSimple> lista2 = new ArrayList<>();
            for (ReposicionDetalle r : lista) {
                ReposicionSimple rs = new ReposicionSimple(r);
                lista2.add(rs);
            }

            final String body = new GsonBuilder().setPrettyPrinting().create().toJson(lista2);

            Response.Listener<ResponseReposicion> listenerExito = new Response.Listener<ResponseReposicion>() {
                @Override
                public void onResponse(ResponseReposicion response) {

                    if (response.isExito()) {
                        Toast.makeText(rootView.getContext().getApplicationContext(), "Exito!", Toast.LENGTH_LONG).show();
                    }
                }
            };

            GsonRequest<ResponseReposicion> req = new GsonRequest<ResponseReposicion>(Request.Method.POST, (new UtilJson(rootView.getContext()).prefUrl) + "/insertaReposicion", ResponseReposicion.class, body, listenerExito, rootView.getContext());

            AppController.getInstance().addToRequestQueue(req);
        }

    }
    /*private void insertaReposicion(final Control c) throws JSONException {

        List<ReposicionDetalle> lista = null;
        try {
            lista = reposcionDetalleDao.queryBuilder()
                    .where().eq(ReposicionDetalle.COL_CONTROL, c)
                    .query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (lista != null && !lista.isEmpty()) {

            List<ReposicionSimple> lista2 = new ArrayList<>();
            for (ReposicionDetalle r : lista) {
                ReposicionSimple rs = new ReposicionSimple(r);
                lista2.add(rs);
            }
            ReposicionLista reposiciones = new ReposicionLista(lista2,telefonoId);

            //final String body = new GsonBuilder().setPrettyPrinting().create().toJson(lista2);
            final String body = new GsonBuilder().setPrettyPrinting().create().toJson(reposiciones);

            Response.Listener<ResponseReposicion> listenerExito = new Response.Listener<ResponseReposicion>() {
                @Override
                public void onResponse(ResponseReposicion response) {

                    if (response.isExito()) {
                        Toast.makeText(rootView.getContext(), "Exito!", Toast.LENGTH_LONG).show();
                    }
                }
            };

            GsonRequest<ResponseReposicion> req = new GsonRequest<ResponseReposicion>(Request.Method.POST, (new UtilJson(rootView.getContext()).prefUrl) + "/insertaReposicion", ResponseReposicion.class, body, listenerExito, rootView.getContext());

            int socketTimeout = 50000;//50 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            req.setRetryPolicy(policy);


            AppController.getInstance().addToRequestQueue(req);
        }

    }*/
}
