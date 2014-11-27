package movil.palermo.com.py.controlstockregreso.custom;

/**
 * Created by cromero on 14/11/2014.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import movil.palermo.com.py.controlstockregreso.AppController;
import movil.palermo.com.py.controlstockregreso.R;
import movil.palermo.com.py.controlstockregreso.modelo.Producto;
import movil.palermo.com.py.controlstockregreso.modelo.Vendedor;


public class VendedorListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Vendedor> vendedores;

    public VendedorListAdapter(Activity activity, List<Vendedor> vendedores) {
        this.activity = activity;
        this.vendedores = vendedores;
    }

    @Override
    public int getCount() {
        return vendedores.size();
    }

    @Override
    public Object getItem(int location) {
        return vendedores.get(location);
    }

    @Override
    public long getItemId(int position) {
        return vendedores.get(position) == null ? vendedores.get(position).getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_vendedor, null);

        TextView nombre = (TextView) convertView.findViewById(R.id.nombre);
        TextView conductor = (TextView) convertView.findViewById(R.id.conductor);

        Vendedor v = vendedores.get(position);
        nombre.setText(v.getNombre());
        conductor.setText(v.getConductor() == null ? "Conductor: " + "No asignado" : "Conductor: " + v.getConductor().getNombre());

        return convertView;
    }

}
