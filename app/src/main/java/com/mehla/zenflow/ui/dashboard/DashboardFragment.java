package com.mehla.zenflow.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mehla.zenflow.R;
import com.mehla.zenflow.databinding.FragmentDashboardBinding;
import com.mehla.zenflow.interfaces.ExerciseApi;
import com.mehla.zenflow.model.Exercise;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private RecyclerView recyclerView;
    private ExerciseAdapter exerciseAdapter;
    private List<Exercise> exerciseList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exerciseList = new ArrayList<>();
        exerciseAdapter = new ExerciseAdapter(getContext(), exerciseList);
        recyclerView.setAdapter(exerciseAdapter);

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

        call.enqueue(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                Log.d(">>", "Here");
                if (!response.isSuccessful()) {
                    Log.d(">>", response.message().toString());
                    Toast.makeText(getContext(), "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                exerciseList = response.body();
                exerciseAdapter.updateExercises(exerciseList);
            }

            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                Log.d(">>", "Here " + t.getMessage());
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
