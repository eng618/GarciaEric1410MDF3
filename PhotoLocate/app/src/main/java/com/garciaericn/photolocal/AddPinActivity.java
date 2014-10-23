package com.garciaericn.photolocal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.garciaericn.photolocal.data.Pin;
import com.garciaericn.photolocal.fragments.AddPinFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Full Sail University
 * Mobile Development BS
 * Created by ENG618-Mac on 10/22/14.
 */
public class AddPinActivity extends Activity implements AddPinFragment.OnFragmentInteractionListener{

    public AddPinActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pin);

        if (savedInstanceState == null) {
            Intent passedIntent = getIntent();
            Bundle b = passedIntent.getExtras();
            if (b != null && b.containsKey(Pin.LAT_LNG)) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.new_pin_container, AddPinFragment.getInstance((LatLng) b.get(Pin.LAT_LNG)), AddPinFragment.TAG)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent cancelIntent = new Intent();
                setResult(RESULT_CANCELED, cancelIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setHomeAsUp() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}