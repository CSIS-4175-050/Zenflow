package com.mehla.zenflow.ui.workouts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mehla.zenflow.MainActivity;
import com.mehla.zenflow.R;
import com.mehla.zenflow.databinding.FragmentWorkoutsBinding;
import com.mehla.zenflow.databinding.FragmentWorkoutsBinding;

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

        // Set up the input
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String exerciseName = input.getText().toString();
                // TODO: Save the exercise name
                Log.d("Exercise Name", exerciseName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}