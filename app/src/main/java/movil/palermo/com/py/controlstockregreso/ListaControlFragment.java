package movil.palermo.com.py.controlstockregreso;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.controlstockregreso.custom.ControlListAdapter;
import movil.palermo.com.py.controlstockregreso.modelo.Control;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;


public class ListaControlFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemLongClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView listaControl;
    Intent datos;
    private List<Control> controlList = new ArrayList<Control>();
    private ControlListAdapter adapter;
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Control, Integer> controlDao;
    private ProgressDialog pDialog;
    private TextView txtVwNuevoControl;
    private ImageView imgVwflecha;


    View rootView;

    public static ListaControlFragment newInstance(int sectionNumber) {
        ListaControlFragment fragment = new ListaControlFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ListaControlFragment() {

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
        listaControl = (ListView) rootView.findViewById(R.id.listaControl);
        txtVwNuevoControl = (TextView) rootView.findViewById(R.id.txtVwNuevoControl);
        imgVwflecha = (ImageView) rootView.findViewById(R.id.imgFlecha);

        adapter = new ControlListAdapter(getActivity(), controlList);
        listaControl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {

                Log.d("OnClick","Hola 1");
                Control control = (Control)  adapterView.getItemAtPosition(pos);
                Log.d("OnClick2","Hola 2");
                Intent i = new Intent(rootView.getContext(), ListaProductos.class);
                Log.d("OnClick3","Hola 3");
                i.putExtra("CONTROL",control);
                Log.d("OnClick4","Hola 4");
                startActivity(i);
                Log.d("OnClick5","Hola 5");
            }
        });
        listaControl.setOnItemLongClickListener(this);

        listaControl.setAdapter(adapter);


        databaseHelper = new DatabaseHelper(getActivity());
        controlDao = databaseHelper.getControlDao();
        recargaLista();

        return rootView;
    }



    public void recargaLista() {
        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        controlList.clear();
        controlList.addAll(controlDao.queryForAll());
        adapter.notifyDataSetChanged();
        if (adapter.getCount()>0) {
            listaControl.setVisibility(View.VISIBLE);
            txtVwNuevoControl.setVisibility(View.GONE);
            imgVwflecha.setVisibility(View.GONE);
        }
        else {
            listaControl.setVisibility(View.GONE);
            txtVwNuevoControl.setVisibility(View.VISIBLE);
            imgVwflecha.setVisibility(View.VISIBLE);
        }
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
    public boolean onItemLongClick(final AdapterView<?> parent, View view, int position, long id) {

        //controlDetalleDao.deleteById(Long.valueOf(id).intValue());
        final int  pos = position;
        AlertDialog dialog = new AlertDialog.Builder(rootView.getContext()).create();
        dialog.setTitle("Advertencia!");
        dialog.setMessage("Desea editar este control?");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(rootView.getContext(),MainCrearControlActivity.class);
                i.putExtra("CONTROL", (Control) parent.getItemAtPosition(pos));
                startActivity(i);
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
        return true;

    }
}
