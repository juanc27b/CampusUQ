package co.edu.uniquindio.campusuq.maps;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import co.edu.uniquindio.campusuq.R;
import co.edu.uniquindio.campusuq.activity.MainActivity;

public class MapsActivity extends MainActivity implements OnMapReadyCallback,
        OnStreetViewPanoramaReadyCallback {

    private LinearLayout mapLayout;
    private LinearLayout streetViewLayout;
    private Button changeButton;
    private boolean isMapEnabled;

    private GoogleMap mMap;
    private StreetViewPanorama mPanorama;
    private LatLng UNIVERSIDAD = new LatLng(4.55435, -75.6601);

    public ArrayList<String> tags;
    public ArrayList<LatLng> locations;

    public MapsActivity() {
        super.setHasNavigationDrawerIcon(false);

        isMapEnabled = true;
        tags = new ArrayList<>();
        locations = new ArrayList<>();
    }

    @Override
    public void addContent(Bundle savedInstanceState) {
        super.addContent(savedInstanceState);
        super.setBackground(R.drawable.portrait_normal_background,
                R.drawable.landscape_normal_background);

        ViewStub stub = findViewById(R.id.layout_stub);
        stub.setLayoutResource(R.layout.content_maps);
        stub.inflate();

        addTagsAndLocations();

        mapLayout = findViewById(R.id.map_layout);
        streetViewLayout = findViewById(R.id.streetview_layout);
        changeButton = findViewById(R.id.change_button) ;
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMapEnabled) {
                    changeButton.setText(R.string.change_to_mapview);
                    mapLayout.setVisibility(View.GONE);
                    streetViewLayout.setVisibility(View.VISIBLE);
                    isMapEnabled = false;
                } else {
                    changeButton.setText(R.string.change_to_streetview);
                    streetViewLayout.setVisibility(View.GONE);
                    mapLayout.setVisibility(View.VISIBLE);
                    isMapEnabled = true;
                }
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Sets the map type to be "hybrid"
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //mMap.setBuildingsEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mMap.setMyLocationEnabled(true);
            //return;
        }

        // Add a marker in UQ and move the camera
        //mMap.setMaxZoomPreference();
        //mMap.addMarker(new MarkerOptions().position(UNIVERSIDAD).title("Marker in UQ").alpha(0.0f));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UNIVERSIDAD, 16));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UNIVERSIDAD.getCenter(), 18));
        //mMap.setLatLngBoundsForCameraTarget(BOUNDS);

        for (int i = 0; i < 41; i++) {
            mMap.addMarker(new MarkerOptions().position(locations.get(i)).title(tags.get(i)).alpha(0.0f));
        }

        GroundOverlayOptions universityMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.map))
                .position(UNIVERSIDAD, 500f, 500f);//.transparency(0.5f).bearing(357);
        GroundOverlay imageOverlay = mMap.addGroundOverlay(universityMap);

        /*
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i("Map clicked", latLng.latitude + ", " + latLng.longitude);
            }
        });
        */
    }

    @Override
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            boolean found = false;
            for (int i = 0; i < 41; i++) {
                if (query.trim().toLowerCase().equals(tags.get(i).toLowerCase()) ||
                        tags.get(i).toLowerCase().contains(query.trim().toLowerCase())) {
                    if (isMapEnabled) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations.get(i), 16));
                    } else {
                        mPanorama.setPosition(locations.get(i));
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                Toast.makeText(this, getString(R.string.location_no_found)+query,
                        Toast.LENGTH_SHORT).show();
            }
        } else if (isMapEnabled && mMap != null)  {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UNIVERSIDAD, 16));
        } else if (!isMapEnabled && mPanorama != null) {
            mPanorama.setPosition(UNIVERSIDAD);
        }
    }

    public void addTagsAndLocations() {

        tags.add(getString(R.string.marker_a1));
        tags.add(getString(R.string.marker_a2));
        tags.add(getString(R.string.marker_b1));
        tags.add(getString(R.string.marker_b2));
        tags.add(getString(R.string.marker_b3));
        tags.add(getString(R.string.marker_b4));
        tags.add(getString(R.string.marker_b5));
        tags.add(getString(R.string.marker_b6));
        tags.add(getString(R.string.marker_b7));
        tags.add(getString(R.string.marker_b8));
        tags.add(getString(R.string.marker_b9));
        tags.add(getString(R.string.marker_b10));
        tags.add(getString(R.string.marker_b11));
        tags.add(getString(R.string.marker_b12));
        tags.add(getString(R.string.marker_b13));
        tags.add(getString(R.string.marker_c1));
        tags.add(getString(R.string.marker_c2));
        tags.add(getString(R.string.marker_c3));
        tags.add(getString(R.string.marker_c4));
        tags.add(getString(R.string.marker_c5));
        tags.add(getString(R.string.marker_c6));
        tags.add(getString(R.string.marker_c7));
        tags.add(getString(R.string.marker_c8));
        tags.add(getString(R.string.marker_c9));
        tags.add(getString(R.string.marker_c10));
        tags.add(getString(R.string.marker_d1));
        tags.add(getString(R.string.marker_d2));
        tags.add(getString(R.string.marker_d3));
        tags.add(getString(R.string.marker_d4));
        tags.add(getString(R.string.marker_e1));
        tags.add(getString(R.string.marker_e2));
        tags.add(getString(R.string.marker_e3));
        tags.add(getString(R.string.marker_e4));
        tags.add(getString(R.string.marker_f1));
        tags.add(getString(R.string.marker_f2));
        tags.add(getString(R.string.marker_f3));
        tags.add(getString(R.string.marker_f4));
        tags.add(getString(R.string.marker_f5));
        tags.add(getString(R.string.marker_f6));
        tags.add(getString(R.string.marker_f7));
        tags.add(getString(R.string.marker_f8));

        locations.add(new LatLng(4.552763010094806, -75.65889827907085));
        locations.add(new LatLng(4.553277371745918, -75.65939817577599));
        locations.add(new LatLng(4.552598574713575, -75.65970327705145));
        locations.add(new LatLng(4.552191496717895, -75.66017668694256));
        locations.add(new LatLng(4.552252992915925, -75.66051498055458));
        locations.add(new LatLng(4.552539418073339, -75.66095855087042));
        locations.add(new LatLng(4.553157721700057, -75.65987393260002));
        locations.add(new LatLng(4.553457180995293, -75.66018171608448));
        locations.add(new LatLng(4.553623955705001, -75.6604727357626));
        locations.add(new LatLng(4.553624289922827, -75.6607087701559));
        locations.add(new LatLng(4.553632645368176, -75.66093441098928));
        locations.add(new LatLng(4.552996962808212, -75.66038791090249));
        locations.add(new LatLng(4.5531209577154765, -75.66162910312416));
        locations.add(new LatLng(4.552761004785512, -75.66139206290245));
        locations.add(new LatLng(4.552824840462178, -75.66194258630276));
        locations.add(new LatLng(4.553289069375022, -75.65884396433832));
        locations.add(new LatLng(4.5535407354639075, -75.65842151641847));
        locations.add(new LatLng(4.553573154595125, -75.65902601927519));
        locations.add(new LatLng(4.553939457254517, -75.6590561941266));
        locations.add(new LatLng(4.554188783592994, -75.65877724438906));
        locations.add(new LatLng(4.5544107040146, -75.658745393157));
        locations.add(new LatLng(4.554696794095656, -75.65956246107817));
        locations.add(new LatLng(4.555251260412376, -75.659036077559));
        locations.add(new LatLng(4.555605864625874, -75.65918862819672));
        locations.add(new LatLng(4.555802049916758, -75.658676661551));
        locations.add(new LatLng(4.555863880022877, -75.6589750573039));
        locations.add(new LatLng(4.556114542560849, -75.65894488245249));
        locations.add(new LatLng(4.556438064213983, -75.66045932471752));
        locations.add(new LatLng(4.55585886677123, -75.66016495227814));
        locations.add(new LatLng(4.555866219540305, -75.65923154354095));
        locations.add(new LatLng(4.556242547529852, -75.65968986600637));
        locations.add(new LatLng(4.555706129687539, -75.65984543412924));
        locations.add(new LatLng(4.55580271835036, -75.65943505614996));
        locations.add(new LatLng(4.555077467520033, -75.66093608736992));
        locations.add(new LatLng(4.55438496927043, -75.66093575209379));
        locations.add(new LatLng(4.55442006210316, -75.66132836043833));
        locations.add(new LatLng(4.554396332664075, -75.6617558375001));
        locations.add(new LatLng(4.5539257543299225, -75.66178634762764));
        locations.add(new LatLng(4.554549070025962, -75.66203378140926));
        locations.add(new LatLng(4.554054428123737, -75.66225606948137));
        locations.add(new LatLng(4.553856237049898, -75.66203948110342));

    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        mPanorama = streetViewPanorama;

        mPanorama.setPanningGesturesEnabled(true);
        mPanorama.setUserNavigationEnabled(true);
        mPanorama.setZoomGesturesEnabled(true);
        mPanorama.setStreetNamesEnabled(true);

        mPanorama.setPosition(UNIVERSIDAD);
    }

}
