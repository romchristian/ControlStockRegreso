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

import java.util.List;

import movil.palermo.com.py.controlstockregreso.R;
import movil.palermo.com.py.controlstockregreso.modelo.Conductor;
import movil.palermo.com.py.controlstockregreso.modelo.Vendedor;


public class ConductorListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Conductor> conductores;

    public ConductorListAdapter(Activity activity, List<Conductor> conductores) {
        this.activity = activity;
        this.conductores = conductores;
    }

    @Override
    public int getCount() {
        return conductores.size();
    }

    @Override
    public Object getItem(int location) {
        return conductores.get(location);
    }

    @Override
    public long getItemId(int position) {
        return conductores.get(position) == null ? conductores.get(position).getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_conductor, null);

        TextView nombre = (TextView) convertView.findViewById(R.id.nombre);
        TextView ci = (TextView) convertView.findViewById(R.id.ci);

        Conductor c = conductores.get(position);
        nombre.setText(c.getNombre());
        ci.setText("CI: "+c.getCi());

        return convertView;
    }

}
