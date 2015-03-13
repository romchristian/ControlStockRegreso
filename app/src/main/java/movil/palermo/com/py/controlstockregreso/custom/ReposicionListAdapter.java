package movil.palermo.com.py.controlstockregreso.custom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

import movil.palermo.com.py.controlstockregreso.AppController;
import movil.palermo.com.py.controlstockregreso.R;
import movil.palermo.com.py.controlstockregreso.modelo.DatabaseHelper;
import movil.palermo.com.py.controlstockregreso.modelo.ProductoReposicion;
import movil.palermo.com.py.controlstockregreso.modelo.ProductoUM;

/**
 * Created by jcolman on 12/02/2015.
 */
public class ReposicionListAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ProductoReposicion> fullProductosRepo;
    private List<ProductoReposicion> filteredProductosRepo;
    private ItemFilter mFilter = new ItemFilter();
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ReposicionListAdapter(Activity activity, List<ProductoReposicion> productosRepo) {
        this.activity = activity;
        this.fullProductosRepo = productosRepo;
        this.filteredProductosRepo = productosRepo;
    }

    @Override
    public int getCount() {
        return filteredProductosRepo.size();
    }

    @Override
    public Object getItem(int location) {
        return filteredProductosRepo.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row_reposicion_producto, parent,false);

        /*if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);*/

        ImageView thumbNail = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView nombre = (TextView) convertView.findViewById(R.id.nombre);
        TextView tituloGruesas=(TextView) convertView.findViewById(R.id.tituloRegGruesas);
        TextView cantGruesas = (TextView) convertView.findViewById(R.id.cantRegGruesas);
        TextView tituloUnidades=(TextView) convertView.findViewById(R.id.tituloRegUnidades);
        TextView cantCajetillas = (TextView) convertView.findViewById(R.id.cantRegCajetillas);
        TextView tituloRepoGruesas=(TextView) convertView.findViewById(R.id.tituloRepoGruesas);
        TextView cantRepoGruesas = (TextView) convertView.findViewById(R.id.cantRepoGruesas);

        // getting movie data for the row
        ProductoReposicion p = filteredProductosRepo.get(position);

        // thumbnail image
        /*thumbNail.setImageUrl(m.getImg(), imageLoader);*/

        int img = 0;
        if (p.getNombre().toLowerCase().contains("blue")) {
            img = R.drawable.palermo_blue;
        } else if (p.getNombre().toLowerCase().contains("green")) {
            img = R.drawable.palermo_green;
        }else if (p.getNombre().toLowerCase().contains("red")) {
            img = R.drawable.palermo_red;
        }else if (p.getNombre().toLowerCase().contains("plm 3")) {
            img = R.drawable.plm3;
        }else if (p.getNombre().toLowerCase().contains("duo")) {
            img = R.drawable.palermo_duo;
        }else if (p.getId() == 218) {
            img = R.drawable.kentucky_10;
        }else if (p.getId() == 403) {
            img = R.drawable.sanmarino_20;
        }else if (p.getId() == 404) {
            img = R.drawable.sanmarino_10;
        }else if (p.getId() == 198) {
            img = R.drawable.kentucky_20;
        }else if (p.getId() == 204) {
            img = R.drawable.kentucky_soft;
        }else{
            img = R.drawable.kit;
        }

        thumbNail.setImageResource(img);
        nombre.setText(p.getNombre());


        if(p.getKit()>0){

            tituloUnidades.setVisibility(TextView.GONE);
            cantCajetillas.setVisibility(TextView.GONE);
            tituloGruesas.setText("Unidades");
            cantGruesas.setText("" + p.getCantUnidad());
            tituloRepoGruesas.setText("Unidades");
            cantRepoGruesas.setText("" + p.getCantReposicionUnidad());
        }else{
            tituloGruesas.setVisibility(TextView.VISIBLE);
            cantGruesas.setVisibility(TextView.VISIBLE);
            tituloUnidades.setVisibility(TextView.VISIBLE);
            cantCajetillas.setVisibility(TextView.VISIBLE);
            tituloGruesas.setText("Gruesas");
            cantGruesas.setText("" + (p.getGruesasPorCaja() + p.getCantGruesas()));
            cantCajetillas.setText("" + (p.getCantCajetillas() + p.getCajetillasPorCaja()+p.getCajetillasPorGruesa()));
            tituloRepoGruesas.setText("Gruesas");
            cantRepoGruesas.setText("" + p.getCantReposicionGruesas());
        }

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

            final List<ProductoReposicion> list = fullProductosRepo;

            int count = list.size();
            final ArrayList<ProductoReposicion> nlist = new ArrayList<ProductoReposicion>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                ProductoReposicion p = list.get(i);
                filterableString = p.getNombre();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(p);
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
                filteredProductosRepo = (ArrayList<ProductoReposicion>) results.values;
                notifyDataSetChanged();
            }
        }


    }

}