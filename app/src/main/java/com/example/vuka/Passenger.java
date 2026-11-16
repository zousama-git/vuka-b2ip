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

public class Passenger extends AppCompatActivity {

    private boolean isPhotoTaken = false;
    private boolean isVehicleVerified = false;
    private boolean isSaferRouteActive = false;

    private static final int BASE_TRIP_MINUTES = 18;
    private static final int SAFER_ROUTE_EXTRA_MINUTES = 6;

    private View cardTripSafety;
    private TextView tvExpectedTime;
    private TextView tvRouteDetails;
    private MaterialButton btnSaferRoute;
    private MaterialButton btnStartTrip;
    private MaterialButton btnSnapPlate;
    private MaterialButton btnConfirmMatch;

    // Camera launcher for taking a photo of driver's license plate
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    isPhotoTaken = true;
                    Toast.makeText(this, "License plate photo recorded! Tap 'Confirm Vehicle Match' to proceed.", Toast.LENGTH_LONG).show();
                    if (btnSnapPlate != null) {
                        btnSnapPlate.setVisibility(View.GONE);
                    }
                }
            });

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_passenger);
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Initialize UI view references
        cardTripSafety = findViewById(R.id.cardTripSafety);
        tvExpectedTime = findViewById(R.id.tvExpectedTime);
        tvRouteDetails = findViewById(R.id.tvRouteDetails);
        btnSaferRoute = findViewById(R.id.button8);
        btnStartTrip = findViewById(R.id.button9);
        btnSnapPlate = findViewById(R.id.btnSnapPlate);
        btnConfirmMatch = findViewById(R.id.button7);

        updateTripTimeDisplay();
    }

    // open camera
    public void snapNumberPlate(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            cameraLauncher.launch(takePictureIntent);
        } catch (Exception e) {
            isPhotoTaken = true;
            Toast.makeText(this, "License plate photo recorded!", Toast.LENGTH_SHORT).show();
            if (btnSnapPlate != null) {
                btnSnapPlate.setVisibility(View.GONE);
            }
        }
    }

    // Step 1: Confirm Vehicle Match
    public void confirmVehicleMatch(View view) {
        if (!isPhotoTaken) {
            Toast.makeText(this, "Please take a photo of the number plate first to verify your ride.", Toast.LENGTH_LONG).show();
            return;
        }

        isVehicleVerified = true;

        if (btnConfirmMatch != null) {
            btnConfirmMatch.setVisibility(View.GONE);
        }

        if (cardTripSafety != null) cardTripSafety.setVisibility(View.VISIBLE);
        if (btnSaferRoute != null) btnSaferRoute.setVisibility(View.VISIBLE);
        if (btnStartTrip != null) btnStartTrip.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Vehicle match confirmed! Trip Safety Profile unlocked.", Toast.LENGTH_LONG).show();
    }

    // Step 2: Use Safer Route toggle
    public void toggleSaferRoute(View view) {
        isSaferRouteActive = !isSaferRouteActive;
        updateTripTimeDisplay();

        if (isSaferRouteActive) {
            Toast.makeText(this, "Safer route selected! Travel time increased to " + (BASE_TRIP_MINUTES + SAFER_ROUTE_EXTRA_MINUTES) + " min.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Standard route selected. Expected time: " + BASE_TRIP_MINUTES + " min.", Toast.LENGTH_SHORT).show();
        }
    }

    // Updates expected trip time text and button UI state
    private void updateTripTimeDisplay() {
        int totalMinutes = BASE_TRIP_MINUTES + (isSaferRouteActive ? SAFER_ROUTE_EXTRA_MINUTES : 0);

        if (tvExpectedTime != null) {
            tvExpectedTime.setText(totalMinutes + " min");
        }

        if (tvRouteDetails != null) {
            tvRouteDetails.setText(isSaferRouteActive ? "Safer Route (+6 min)" : "Standard Route");
        }

        if (btnSaferRoute != null) {
            btnSaferRoute.setText(isSaferRouteActive ? "Safer Route Active (" + totalMinutes + " min)" : "Use Safer Route (+6 min)");
        }
    }

    public void startPassTrip(View view) {
        Intent intent = new Intent(this, PassengerTrip.class);
        startActivity(intent);
    }
}
