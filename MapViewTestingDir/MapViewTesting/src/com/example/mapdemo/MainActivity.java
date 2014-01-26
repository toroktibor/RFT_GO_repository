package com.example.mapdemo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {
 
    private GoogleMap googleMap;
    private Session Session;
    private LatLng myPosition = new LatLng(47.52888,21.625468);
 
    @Override
    protected void onCreate(Bundle savedInstance) {
    	super.onCreate(savedInstance);
    	setContentView(R.layout.activity_main);
    	Log.d("MAPVIEWDEMO", "BEFORE INITIALIZATION");
    	initilizeMap();
    	Log.d("MAPVIEWDEMO", "AFTER INITIALIZATION");
    	
    	if(googleMap != null) {
    		Log.d("MAPVIEWDEMO", "MAP CATCHED");
    		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    		Log.d("MAPVIEWDEMO", "MAP TYPE SETTED");
    		googleMap.setMyLocationEnabled(true);
    		Log.d("MAPVIEWDEMO", "MY LOCATION SETTED");
    		CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(myPosition).zoom(15).build();
    		Log.d("MAPVIEWDEMO", "CAMERA MOVED");
    		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    		if(Session.getInstance().getActualUser().type == 0) {
    			showOnlyApprovedPlacesOnTheMap();
    			Log.e("MAP", "ONLY APPROVED PLACES HAS BEEN SHOWN ON THE MAP.");
    		}
    		else if(Session.getInstance().getActualUser().type == 1) {
    			showEveryPlacesOnTheMap();
    			Log.e("MAP", "EVERY PLACES HAS BEEN SHOWN ON THE MAP");
    		}	
    	}
    }
    
    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
        	Log.d("MAPVIEWDEMO", "INITIALIZATION IN PROGRESS");
            googleMap = ((SupportMapFragment) getSupportFragmentManager()
            		.findFragmentById(R.id.map)).getMap() ;
            Log.d("MAPVIEWDEMO", "INITIALIZATION DONE");
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
    	if(googleMap != null) {
    		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    		if(Session.getInstance().getActualUser().type == 0) {
    			showOnlyApprovedPlacesOnTheMap();
    			Log.e("MAP", "ONLY APPROVED PLACES HAS BEEN SHOWN ON THE MAP.");
    		}
    		else if(Session.getInstance().getActualUser().type == 1) {
    			showEveryPlacesOnTheMap();
    			Log.e("MAP", "EVERY PLACES HAS BEEN SHOWN ON THE MAP");
    		}
    	}
    }
}