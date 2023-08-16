package com.mehla.zenflow.model;


import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Workout {
    private String workoutId;
    private String ExerciseName;
    private long Day;
    private long Month;
    private long Year;
    private long Minute;
    private long Second;
    private Timestamp creationDate;

    public String getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(String workoutId) {
        this.workoutId = workoutId;
    }

    public String getExerciseName() {
        return ExerciseName;
    }

    public void setExerciseName(String exerciseName) {
        ExerciseName = exerciseName;
    }

    public long getDay() {
        return Day;
    }

    public void setDay(int day) {
        Day = day;
    }

    public long getMonth() {
        return Month;
    }

    public void setMonth(int month) {
        Month = month;
    }

    public long getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }

    public long getMinute() {
        return Minute;
    }

    public void setMinute(int minute) {
        Minute = minute;
    }

    public long getSecond() {
        return Second;
    }

    public void setSecond(int second) {
        Second = second;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("workoutId", this.workoutId);
        map.put("ExerciseName", this.ExerciseName);
        map.put("Day", this.Day);
        map.put("Month", this.Month);
        map.put("Year", this.Year);
        map.put("Minute", this.Minute);
        map.put("Second", this.Second);
        map.put("creationDate", this.creationDate);
        return map;
    }

}
