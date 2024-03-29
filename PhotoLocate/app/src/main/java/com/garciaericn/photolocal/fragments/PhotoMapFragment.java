package com.garciaericn.photolocal.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.garciaericn.photolocal.AddPinActivity;
import com.garciaericn.photolocal.PinActivity;
import com.garciaericn.photolocal.R;
import com.garciaericn.photolocal.data.DataManager;
import com.garciaericn.photolocal.data.MarkerAdapter;
import com.garciaericn.photolocal.data.Pin;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Full Sail University
 * Mobile Development BS
 * Created by ENG618-Mac on 10/21/14.
 */
public class PhotoMapFragment extends MapFragment
        implements
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMapLongClickListener{

    public static final String TAG = "com.garciaericn.photolocal.fragments.PhotoMapFragment.TAG";
    public static final int NEW_PIN = 1234;
    private static final String ARG_PINS_ARRAY = "com.garciaericn.photolocal.fragments.ARG_PINS_ARRAY";
    private GoogleMap googleMap;
    private ArrayList<Pin> pins;
    private OnFragmentInteractionListener mListener;

    public PhotoMapFragment() {
        if (pins == null) {
            pins = new ArrayList<Pin>();
        }
    }

    public static PhotoMapFragment getInstance(ArrayList<Pin> pins) {
        PhotoMapFragment fragment = new PhotoMapFragment();
        Bundle b = new Bundle();
        b.putSerializable(ARG_PINS_ARRAY, pins);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_PINS_ARRAY)) {
            pins = (ArrayList<Pin>) getArguments().getSerializable(ARG_PINS_ARRAY);
        }

        if (savedInstanceState == null) {
            // Get instance of map
            googleMap = getMap();

            if (googleMap != null) {
                if (pins != null) {
                    // Add a map marker
                    for (Pin pin: pins) {
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(pin.getLatitude(), pin.getLongitude())).title(pin.getTitle()).snippet(pin.getImageUriString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                    }
                }

                // Set marker adapter
                googleMap.setInfoWindowAdapter(new MarkerAdapter(getActivity()));
                // Set listeners
                googleMap.setOnInfoWindowClickListener(this);
                googleMap.setOnMapClickListener(this);
                googleMap.setOnMapLongClickListener(this);


                // Set target, zoom, and animation
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.590647, -81.304510), 10));
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Ensure activity implements OnFragmentInteractionListener
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                Intent intent = new Intent(getActivity(), AddPinActivity.class);

                startActivityForResult(intent, NEW_PIN);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity(), marker.getTitle() + " pressed", Toast.LENGTH_SHORT).show();
        Pin pin = null;
        // TODO: launch detail frag
        for (Pin checkPin : pins) {
            if (marker.getTitle().equals(checkPin.getTitle())) {
                pin = checkPin;
            }
        }

        if (pin != null) {
            Intent detailIntent = new Intent(getActivity(), PinActivity.class);
            detailIntent.putExtra(Pin.PIN, pin);
            startActivity(detailIntent);
        }
    }

    @Override
    public void onMapClick(final LatLng latLng) {
        Toast.makeText(getActivity(), "Press at: " + latLng, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
        Intent intent = new Intent(getActivity(), AddPinActivity.class);
        intent.putExtra(Pin.LAT_LNG, latLng);

        startActivityForResult(intent, NEW_PIN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if (requestCode == NEW_PIN && resultCode == Activity.RESULT_OK) {
            Bundle b = data.getExtras();
            if (b.containsKey(Pin.PIN)) {
                Pin newpin = (Pin) b.getSerializable(Pin.PIN);
                pins.add(newpin);
                googleMap.addMarker(new MarkerOptions().position(new LatLng(newpin.getLatitude(), newpin.getLongitude())).title(newpin.getTitle()).snippet(newpin.getImageUriString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                DataManager mgr = DataManager.getInstance(getActivity());
                if (mgr != null) {
                    mgr.writeToDisk(pins);
                }
            }
        } else {
            Toast.makeText(getActivity(), "No new pin was added", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * PhotoMapFragment
     * Interface
     * */
    public interface OnFragmentInteractionListener {
    }
}
