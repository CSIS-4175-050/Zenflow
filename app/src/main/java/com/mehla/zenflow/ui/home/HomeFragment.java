package com.mehla.zenflow.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.mehla.zenflow.MainActivity;
import com.mehla.zenflow.R;
import com.mehla.zenflow.databinding.FragmentHomeBinding;
import com.mehla.zenflow.interfaces.ExerciseApi;
import com.mehla.zenflow.model.Exercise;
import com.mehla.zenflow.ui.dashboard.ExerciseDetails;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Random random;

    static TextView dayQuote;
    Button btnRefreshQuote;

    static ArrayList<String> quotes;




    private TextView txtPopularWorkoutTitle, cardTitle, cardDesc;
    private ImageView imageView;
    private ProgressBar imageLoading;
    private SharedPreferences sharedPreferences;
    private List<Exercise> exerciseList;
    private Exercise exercise;

    private static final int MAX_RETRIES = 3;
    private int retryCount = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        quotes = new ArrayList<>();
        quotes.add("The only way to do great work is to love what you do. - Steve Jobs");
        quotes.add("Believe you can and you're halfway there. - Theodore Roosevelt");
        quotes.add("Don't watch the clock; do what it does. Keep going. - Sam Levenson");
        quotes.add("The future belongs to those who believe in the beauty of their dreams. - Eleanor Roosevelt");
        quotes.add("Success is not final, failure is not fatal: It is the courage to continue that counts. - Winston Churchill");

        btnRefreshQuote = binding.refreshQuote;
        btnRefreshQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshQuote();
            }
        });
        refreshQuote();

        txtPopularWorkoutTitle = root.findViewById(R.id.txt_popular_workout_title);
        cardTitle = root.findViewById(R.id.card_title);
        cardDesc = root.findViewById(R.id.card_desc);
        imageView = root.findViewById(R.id.imageView);
        imageLoading = root.findViewById(R.id.image_loading);
        binding.popularExerciseCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an instance of the ExerciseDetails fragment
                ExerciseDetails detailFragment = new ExerciseDetails();

                // Pass the selected exercise details as arguments to the ExerciseDetails fragment
                Bundle args = new Bundle();

                args.putString("englishName", exercise.getEnglishName());
                args.putString("sanskritName", exercise.getSanskritName());
                args.putString("description", exercise.getDescription());
                args.putString("image", exercise.getImage());
                args.putString("benefits", exercise.getBenefits());
                args.putString("steps", exercise.getSteps());
                args.putString("time", exercise.getTime());
                args.putString("category", exercise.getCategory());
                args.putString("target", exercise.getTarget());

                // Add more details as needed
                detailFragment.setArguments(args);

                // Replace the current fragment with the ExerciseDetails fragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setReorderingAllowed(true); // Set reordering allowed flag
                transaction.replace(R.id.nav_host_fragment_activity_main, detailFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        sharedPreferences = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);

        fetchExercises();

        return root;
    }

    private void fetchExercises() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ExerciseApi exerciseApi = retrofit.create(ExerciseApi.class);
        Call<List<Exercise>> call = exerciseApi.getExercises();

        call.enqueue(new retrofit2.Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                if (!response.isSuccessful()) {
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                exerciseList = response.body();
                updatePopularWorkout();
            }

            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePopularWorkout() {
        if(retryCount < MAX_RETRIES) {
            Random random = new Random();
            int index = random.nextInt(exerciseList.size());
            exercise = exerciseList.get(index);

            cardTitle.setText(exercise.getSanskritName());
            cardDesc.setText(exercise.getDescription());

            Log.d(">>", exercise.getSanskritName());

            imageLoading.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(exercise.getImage())
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            imageLoading.setVisibility(View.GONE);
                            retryCount = 0;
                        }

                        @Override
                        public void onError(Exception e) {
                            imageLoading.setVisibility(View.GONE);
                            retryCount++;
                            updatePopularWorkout();
                        }
                    });
        }
    }


    private String getRandomQuote() {
        int index = random.nextInt(quotes.size());
        return quotes.get(index);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void refreshQuote() {
        // Quote of the day
        random = new Random();
        String randomQuote = getRandomQuote();
        dayQuote = binding.dayQuote;
        dayQuote.setText(randomQuote);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update Option Menu
        ((MainActivity) getActivity()).updateMenu();
    }

}