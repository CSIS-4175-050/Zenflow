package com.mehla.zenflow.ui.dashboard;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.Toolbar;

import com.mehla.zenflow.R;
import com.mehla.zenflow.databinding.FragmentDashboardBinding;
import com.mehla.zenflow.databinding.FragmentExerciseDetailsBinding;
import com.mehla.zenflow.model.Exercise;
import com.squareup.picasso.Picasso;

public class ExerciseDetails extends Fragment {

    Exercise e;

    FragmentExerciseDetailsBinding binding;


    public ExerciseDetails() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_details, container, false);

        binding = FragmentExerciseDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        e = new Exercise();

        Bundle args = getArguments();
        if (args != null) {
            e.setEnglishName(args.getString("englishName"));
            e.setSanskritName(args.getString("sanskritName"));
            e.setDescription(args.getString("description"));
            e.setBenefits(args.getString("benefits"));
            e.setCategory(args.getString("category"));
            e.setSteps(args.getString("steps"));
            e.setTarget(args.getString("target"));
            e.setTime(args.getString("time"));
            e.setImage(args.getString("image"));
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the AppCompatActivity hosting this fragment
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(e.getEnglishName());

                // Enable the Up/Back button in the ActionBar
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);

                actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24);
            }
        }

        binding.edTitle.setText(e.getEnglishName());
        binding.edSanskritName.setText(e.getSanskritName());
        binding.edTime.setText(e.getTime());
        binding.edCategory.setText(e.getCategory());
        binding.edDescription.setText(e.getDescription());
        binding.edSteps.setText(e.getSteps());
        binding.edBenefits.setText(e.getBenefits());
        Picasso.get()
                .load(e.getImage())
                .into(binding.edImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        binding.imageLoading.setVisibility(View.GONE);
                        binding.errorImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        binding.imageLoading.setVisibility(View.GONE);
                        binding.errorImage.setVisibility(View.VISIBLE);
                    }
                });


    }

}