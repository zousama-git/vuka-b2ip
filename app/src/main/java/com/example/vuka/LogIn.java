package com.example.vuka;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * LogIn Activity - Handles User Login and Phone Number Formatting UI.
 * Written with clean, simple Java 8 constructs for students.
 */
public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_log_in);

        // Adjust padding for system bars
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Attach phone number formatting listener
        setupPhoneFormatting();
    }

    /**
     * Automatically formats phone numbers with spaces as the user types (e.g. 82 123 4567).
     */
    private void setupPhoneFormatting() {
        EditText editTextPhone = findViewById(R.id.editTextPhone);
        if (editTextPhone == null) return;

        editTextPhone.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                // Extract numbers only
                String digitsOnly = s.toString().replaceAll("\\D", "");
                if (digitsOnly.length() > 9) {
                    digitsOnly = digitsOnly.substring(0, 9);
                }

                // Format as XX XXX XXXX
                StringBuilder formattedText = new StringBuilder();
                for (int i = 0; i < digitsOnly.length(); i++) {
                    if (i == 2 || i == 5) {
                        formattedText.append(" ");
                    }
                    formattedText.append(digitsOnly.charAt(i));
                }

                s.replace(0, s.length(), formattedText.toString());
                isEditing = false;
            }
        });
    }

    /**
     * Called when the user clicks the "Send OTP" button.
     */
    public void startOTP(View buttonView) {
        RadioButton rdDriver = findViewById(R.id.rdDriver);
        boolean isDriverSelected = (rdDriver != null && rdDriver.isChecked());

        EditText editTextPhone = findViewById(R.id.editTextPhone);
        String enteredPhone = (editTextPhone != null) ? editTextPhone.getText().toString().trim() : "";

        // Validate South African phone number format
        if (!isValidSouthAfricanPhone(enteredPhone)) {
            Toast.makeText(this, "Please enter a valid South African mobile number (e.g. 82 123 4567)", Toast.LENGTH_LONG).show();
            return;
        }

        String formattedPhone = convertToInternationalFormat(enteredPhone);
        goToVerificationScreen(formattedPhone, isDriverSelected);
    }

    /**
     * Validates if input is a valid South African mobile number.
     */
    private boolean isValidSouthAfricanPhone(String phoneInput) {
        String numbers = phoneInput.replaceAll("\\D", "");
        if (numbers.length() == 9) {
            return numbers.matches("^[6-9][0-9]{8}$");
        } else if (numbers.length() == 10 && numbers.startsWith("0")) {
            return numbers.matches("^0[6-9][0-9]{8}$");
        } else if (numbers.length() == 11 && numbers.startsWith("27")) {
            return numbers.matches("^27[6-9][0-9]{8}$");
        }
        return false;
    }

    /**
     * Converts phone number into international +27 format.
     */
    private String convertToInternationalFormat(String phoneInput) {
        String numbers = phoneInput.replaceAll("\\D", "");
        if (numbers.length() == 9) {
            return "+27" + numbers;
        } else if (numbers.length() == 10 && numbers.startsWith("0")) {
            return "+27" + numbers.substring(1);
        } else if (numbers.length() == 11 && numbers.startsWith("27")) {
            return "+" + numbers;
        }
        return "+27" + numbers;
    }

    /**
     * Navigates to the VerificationCode activity.
     */
    private void goToVerificationScreen(String phoneNumber, boolean isDriver) {
        Intent intent = new Intent(LogIn.this, VerificationCode.class);
        intent.putExtra("isDriver", isDriver);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);
    }
}
