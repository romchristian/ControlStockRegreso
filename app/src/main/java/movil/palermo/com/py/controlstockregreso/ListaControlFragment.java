package movil.palermo.com.py.controlstockregreso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.controlstockregreso.custom.ControlListAdapter;
import movil.palermo.com.py.controlstockregreso.modelo.Control;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;


public class ListaControlFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView listaControl;
    Intent datos;
    private List<Control> controlList = new ArrayList<Control>();
    private ControlListAdapter adapter;
    private DatabaseHelper databaseHelper;
    private RuntimeExceptionDao<Control, Integer> controlDao;
    private ProgressDialog pDialog;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_lista_control, container, false);
        listaControl = (ListView) rootView.findViewById(R.id.listaControl);

        adapter = new ControlListAdapter(getActivity(), controlList);

        listaControl.setAdapter(adapter);
        listaControl.setOnItemClickListener(this);

        databaseHelper = new DatabaseHelper(getActivity());
        controlDao = databaseHelper.getControlDao();
        recargaLista();

        return rootView;
    }


    private void recargaLista() {
        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        controlList.clear();
        controlList.addAll(controlDao.queryForAll());
        adapter.notifyDataSetChanged();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

  @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
