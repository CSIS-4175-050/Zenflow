package com.mehla.zenflow.ui.workouts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mehla.zenflow.R;

import java.util.List;
import java.util.Map;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    private final List<Map<String, Object>> workoutsData;

    public WorkoutAdapter(List<Map<String, Object>> data) {
        this.workoutsData = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView workoutTitle;  // Adjust as needed

        public ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.cardView);  // Adjust the ID as needed
            workoutTitle = v.findViewById(R.id.workout_title);  // Adjust as needed
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
        // Set data to the views here. Example:
        holder.workoutTitle.setText(workout.get("ExerciseName").toString());
    }

    @Override
    public int getItemCount() {
        return workoutsData.size();
    }
}
