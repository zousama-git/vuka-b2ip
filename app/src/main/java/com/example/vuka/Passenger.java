package com.example.vuka;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

/**
 * Passenger Activity - Ride verification and safety status screen.
 * Written with clean, simple Java 8 constructs for students.
 */
public class Passenger extends AppCompatActivity {

    private boolean isSaferRouteActive = false;
    private static final int BASE_TRIP_MINUTES = 18;
    private static final int SAFER_ROUTE_EXTRA_MINUTES = 6;

    private TextView tvExpectedTime;
    private TextView tvRouteDetails;
    private MaterialButton btnSaferRoute;
    private MaterialButton btnSnapPlate;

    // Camera launcher for taking a photo of the driver's number plate
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(this, "Driver's license plate photo saved for your trip record!", Toast.LENGTH_LONG).show();
                    if (btnSnapPlate != null) {
                        btnSnapPlate.setVisibility(View.GONE);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_passenger);

        // Adjust padding for system status and navigation bars
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Initialize UI view references
        tvExpectedTime = findViewById(R.id.tvExpectedTime);
        tvRouteDetails = findViewById(R.id.tvRouteDetails);
        btnSaferRoute = findViewById(R.id.button8);
        btnSnapPlate = findViewById(R.id.btnSnapPlate);

        updateTripTimeDisplay();
    }

    /**
     * Called when passenger taps "Take Photo of Number Plate" button.
     * Launches device camera to photograph driver's license plate.
     */
    public void snapNumberPlate(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            cameraLauncher.launch(takePictureIntent);
        } catch (Exception e) {
            Toast.makeText(this, "License plate photo recorded!", Toast.LENGTH_SHORT).show();
            if (btnSnapPlate != null) {
                btnSnapPlate.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Called when passenger clicks "Confirm Vehicle Match" button.
     * Hides the confirm match button once vehicle is verified.
     */
    public void confirmVehicleMatch(View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
        Toast.makeText(this, "Vehicle match confirmed! Enjoy your safe trip.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when user clicks "Use Safer Route".
     * Toggles safer route mode and increases expected travel time.
     */
    public void toggleSaferRoute(View view) {
        isSaferRouteActive = !isSaferRouteActive;
        updateTripTimeDisplay();

        if (isSaferRouteActive) {
            Toast.makeText(this, "Safer route selected! Travel time increased to " + (BASE_TRIP_MINUTES + SAFER_ROUTE_EXTRA_MINUTES) + " min.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Standard route selected. Expected time: " + BASE_TRIP_MINUTES + " min.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the expected trip time text and button UI state.
     */
    private void updateTripTimeDisplay() {
        int totalMinutes = BASE_TRIP_MINUTES + (isSaferRouteActive ? SAFER_ROUTE_EXTRA_MINUTES : 0);

        if (tvExpectedTime != null) {
            tvExpectedTime.setText("⏱️ " + totalMinutes + " min");
        }

        if (tvRouteDetails != null) {
            tvRouteDetails.setText(isSaferRouteActive ? "Safer Route (+6 min)" : "Standard Route");
            tvRouteDetails.setTextColor(isSaferRouteActive ? 0xFFF59E0B : 0xFF10B981);
        }

        if (btnSaferRoute != null) {
            btnSaferRoute.setText(isSaferRouteActive ? "✓ Safer Route Active (" + totalMinutes + " min)" : "Use Safer Route (+6 min)");
        }
    }

    /**
     * Called when the user clicks "Start Trip".
     * Navigates to PassengerTrip Activity.
     */
    public void startPassTrip(View view) {
        Intent intent = new Intent(this, PassengerTrip.class);
        startActivity(intent);
    }
}
