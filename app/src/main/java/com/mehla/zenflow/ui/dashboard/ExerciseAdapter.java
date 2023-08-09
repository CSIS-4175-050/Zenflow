package com.mehla.zenflow.ui.dashboard;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mehla.zenflow.R;
import com.mehla.zenflow.model.Exercise;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Callback;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private Context context;
    private List<Exercise> exerciseList;
    private OnItemClickListener clickListener;

    public ExerciseAdapter(Context context, List<Exercise> exerciseList, OnItemClickListener clickListener) {
        this.context = context;
        this.clickListener = clickListener;
        this.exerciseList = exerciseList;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.exercise_item, parent, false);
        return new ExerciseViewHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        holder.name.setText(exercise.getSanskritName());
        holder.description.setText(exercise.getDescription());
        // Load image using Picasso or Glide
        Picasso.get()
                .load(exercise.getImage())
                .into(holder.exercise_image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        holder.imageLoading.setVisibility(View.GONE);
                        holder.errorImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.imageLoading.setVisibility(View.GONE);
                        holder.errorImage.setVisibility(View.VISIBLE);
                    }
                });

        holder.itemView.setOnClickListener(view -> {
            if (clickListener != null) {
                clickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    public void updateExercises(List<Exercise> exercises) {
        this.exerciseList = exercises;
        notifyDataSetChanged();
    }

    public class ExerciseViewHolder extends RecyclerView.ViewHolder {

        TextView name, description;
        ImageView exercise_image, errorImage;

        ProgressBar imageLoading;


        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.exercise_name);
            description = itemView.findViewById(R.id.exercise_description);
            exercise_image = itemView.findViewById(R.id.exercise_image);
            errorImage = itemView.findViewById(R.id.error_image);
            imageLoading = itemView.findViewById(R.id.image_loading);
        }
    }
}
