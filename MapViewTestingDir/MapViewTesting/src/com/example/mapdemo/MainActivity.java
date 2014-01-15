package com.example.mapdemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.example.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {
 
    private GoogleMap googleMap;
    
 
    @Override
    protected void onCreate(Bundle savedInstance) {
    	super.onCreate(savedInstance);
    	setContentView(R.layout.activity_main);
    	
    	initilizeMap();
    	//showEveryPlacesOnTheMap();
    	//Log.d("MAP", "Every places has benn shown on the map.");
    	showOnlyApprovedPlacesOnTheMap();
    	Log.d("MAP", "Only approved places has benn shown on the map.");
    }
    
    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
        	FragmentManager fragmentManager = getSupportFragmentManager();
            SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
                    .findFragmentById(R.id.map);
            googleMap = mapFragment.getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(), "Sorry! Unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }
    }
 
    private void showOnlyApprovedPlacesOnTheMap() {
    	BitmapDescriptor bmd = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        for(Club actualClub : ClubsList.searchViewClubs) {
        	if(actualClub.getApproved()!=0) {
        		googleMap.addMarker(new MarkerOptions()
									.position(actualClub.getLatlng())
									.title(actualClub.getName())
									.snippet(actualClub.getAddress().toString()) //ez nem jó, Geocoder kell
									.icon(bmd));
        	}
        }
    	return;
    }
    
    private void showEveryPlacesOnTheMap() {
    	BitmapDescriptor bmd;
    	for (Club actualClub : ClubsList.searchViewClubs) {
    		if(actualClub.getApproved()==0)
        		bmd = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        	else
        		bmd = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        	
        googleMap.addMarker(new MarkerOptions()
									.position(actualClub.getLatlng())
									.title(actualClub.getName())
									.snippet(actualClub.getAddress().toString()) //ez nem jó, Geocoder kell
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