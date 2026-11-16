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

public class LogIn extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_log_in);
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        setupPhoneFormatting();
    }

    private void setupPhoneFormatting() {
        EditText editTextPhone = findViewById(R.id.editTextPhone);
        if (editTextPhone == null) return;

        editTextPhone.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                String digitsOnly = s.toString().replaceAll("\\D", "");
                // limit to 9 digits
                if (digitsOnly.length() > 9) {
                    digitsOnly = digitsOnly.substring(0, 9);
                }

                // Format as XX XXX XXXX (e.g. 82 123 4567)
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

    public void startOTP(View buttonView) {
        RadioButton rdDriver = findViewById(R.id.rdDriver);
        boolean isDriverSelected = (rdDriver != null && rdDriver.isChecked());

        EditText editTextPhone = findViewById(R.id.editTextPhone);
        String enteredPhone = (editTextPhone != null) ? editTextPhone.getText().toString().trim() : "";

        if (!isValidSouthAfricanPhone(enteredPhone)) {
            Toast.makeText(this, "Please enter a valid 9-digit mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        String formattedPhone = convertToInternationalFormat(enteredPhone);
        goToVerificationScreen(formattedPhone, isDriverSelected);
    }

    private boolean isValidSouthAfricanPhone(String phoneInput) {
        String numbers = phoneInput.replaceAll("\\D", "");
        return numbers.length() == 9;
    }

    private String convertToInternationalFormat(String phoneInput) {
        String numbers = phoneInput.replaceAll("\\D", "");
        return "+27" + numbers;
    }

    private void goToVerificationScreen(String phoneNumber, boolean isDriver) {
        Intent intent = new Intent(LogIn.this, VerificationCode.class);
        intent.putExtra("isDriver", isDriver);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);
    }
}
