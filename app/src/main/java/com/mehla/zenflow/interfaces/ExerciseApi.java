package com.mehla.zenflow.interfaces;

import com.mehla.zenflow.model.Exercise;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ExerciseApi {
    @GET("priyangsubanerjee/yogism/master/all-poses.json")
    Call<List<Exercise>> getExercises();
}
