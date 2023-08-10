package com.mehla.zenflow.ui.workouts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.mehla.zenflow.MainActivity;
import com.mehla.zenflow.R;
import com.mehla.zenflow.databinding.FragmentWorkoutsBinding;
import com.mehla.zenflow.databinding.FragmentWorkoutsBinding;

import java.text.DateFormat;
import java.util.Calendar;

public class WorkoutsFragment extends Fragment {

    private FragmentWorkoutsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        WorkoutsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(WorkoutsViewModel.class);

        binding = FragmentWorkoutsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update Option Menu
        ((MainActivity) getActivity()).updateMenu();
    }

    public void addExercise() {
        Log.d(">>", "addExercise: ");

        Activity activity = getActivity();
        if (activity == null) {
            // Fragment is not attached to an activity, so just return
            Log.d(">>", "Fragment is not attached to an activity");
            return;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Exercise");

        // Inflate the layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_exercise, null);

        // Find the views
        final EditText exerciseInput = dialogView.findViewById(R.id.exerciseInput);
        final TextView timePlaceholder = dialogView.findViewById(R.id.timePlaceholder);
        final TextView datePlaceholder = dialogView.findViewById(R.id.datePlaceholder);

        // Set up time placeholder click listener
        timePlaceholder.setOnClickListener(v -> {
            showTimePickerDialog(timePlaceholder);
        });


        // Set up date placeholder click listener
        datePlaceholder.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    (view, year, month, dayOfMonth) -> datePlaceholder.setText("Select Date : " + dayOfMonth + "/" + (month + 1) + "/" + year),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            // Set maximum date to today
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

            // Set minimum date to one year ago
            calendar.add(Calendar.YEAR, -1);
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

            datePickerDialog.show();
        });

        // Set the view of the builder
        builder.setView(dialogView);

        builder.setPositiveButton("Submit", null);
        builder.setNegativeButton("Cancel", null);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values
                String exercise = exerciseInput.getText().toString();
                String time = timePlaceholder.getText().toString();
                String date = datePlaceholder.getText().toString();

                // Validate all fields are provided
                String errorMessage = null;
                if (exercise.isEmpty()) {
                    errorMessage = "Exercise name is required!";
                } else if ("Select Time".equals(time)) {
                    errorMessage = "Time selection is required!";
                } else if ("Select Date".equals(date)) {
                    errorMessage = "Date selection is required!";
                }

                if (errorMessage != null) {
                    // Show Snackbar with specific error message
                    Snackbar.make(dialogView, errorMessage, Snackbar.LENGTH_LONG).show();
                    return;
                }

                // Log the values
                Log.d("ExerciseDialog", "Exercise: " + exercise);
                Log.d("ExerciseDialog", "Time: " + time);
                Log.d("ExerciseDialog", "Date: " + date);
                dialog.dismiss();
            }
        });

    }

    private void showTimePickerDialog(TextView timePlaceholder) {
        LinearLayout minuteLayout = new LinearLayout(getActivity());
        minuteLayout.setOrientation(LinearLayout.VERTICAL);
        minuteLayout.setGravity(Gravity.CENTER);

        TextView minuteTitle = new TextView(getActivity());
        minuteTitle.setText("Minutes");
        minuteTitle.setGravity(Gravity.CENTER);

        NumberPicker minutePicker = new NumberPicker(getActivity());
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(1); // Default value

        minuteLayout.addView(minuteTitle);
        minuteLayout.addView(minutePicker);


        LinearLayout secondLayout = new LinearLayout(getActivity());
        secondLayout.setOrientation(LinearLayout.VERTICAL);
        secondLayout.setGravity(Gravity.CENTER);

        TextView secondTitle = new TextView(getActivity());
        secondTitle.setText("Seconds");
        secondTitle.setGravity(Gravity.CENTER);

        NumberPicker secondPicker = new NumberPicker(getActivity());
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);

        secondLayout.addView(secondTitle);
        secondLayout.addView(secondPicker);


        LinearLayout timePickerLayout = new LinearLayout(getActivity());
        timePickerLayout.setOrientation(LinearLayout.HORIZONTAL);
        timePickerLayout.setGravity(Gravity.CENTER);
        timePickerLayout.addView(minuteLayout);
        timePickerLayout.addView(secondLayout);

        new AlertDialog.Builder(getActivity())
                .setTitle("Select Time")
                .setView(timePickerLayout)
                .setPositiveButton("OK", (dialog, which) -> {
                    int selectedMinutes = minutePicker.getValue();
                    int selectedSeconds = secondPicker.getValue();
                    timePlaceholder.setText("Select time : " + selectedMinutes + " min " + selectedSeconds + " sec");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}