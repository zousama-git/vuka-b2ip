package com.example.vuka;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import org.maplibre.android.MapLibre;
import org.maplibre.android.WellKnownTileServer;
import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.annotations.PolylineOptions;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.geometry.LatLngBounds;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.Style;

import java.util.Random;

public class PassengerTrip extends AppCompatActivity {

    private MapView mapView;

    private static class Place {
        final String name;
        final LatLng location;

        Place(String name, double lat, double lng) {
            this.name = name;
            this.location = new LatLng(lat, lng);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize MapLibre before inflating layout
        String token = getString(R.string.mapbox_access_token);
        MapLibre.getInstance(getApplicationContext(), token, WellKnownTileServer.Mapbox);

        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_passenger_trip);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize MapView & async map setup
        // code copied from MapBox docs
        mapView = findViewById(R.id.mapView);

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(mapLibreMap -> {
                try {
                    mapLibreMap.setStyle(Style.getPredefinedStyle("Streets"), style -> setupRandomTripRoute(mapLibreMap));
                } catch (Exception e) {
                    mapLibreMap.setStyle(new Style.Builder().fromUri("https://demotiles.maplibre.org/style.json"), style -> setupRandomTripRoute(mapLibreMap));
                }
            });
        }

        setupButtonListeners();
    }

    // Static location for demo
    private void setupRandomTripRoute(MapLibreMap mapLibreMap) {
        if (mapLibreMap == null) return;

        // Selection of random destination places in GP region
        Place[] places = new Place[]{
                new Place("Sandton City", -26.1076, 28.0567),
                new Place("Rosebank Mall", -26.1465, 28.0436),
                new Place("Mall of Africa", -25.9983, 28.1086),
                new Place("OR Tambo Airport", -26.1367, 28.2411),
                new Place("Vilakazi St, Soweto", -26.2366, 27.9079),
                new Place("Hatfield, Pretoria", -25.7497, 28.2356)
        };

        // Pick a random destination
        int startIndex = new Random().nextInt(places.length);
        int destIndex;
        do {
            destIndex = new Random().nextInt(places.length);
        } while (destIndex == startIndex);

        //for route
        Place startPlace = places[startIndex];
        Place destPlace = places[destIndex];

        // Markers
        mapLibreMap.addMarker(new MarkerOptions()
                .position(startPlace.location )
                .title("Live Location (Start)")
                .snippet("Trip started here"));

        mapLibreMap.addMarker(new MarkerOptions()
                .position(destPlace.location)
                .title("Destination: " + destPlace.name)
                .snippet("En route to " + destPlace.name));

        // Draw Polyline route
        mapLibreMap.addPolyline(new PolylineOptions()
                .add(startPlace.location, destPlace.location)
                .width(5f)
                .color(Color.parseColor("#3bb2d0")));

        // Frame both locations on the map camera
        try {
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(startPlace.location)
                    .include(destPlace.location)
                    .build();
            mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
        } catch (Exception e) {
            CameraPosition position = new CameraPosition.Builder()
                    .target(startPlace.location)
                    .zoom(12.0)
                    .build();
            mapLibreMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }

        // top status bar
        TextView tvRouteStatus = findViewById(R.id.textView28);
        tvRouteStatus.setText(startPlace.name + " to " + destPlace.name);
    }

    private void setupButtonListeners() {
        View btnOkay = findViewById(R.id.button11);
        View btnNeedHelp = findViewById(R.id.button12);
        View cardRouteCheck = findViewById(R.id.cardRouteCheck);

        if (btnOkay != null) {
            btnOkay.setOnClickListener(v -> {
                Toast.makeText(this, "Safety check confirmed! You are safe.", Toast.LENGTH_SHORT).show();
                if (cardRouteCheck != null) {
                    cardRouteCheck.setVisibility(View.GONE);
                }
            });
        }

        if (btnNeedHelp != null) {
            btnNeedHelp.setOnClickListener(v ->
                    Toast.makeText(this, "Alert sent to emergency contact and security dispatch!", Toast.LENGTH_LONG).show()
            );
        }
    }
}
