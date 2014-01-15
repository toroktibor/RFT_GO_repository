package com.example.mapdemo;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class ClubsList {
	{
		searchViewClubs.add(new Club("Le'Programoz-Lak", new LatLng(47.527148, 21.602093), 1 ));
		searchViewClubs.add(new Club("Egy közeli kocsma :)", new LatLng(47.527867, 21.602825), 0));
		searchViewClubs.add(new Club("Schönherz Iskolaszövetkezet Iroda", new LatLng(47.528452, 21.622980), 1));
		searchViewClubs.add(new Club("Álomház ingatlan", new LatLng(47.527982, 21.621671), 1));
	}
	
	public static List<Club> searchViewClubs = new ArrayList<Club>();
	
	
}
