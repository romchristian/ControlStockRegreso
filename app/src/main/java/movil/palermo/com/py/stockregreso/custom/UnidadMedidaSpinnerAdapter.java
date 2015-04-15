package movil.palermo.com.py.stockregreso.custom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import movil.palermo.com.py.stockregreso.R;
import movil.palermo.com.py.stockregreso.modelo.UnidadMedida;

/**
 * Created by cromero on 04/12/2014.
 */
public class UnidadMedidaSpinnerAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<UnidadMedida> unidades;

    public UnidadMedidaSpinnerAdapter(Activity activity, List<UnidadMedida> unidades) {
        this.activity = activity;
        this.unidades = unidades;
    }


    @Override
    public int getCount() {
        return unidades.size();
    }

    @Override
    public Object getItem(int i) {
        return unidades.get(i);
    }

    @Override
    public long getItemId(int position) {
        return unidades.get(position) == null ? unidades.get(position).getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.spinner_row_unidad_medida, null);

        TextView nombre = (TextView) convertView.findViewById(R.id.nombre);

        UnidadMedida u = unidades.get(position);
        nombre.setText(u.getNombre());
        return convertView;
    }
}
