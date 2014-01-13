package com.example.mapdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends Activity {
 
    // Google Map
    private GoogleMap googleMap;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        try {
            // Loading map
            initilizeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Club[] klubtomb = { new Club("Elfogadott hely neve", new LatLng(47.527148, 21.602093), 1 ), 
        					new Club("Nem elfogadott hely neve", new LatLng(47.527867, 21.602825), 0) } ;
        showEveryPlacesOnTheMap(klubtomb);
    }
 
    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
 
    private void showOnlyApprovedPlacesOnTheMap(Club[] klubtomb) {
    	BitmapDescriptor bmd = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        for(int i = 0; i < klubtomb.length; ++i) {
        	if(klubtomb[i].getApproved()!=0) {
        		googleMap.addMarker(new MarkerOptions()
									.position(klubtomb[i].getLatlng())
									.title(klubtomb[i].getName())
									.snippet(klubtomb[i].getAddress().toString())
									.icon(bmd));
        	}
        }
    	return;
    }
    
    private void showEveryPlacesOnTheMap(Club[] klubtomb) {
    	BitmapDescriptor bmd;
        for(int i = 0; i < klubtomb.length; ++i) {
        	if(klubtomb[i].getApproved()==0)
        		bmd = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        	else
        		bmd = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        	
        googleMap.addMarker(new MarkerOptions()
									.position(klubtomb[i].getLatlng())
									.title(klubtomb[i].getName())
									.snippet(klubtomb[i].getAddress().toString())
									.icon(bmd));
        }
    	return;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }
}