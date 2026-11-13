package com.example.vuka;

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

/**
 * PassengerTrip Activity - Live interactive trip tracking screen.
 * Written with clean, simple Java 8 constructs for students.
 */
public class PassengerTrip extends AppCompatActivity {

    private MapView mapView;

    private static class DestinationPlace {
        final String name;
        final LatLng location;

        DestinationPlace(String name, double lat, double lng) {
            this.name = name;
            this.location = new LatLng(lat, lng);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize MapLibre before inflating layout
        try {
            String token = getString(R.string.mapbox_access_token);
            MapLibre.getInstance(getApplicationContext(), token, WellKnownTileServer.Mapbox);
        } catch (Exception e) {
            try {
                MapLibre.getInstance(getApplicationContext());
            } catch (Exception ignored) {
            }
        }

        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_passenger_trip);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize MapView & async map setup
        try {
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
        } catch (Exception e) {
            // Ignore map error if fallback rendering is active
        }

        setupButtonListeners();
    }

    /**
     * Sets up live location starting point and routes to a random destination.
     */
    private void setupRandomTripRoute(MapLibreMap mapLibreMap) {
        if (mapLibreMap == null) return;

        // Start / Live Location (Johannesburg CBD)
        LatLng startLiveLocation = new LatLng(-26.2041, 28.0473);

        // Selection of random destination places in GP region
        DestinationPlace[] places = new DestinationPlace[]{
                new DestinationPlace("Sandton City", -26.1076, 28.0567),
                new DestinationPlace("Rosebank Mall", -26.1465, 28.0436),
                new DestinationPlace("Mall of Africa", -25.9983, 28.1086),
                new DestinationPlace("OR Tambo Airport", -26.1367, 28.2411),
                new DestinationPlace("Vilakazi St, Soweto", -26.2366, 27.9079),
                new DestinationPlace("Hatfield, Pretoria", -25.7497, 28.2356)
        };

        // Pick a random destination
        int randomIndex = new Random().nextInt(places.length);
        DestinationPlace destPlace = places[randomIndex];

        // Add Live Location Start Marker
        mapLibreMap.addMarker(new MarkerOptions()
                .position(startLiveLocation)
                .title("Live Location (Start)")
                .snippet("Trip started here"));

        // Add Random Destination Marker
        mapLibreMap.addMarker(new MarkerOptions()
                .position(destPlace.location)
                .title("Destination: " + destPlace.name)
                .snippet("En route to " + destPlace.name));

        // Draw Polyline route between Live Location and Random Destination
        mapLibreMap.addPolyline(new PolylineOptions()
                .add(startLiveLocation, destPlace.location)
                .color(0xFF10B981)
                .width(5f));

        // Frame both locations on the map camera
        try {
            LatLngBounds bounds = new LatLngBounds.Builder()
                    .include(startLiveLocation)
                    .include(destPlace.location)
                    .build();
            mapLibreMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
        } catch (Exception e) {
            CameraPosition position = new CameraPosition.Builder()
                    .target(startLiveLocation)
                    .zoom(12.0)
                    .build();
            mapLibreMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }

        // Update top status bar text with the live trip route
        TextView tvRouteStatus = findViewById(R.id.textView28);
        if (tvRouteStatus != null) {
            tvRouteStatus.setText("🟢 On Route • Live Location → " + destPlace.name);
        }
    }

    /**
     * Configures interactive safety action buttons on the trip screen.
     */
    private void setupButtonListeners() {
        View btnOkay = findViewById(R.id.button11);
        View btnNeedHelp = findViewById(R.id.button12);
        View btnStopSharing = findViewById(R.id.button13);
        View btnSos = findViewById(R.id.button10);
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
                    Toast.makeText(this, "⚠️ Alert sent to emergency contact and security dispatch!", Toast.LENGTH_LONG).show()
            );
        }

        if (btnStopSharing != null) {
            btnStopSharing.setOnClickListener(v ->
                    Toast.makeText(this, "Live trip location sharing paused.", Toast.LENGTH_SHORT).show()
            );
        }

        if (btnSos != null) {
            btnSos.setOnClickListener(v ->
                    Toast.makeText(this, "🚨 EMERGENCY SOS ACTIVATED! Dispatching immediate response.", Toast.LENGTH_LONG).show()
            );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) {
            try {
                mapView.onStart();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            try {
                mapView.onResume();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            try {
                mapView.onPause();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null) {
            try {
                mapView.onStop();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            try {
                mapView.onSaveInstanceState(outState);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            try {
                mapView.onLowMemory();
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            try {
                mapView.onDestroy();
            } catch (Exception ignored) {
            }
        }
    }
}
