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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.controlstockregreso.R;
import movil.palermo.com.py.controlstockregreso.modelo.Conductor;
import movil.palermo.com.py.controlstockregreso.modelo.Vendedor;


public class ConductorListAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Conductor> fullConductores;
    private List<Conductor> filteredConductores = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public ConductorListAdapter(Activity activity, List<Conductor> conductores) {
        this.activity = activity;
        this.fullConductores = conductores;
        this.filteredConductores= conductores;
    }

    @Override
    public int getCount() {
        return filteredConductores.size();
    }

    @Override
    public Object getItem(int location) {
        return filteredConductores.get(location);
    }

    @Override
    public long getItemId(int position) {
        return fullConductores.get(position) == null ? filteredConductores.get(position).getId() : position;
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

        Conductor c = filteredConductores.get(position);
        nombre.setText(c.getNombre());
        ci.setText("CI: "+c.getCi());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }


    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Conductor> list = fullConductores;

            int count = list.size();
            final ArrayList<Conductor> nlist = new ArrayList<Conductor>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                Conductor c = list.get(i);
                filterableString = c.getNombre();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(c);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, android.widget.Filter.FilterResults results) {
            if(results!=null) {
                filteredConductores = (ArrayList<Conductor>) results.values;
                notifyDataSetChanged();
            }
        }


    }
}
