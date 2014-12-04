package movil.palermo.com.py.controlstockregreso;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import movil.palermo.com.py.controlstockregreso.custom.SlidingUpPaneLayout;


public class PruebaActivity extends ActionBarActivity {

    RelativeLayout bottom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);

        final float density = getResources().getDisplayMetrics().density;

        SlidingUpPaneLayout slidingUpPaneLayout = (SlidingUpPaneLayout) findViewById(R.id.sliding_up_layout);
        slidingUpPaneLayout.setParallaxDistance((int) (200 * density));
        slidingUpPaneLayout.setShadowResourceTop(R.drawable.shadow_top);

        bottom = (RelativeLayout) findViewById(R.id.bottom_view);
        slidingUpPaneLayout.openPane(bottom,0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prueba, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
