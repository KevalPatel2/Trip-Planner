package com.example.tripplanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Intent;
import androidx.activity.ComponentActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SummaryActivity extends ComponentActivity {

    private TextView textViewSummary;
    private TextView textViewTripDetails;
    private Button buttonEdit;
    private Button buttonFinish;
    private ProgressBar progressBarCountdown;
    private TextView textViewCountdown;
    private long endTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // Initialize UI components
        textViewSummary = findViewById(R.id.textViewSummary);
        textViewTripDetails = findViewById(R.id.textViewTripDetails);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonFinish = findViewById(R.id.buttonFinish);
        progressBarCountdown = findViewById(R.id.progressBarCountdown);
        textViewCountdown = findViewById(R.id.textViewCountdown);

        // Retrieve trip information from Intent extras
        Intent intent = getIntent();
        String startPoint = intent.getStringExtra("startPoint");
        String destination = intent.getStringExtra("destination");
        String selectedDate = intent.getStringExtra("selectedDate");
        String selectedTime = intent.getStringExtra("selectedTime");
        String notes = intent.getStringExtra("notes");
        int numberOfPeople = intent.getIntExtra("numberOfPeople", 0);

        // Display the trip details
        String tripDetails = "Start Point: " + startPoint + "\n" +
                "Destination: " + destination + "\n" +
                "Date: " + selectedDate + "\n" +
                "Time: " + selectedTime + "\n" +
                "Number of People: " + numberOfPeople + "\n" +
                "Notes: " + notes;
        textViewTripDetails.setText(tripDetails);

        // Start countdown timer
        startCountdownTimer(selectedDate, selectedTime);

        // Button click listeners
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the total number of people from the intent extras
                int numberOfPeople = getIntent().getIntExtra("numberOfPeople", 0);

                // Create an intent to start the CheckoutActivity
                Intent intent = new Intent(SummaryActivity.this, CheckoutActivity.class);
                // Pass the total number of people to the CheckoutActivity
                intent.putExtra("numberOfPeople", numberOfPeople);
                startActivity(intent);
            }
        });
    }

    private void startCountdownTimer(String selectedDate, String selectedTime) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date dateTime = dateFormat.parse(selectedDate + " " + selectedTime);
            long selectedDateTimeMillis = dateTime.getTime();
            long currentTimeMillis = System.currentTimeMillis();
            endTimeMillis = selectedDateTimeMillis - currentTimeMillis;

            new CountDownTimer(endTimeMillis, 1000) {
                public void onTick(long millisUntilFinished) {
                    updateCountdownText(millisUntilFinished);
                    updateProgressBar(millisUntilFinished);
                }

                public void onFinish() {
                    textViewCountdown.setText("00:00:00:00");
                    progressBarCountdown.setProgress(0);
                    showCountdownFinishedAlert();
                }
            }.start();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateCountdownText(long millisUntilFinished) {
        long seconds = millisUntilFinished / 1000;
        long days = seconds / (24 * 3600);
        long hours = (seconds % (24 * 3600)) / 3600;
        long minutes = ((seconds % (24 * 3600)) % 3600) / 60;
        seconds = ((seconds % (24 * 3600)) % 3600) % 60;

        String countdownText = String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
        textViewCountdown.setText(countdownText);
    }

    private void updateProgressBar(long millisUntilFinished) {
        int progress = (int) (((float) millisUntilFinished / endTimeMillis) * 100);
        progressBarCountdown.setProgress(progress);
    }

    private void showCountdownFinishedAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Countdown Finished")
                .setMessage("Your trip is about to start!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // You can add any action you want here, or just dismiss the dialog
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
