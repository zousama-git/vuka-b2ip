package com.example.vuka;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class VerificationCode extends AppCompatActivity {
    private EditText[] otpBoxes;
    private MaterialButton btnResendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_verification_code);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        btnResendCode = findViewById(R.id.btnResendCode);
        setupOtpInputs();
    }

    private void setupOtpInputs() {
        int[] ids = {R.id.editTextNumber2, R.id.editTextNumber3, R.id.editTextNumber4, R.id.editTextNumber5, R.id.editTextNumber6, R.id.editTextNumber7};
        otpBoxes = new EditText[ids.length];

        for (int i = 0; i < ids.length; i++) {
            int idx = i;
            otpBoxes[i] = findViewById(ids[i]);
            if (otpBoxes[i] == null) continue;

            otpBoxes[i].setOnFocusChangeListener((v, hasFocus) -> { //removes dots when on current box
                if (v instanceof EditText) {
                    EditText box = (EditText) v;
                    if (hasFocus) {
                        box.setHint("");
                    } else if (box.getText().length() == 0) {
                        box.setHint("•");
                    }
                }
            });

            otpBoxes[i].addTextChangedListener(new TextWatcher() { //change to next box after input
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void afterTextChanged(Editable s) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && idx < otpBoxes.length - 1) otpBoxes[idx + 1].requestFocus();
                }
            });

            otpBoxes[i].setOnKeyListener((v, code, e) -> {
                if (code == KeyEvent.KEYCODE_DEL && e.getAction() == KeyEvent.ACTION_DOWN && otpBoxes[idx].getText().length() == 0 && idx > 0) {
                    otpBoxes[idx - 1].requestFocus();
                    return true;
                }
                return false;
            });
        }
    }

    //resend otp
    public void resendCode(View view) {
        if (btnResendCode != null) {
            btnResendCode.setEnabled(false);
        }
        Toast.makeText(this, "New verification code sent!", Toast.LENGTH_SHORT).show();

        // 120,000 ms cooldown timer
        new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                long min = secondsLeft / 60;
                long sec = secondsLeft % 60;
                if (btnResendCode != null) {
                    btnResendCode.setText(String.format(Locale.getDefault(), "Resend Code (%02d:%02d)", min, sec));
                }
            }

            public void onFinish() {
                if (btnResendCode != null) {
                    btnResendCode.setEnabled(true);
                    btnResendCode.setText(R.string.resend_code);
                }
            }
        }.start();
    }

    public void startDriver(View verifyButton) {
        boolean isDriver = getIntent().getBooleanExtra("isDriver", false);

        if (readOtpFromInputFields().length() < 6) {
            Toast.makeText(this, "Please enter all 6 digits of your OTP code", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent nextIntent = new Intent(VerificationCode.this, isDriver ? Driver.class : Passenger.class);
        startActivity(nextIntent);
        finish();
    }

    private String readOtpFromInputFields() {
        StringBuilder otp = new StringBuilder();
        if (otpBoxes != null) {
            for (EditText box : otpBoxes) {
                if (box != null) otp.append(box.getText().toString());
            }
        }
        return otp.toString().trim();
    }

    public void goBack(View backButton) {
        finish();
    }
}
