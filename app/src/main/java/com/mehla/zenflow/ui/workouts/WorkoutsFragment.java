package com.mehla.zenflow.ui.workouts;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.mehla.zenflow.MainActivity;
import com.mehla.zenflow.R;
import com.mehla.zenflow.databinding.FragmentWorkoutsBinding;
import com.mehla.zenflow.databinding.FragmentWorkoutsBinding;
import com.mehla.zenflow.model.Workout;
import com.mehla.zenflow.ui.login.Login;

//import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutsFragment extends Fragment {

    private FragmentWorkoutsBinding binding;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;

    private WorkoutAdapter adapter;

    List<Map<String, Object>> workoutsList;


    int mYear, mMonth, mDay, mMinute, mSecond;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        WorkoutsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(WorkoutsViewModel.class);

        binding = FragmentWorkoutsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        workoutsList = new ArrayList<>();

        adapter = new WorkoutAdapter(workoutsList);
        binding.workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.workoutsRecyclerView.setAdapter(adapter);

        if(user == null) {
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
        }

        fetchWorkouts();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                // Not needed since we are only implementing swipe to delete.
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();

                Map<String, Object> map = workoutsList.get(position);
                Workout deletedWorkout = mapToWorkout(map);
                String workoutIdToDelete = deletedWorkout.getWorkoutId();

                deleteWorkoutFromFirebase(workoutIdToDelete);

                workoutsList.remove(position);
                adapter.notifyItemRemoved(position);
            }



            private void removeDataFromFirebase(Map<String, Object> workout) {
                // Assuming you have a unique ID for each workout to identify it in Firebase

            }


            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;
                Paint paint = new Paint();

                if (dX > 0) { // Swipe Right
                    paint.setColor(Color.RED);
                    c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), paint);

                    paint.setColor(Color.WHITE);
                    paint.setTextSize(40);

                    String text = "Swipe to delete";
                    float textWidth = paint.measureText(text);
                    float textHeight = paint.descent() - paint.ascent();

                    // Calculate position to center the text
                    float textX = (float) itemView.getLeft() + (dX / 2) - (textWidth / 2);
                    float textY = (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop()) / 2 + (textHeight / 2) - paint.descent();

                    c.drawText(text, textX, textY, paint);
                } else if (dX < 0) { // Swipe Left
                    // For now, we aren't implementing left swipe. If needed, you can add a similar logic here for left swipe.
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        });
        itemTouchHelper.attachToRecyclerView(binding.workoutsRecyclerView);


        return root;
    }

    private void deleteWorkoutFromFirebase(String workoutIdToDelete) {
        db.collection("users").document(user.getUid()).collection("workouts")
                .document(workoutIdToDelete)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Workout deleted successfully.");

                        // Show Snackbar upon successful deletion
                        Snackbar.make(binding.getRoot(), "Workout deleted successfully!", Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "Error deleting workout.", e);

                        // Show Snackbar upon failure
                        Snackbar.make(binding.getRoot(), "Failed to delete workout.", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }


    public Workout mapToWorkout(Map<String, Object> map) {
        Workout workout = new Workout();

        workout.setWorkoutId((String) map.get("workoutId"));
        workout.setExerciseName((String) map.get("ExerciseName"));
        workout.setDay((int) (long) map.get("Day"));
        workout.setMonth((int) (long) map.get("Month"));
        workout.setYear((int) (long) map.get("Year"));
        workout.setMinute((int) (long) map.get("Minute"));
        workout.setSecond((int) (long) map.get("Second"));
        workout.setCreationDate((Timestamp) map.get("creationDate"));

        return workout;
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
                    (view, year, month, dayOfMonth) -> {
                        mYear = year;
                        mMonth = month + 1;
                        mDay = dayOfMonth;

                        datePlaceholder.setText("Select Date : " + dayOfMonth + "/" + (month + 1) + "/" + year);
                    },
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
                String exerciseName = exerciseInput.getText().toString();
                String time = timePlaceholder.getText().toString();
                String date = datePlaceholder.getText().toString();

                // Validate all fields are provided
                String errorMessage = null;
                if (exerciseName.isEmpty()) {
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

                saveWorkout(exerciseName);
                dialog.dismiss();
            }
        });

    }

    public void saveWorkout(String exerciseName) {
        if (user != null) {
            String userId = user.getUid();

            // Reference to the user's document
            DocumentReference userDocRef = db.collection("users").document(userId);

            // Check if the user document exists
            userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (!document.exists()) {
                            // The user doesn't exist, create a new document with placeholder data or some initial data
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("uid", userId);
                            userData.put("email", user.getEmail());  // example field

                            userDocRef.set(userData);  // This will create the document with the user's ID and set the initial data
                        }

                        // Now, add the workout data to the user's sub-collection 'workouts'
                        addWorkout(userId, exerciseName, getView());
                    } else {
                        Log.d("ExerciseDialog", "Failed checking user existence: ", task.getException());
                    }
                }
            });
        } else {
            Log.d("ExerciseDialog", "No user is signed in!");
        }
    }

    private void addWorkout(String userId, String exerciseName, View rootView) {
        Map<String, Object> workoutData = new HashMap<>();
        workoutData.put("ExerciseName", exerciseName);
        workoutData.put("Year", mYear);
        workoutData.put("Month", mMonth);
        workoutData.put("Day", mDay);
        workoutData.put("Minute", mMinute);
        workoutData.put("Second", mSecond);
        workoutData.put("creationDate", FieldValue.serverTimestamp());

        db.collection("users").document(userId).collection("workouts")
                .add(workoutData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String uniqueId = documentReference.getId();

                        workoutData.put("workoutId", uniqueId);
                        documentReference.set(workoutData, SetOptions.merge())
                                .addOnSuccessListener(aVoid -> {
                                    Snackbar.make(rootView, "Data inserted successfully!", Snackbar.LENGTH_SHORT).show();
                                    fetchWorkouts();
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("ExerciseDialog", "Error updating workout ID: " + e.getMessage());
                                    Snackbar.make(rootView, "Error: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ExerciseDialog", "Error saving workout data: " + e.getMessage());
                        Snackbar.make(rootView, "Error: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
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

                    mMinute = selectedMinutes;
                    mSecond = selectedSeconds;

                    timePlaceholder.setText("Select time : " + selectedMinutes + " min " + selectedSeconds + " sec");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void fetchWorkouts() {
        if(user != null) {
            db.collection("users").document(user.getUid()).collection("workouts")
                    .orderBy("creationDate", Query.Direction.DESCENDING)  // Order by creationDate in descending order
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            workoutsList.clear(); // Clear the existing data

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Workout workout = document.toObject(Workout.class);
                                workout.setWorkoutId(document.getId()); // Set the workoutId
                                workoutsList.add(workout.toMap());
                            }

                            if (workoutsList.isEmpty()) {
                                binding.workoutsRecyclerView.setVisibility(View.GONE);
                                binding.noWorkoutsTextview.setVisibility(View.VISIBLE);
                            } else {
                                binding.workoutsRecyclerView.setVisibility(View.VISIBLE);
                                binding.noWorkoutsTextview.setVisibility(View.GONE);
                            }

                            // Notify the adapter that data has changed
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error fetching workouts: ", task.getException());
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}