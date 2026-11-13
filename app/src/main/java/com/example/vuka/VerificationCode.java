package com.example.vuka;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * VerificationCode Activity - Handles 6-digit OTP UI, auto-focus navigation, and user routing.
 * Written with clean, simple Java 8 constructs for students.
 */
public class VerificationCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_verification_code);

        // Adjust padding for system bars
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Attach auto-advance listeners to the OTP digit fields
        setupOtpInputs();
    }

    /**
     * Sets up text change and key listeners on the 6 digit boxes to auto-advance focus.
     */
    private void setupOtpInputs() {
        EditText et2 = findViewById(R.id.editTextNumber2);
        EditText et3 = findViewById(R.id.editTextNumber3);
        EditText et4 = findViewById(R.id.editTextNumber4);
        EditText et5 = findViewById(R.id.editTextNumber5);
        EditText et6 = findViewById(R.id.editTextNumber6);
        EditText et7 = findViewById(R.id.editTextNumber7);

        EditText[] otpBoxes = new EditText[]{et2, et3, et4, et5, et6, et7};

        for (int i = 0; i < otpBoxes.length; i++) {
            final int currentIndex = i;
            EditText currentBox = otpBoxes[i];
            if (currentBox == null) continue;

            currentBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && currentIndex < otpBoxes.length - 1) {
                        EditText nextBox = otpBoxes[currentIndex + 1];
                        if (nextBox != null) {
                            nextBox.requestFocus();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            currentBox.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (currentBox.getText().toString().isEmpty() && currentIndex > 0) {
                        EditText prevBox = otpBoxes[currentIndex - 1];
                        if (prevBox != null) {
                            prevBox.requestFocus();
                            return true;
                        }
                    }
                }
                return false;
            });
        }
    }

    /**
     * Called when the user clicks the "Verify & Continue" button.
     */
    public void startDriver(View verifyButton) {
        boolean isDriver = getIntent().getBooleanExtra("isDriver", false);

        String enteredOtp = readOtpFromInputFields();
        if (enteredOtp.length() < 6) {
            Toast.makeText(this, "Please enter all 6 digits of your OTP code", Toast.LENGTH_SHORT).show();
            return;
        }

        navigateToTargetActivity(isDriver);
    }

    /**
     * Reads and combines the 6 separate OTP digit boxes into a single string.
     */
    private String readOtpFromInputFields() {
        EditText et2 = findViewById(R.id.editTextNumber2);
        EditText et3 = findViewById(R.id.editTextNumber3);
        EditText et4 = findViewById(R.id.editTextNumber4);
        EditText et5 = findViewById(R.id.editTextNumber5);
        EditText et6 = findViewById(R.id.editTextNumber6);
        EditText et7 = findViewById(R.id.editTextNumber7);

        StringBuilder otpBuilder = new StringBuilder();
        if (et2 != null) otpBuilder.append(et2.getText().toString());
        if (et3 != null) otpBuilder.append(et3.getText().toString());
        if (et4 != null) otpBuilder.append(et4.getText().toString());
        if (et5 != null) otpBuilder.append(et5.getText().toString());
        if (et6 != null) otpBuilder.append(et6.getText().toString());
        if (et7 != null) otpBuilder.append(et7.getText().toString());

        return otpBuilder.toString().trim();
    }

    /**
     * Navigates to Driver Activity if user is a driver, or Passenger Activity if passenger.
     */
    private void navigateToTargetActivity(boolean isDriver) {
        Intent nextIntent = new Intent(VerificationCode.this, isDriver ? Driver.class : Passenger.class);
        startActivity(nextIntent);
        finish();
    }

    /**
     * Called when the user clicks the "Back" button.
     */
    public void goBack(View backButton) {
        finish();
    }
}
