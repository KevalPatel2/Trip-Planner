package com.example.tripplanner;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.content.Intent;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import java.util.Arrays;
import java.util.Calendar;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

// DestinationActivity.java
public class DestinationActivity extends AppCompatActivity
{
    private TextView textViewSelectedDate;
    private TextView textViewSelectedTime;
    private EditText editTextNotes;
    private TextView textViewCharacterCount;
    private TextView textViewSelectedStartPoint;

    private TextView textViewSelectedDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

            // Initialize Places API
            Places.initialize(getApplicationContext(), "AIzaSyC7i8wMfV0O5mNQvY2NOQvWSkoGo2V_GCM");

            // Initialize Places Client
            PlacesClient placesClient = Places.createClient(this);

        DatePicker datePicker = findViewById(R.id.datePicker);
        TimePicker timePicker = findViewById(R.id.timePicker);

        textViewSelectedStartPoint = findViewById(R.id.textViewSelectedStartPoint);
        textViewSelectedDestination = findViewById(R.id.textViewSelectedDestination);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        textViewSelectedTime = findViewById(R.id.textViewSelectedTime);

        // Set onClickListener for date icon and text
        ImageView imageViewDate = findViewById(R.id.imageViewDate);
        TextView textViewDateAndTime = findViewById(R.id.textViewDateAndTime);

        // Inside onCreate() method after initializing other views
        NumberPicker numberPickerPeople = findViewById(R.id.numberPickerPeople);
        numberPickerPeople.setMinValue(1); // Minimum value
        numberPickerPeople.setMaxValue(5); // Maximum value

        editTextNotes = findViewById(R.id.editTextNotes);
        textViewCharacterCount = findViewById(R.id.textViewCharacterCount);

        // Set up AutocompleteSupportFragment for start point
        AutocompleteSupportFragment autocompleteStartPointFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_start_point_fragment);

        autocompleteStartPointFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteStartPointFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            public void onPlaceSelected(@NonNull Place place) {
                // Inside onPlaceSelected method for start point AutocompleteSupportFragment
                textViewSelectedStartPoint.setVisibility(View.VISIBLE);
                textViewSelectedStartPoint.setText(place.getName());
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle error
            }
        });

        // Set up AutocompleteSupportFragment for destination
        AutocompleteSupportFragment autocompleteDestinationFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_destination_fragment);
        autocompleteDestinationFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteDestinationFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                textViewSelectedDestination.setVisibility(View.VISIBLE);
                textViewSelectedDestination.setText(place.getName());
            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle error
            }
        });

        //Set up TextWatcher
        editTextNotes.addTextChangedListener(new TextWatcher()
        {
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count)
            {
                // Not needed
            }

            public void onTextChanged(CharSequence charSequence, int start, int before, int count)
            {
                //Update Character Count
                int currentCharCount = charSequence.length();
                textViewCharacterCount.setText(String.valueOf(currentCharCount) + " characters");

                //Limit to 250 words
                if(currentCharCount >300)
                {
                    String limitedText = charSequence.subSequence(0, 300).toString();
                    editTextNotes.removeTextChangedListener(this);  // Remove listener temporarily
                    editTextNotes.setText(limitedText);
                    editTextNotes.setSelection(300);  // Move cursor to the end
                    editTextNotes.addTextChangedListener(this);  // Add back listener
                }
            }

            public void afterTextChanged(Editable editable)
            {
                // Not needed
            }
        });
        imageViewDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        textViewDateAndTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Set onClickListener for clock icon
        ImageView imageViewClock = findViewById(R.id.imageViewClock);
        imageViewClock.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showTimePicker();
            }
        });

        Button nextButton = findViewById(R.id.buttonNext);
        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Navigate to SummaryActivity
                if(isFieldsValid())
                {
                    int numberOfPeople = numberPickerPeople.getValue(); // Get the selected value
                    Intent intent = new Intent(DestinationActivity.this, SummaryActivity.class);
                    intent.putExtra("startPoint", textViewSelectedStartPoint.getText().toString().trim());
                    intent.putExtra("destination", textViewSelectedDestination.getText().toString().trim());
                    intent.putExtra("selectedDate", textViewSelectedDate.getText().toString().trim());
                    intent.putExtra("selectedTime", textViewSelectedTime.getText().toString().trim());
                    intent.putExtra("notes", editTextNotes.getText().toString().trim());
                    intent.putExtra("numberOfPeople", numberOfPeople);
                    startActivity(intent);
                }
            }
        });
    }

    private void showDatePicker()
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        textViewSelectedDate.setText(selectedDate);
                        textViewSelectedDate.setVisibility(View.VISIBLE);
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void showTimePicker()
    {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {
                        String selectedTime = hourOfDay + ":" + minute;
                        textViewSelectedTime.setText(selectedTime);
                        textViewSelectedTime.setVisibility(View.VISIBLE);
                    }
                },
                hour, minute, true);

        timePickerDialog.show();
    }

    private boolean isFieldsValid()
    {
        String startPoint = textViewSelectedStartPoint.getText().toString().trim();
        String destination = textViewSelectedDestination.getText().toString().trim();
        String selectedDate = textViewSelectedDate.getText().toString().trim();
        String selectedTime = textViewSelectedTime.getText().toString().trim();

        if (startPoint.isEmpty()) {
            showError("Please enter the start point.");
            return false;
        }

        if (destination.isEmpty()) {
            showError("Please enter the destination.");
            return false;
        }

        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            showError("Please select the date and time.");
            return false;
        }

        return true;
    }

    private void showError(String errorMessage)
    {
        // Display an error message, you can use a Toast or any other UI element for this
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

}


