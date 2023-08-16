package com.mehla.zenflow.ui.workouts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.mehla.zenflow.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    private final List<Map<String, Object>> workoutsData;

    public WorkoutAdapter(List<Map<String, Object>> data) {
        this.workoutsData = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView workoutTitle;
        TextView workoutTime;
        TextView workoutDate;
        TextView workoutCreationTime;

        public ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.cardView);
            workoutTitle = v.findViewById(R.id.workout_title);
            workoutTime = v.findViewById(R.id.workout_time);
            workoutDate = v.findViewById(R.id.workout_date);
            workoutCreationTime = v.findViewById(R.id.creation_datetime);
        }
    }

    @NonNull
    @Override
    public WorkoutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String, Object> workout = workoutsData.get(position);

        String exerciseName = (String) workout.get("ExerciseName");
        holder.workoutTitle.setText(exerciseName);

        Long yearLong = (Long) workout.get("Year");
        int year = yearLong.intValue();

        Long monthLong = (Long) workout.get("Month");
        int month = monthLong.intValue();

        Long dayLong = (Long) workout.get("Day");
        int day = dayLong.intValue();

        String dateFormatted = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month, day);
        holder.workoutDate.setText("Date: " + dateFormatted);

        Long minuteLong = (Long) workout.get("Minute");
        int minute = minuteLong.intValue();

        Long secondLong = (Long) workout.get("Second");
        int second = secondLong.intValue();
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minute, second);
        holder.workoutTime.setText("Time: " + timeFormatted);

        Timestamp creationTimestamp = (Timestamp) workout.get("creationDate");
        Date creationDate = creationTimestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String creationDateTime = sdf.format(creationDate);
        holder.workoutCreationTime.setText(creationDateTime);
    }

    @Override
    public int getItemCount() {
        return workoutsData.size();
    }
}
