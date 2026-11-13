package com.example.vuka;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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
 * Driver Activity - Interactive map, safety HUD, and random route navigation.
 * Written with clean, simple Java 8 constructs for students.
 */
public class Driver extends AppCompatActivity {

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

        // Initialize MapLibre before layout inflation
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
        setContentView(R.layout.activity_driver);
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
     * Sets up driver's live location starting point and routes to a random destination.
     */
    private void setupRandomTripRoute(MapLibreMap mapLibreMap) {
        if (mapLibreMap == null) return;

        // Driver Live Location (Johannesburg CBD)
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

        // Add Driver Live Location Start Marker
        mapLibreMap.addMarker(new MarkerOptions()
                .position(startLiveLocation)
                .title("Driver Live Location")
                .snippet("Current position"));

        // Add Random Passenger Destination Marker
        mapLibreMap.addMarker(new MarkerOptions()
                .position(destPlace.location)
                .title("Passenger Destination: " + destPlace.name)
                .snippet("Navigating to " + destPlace.name));

        // Draw Polyline route line between Driver Location and Random Destination
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
    }

    /**
     * Configures interactive safety action buttons and switches on the driver HUD screen.
     */
    private void setupButtonListeners() {
        SwitchCompat switchSaferRoute = findViewById(R.id.switch2);
        View btnReportIncident = findViewById(R.id.button4);
        View btnSos = findViewById(R.id.button5);

        if (switchSaferRoute != null) {
            switchSaferRoute.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String mode = isChecked ? "Safer Route Enabled (+6 min)" : "Fastest Route Enabled";
                Toast.makeText(this, mode, Toast.LENGTH_SHORT).show();
            });
        }

        if (btnReportIncident != null) {
            btnReportIncident.setOnClickListener(v ->
                    Toast.makeText(this, "Incident reported to VUKA safety network!", Toast.LENGTH_SHORT).show()
            );
        }

        if (btnSos != null) {
            btnSos.setOnClickListener(v ->
                    Toast.makeText(this, "🚨 EMERGENCY SOS ACTIVATED! Dispatching response unit.", Toast.LENGTH_LONG).show()
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
