package com.mehla.zenflow.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mehla.zenflow.ui.login.Login;
import com.mehla.zenflow.services.DarkMode;
import com.mehla.zenflow.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser user;
    Button btnLogout;
    TextView userDetails;

    private @NonNull FragmentSettingsBinding binding;
    private Switch switchDarkMode;

    DarkMode dm;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        btnLogout = binding.logout;
        userDetails = binding.userDetails;

        user = mAuth.getCurrentUser();

        if(user == null) {
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
        } else {
            userDetails.setText(user.getEmail());
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        });

        // Initialize DarkMode
        dm = new DarkMode(requireContext());

        // Set the initial state of the toggle button
        switchDarkMode = binding.switchDarkMode;
        switchDarkMode.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Enable dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    dm.saveDarkModeState(true);
                } else {
                    // Disable dark mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    dm.saveDarkModeState(false);
                }
                requireActivity().recreate(); // Recreate the activity to apply the new theme
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}