package com.mehla.zenflow.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mehla.zenflow.MainActivity;
import com.mehla.zenflow.ui.login.Login;
import com.mehla.zenflow.services.DarkMode;
import com.mehla.zenflow.databinding.FragmentSettingsBinding;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class SettingsFragment extends Fragment {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    FirebaseAuth mAuth;
    FirebaseUser user;
    Button btnLogout;
    TextView userDetails;

    private @NonNull FragmentSettingsBinding binding;
    private Switch switchDarkMode;

    private ImageView profilePic;

    DarkMode dm;

    private DatabaseReference databaseRef;
    private ValueEventListener valueEventListener;

    private ActivityResultLauncher<String> imagePickerLauncher =
        registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        profilePic.setImageURI(result);

                        // Upload the image to Firebase Storage
                        uploadImageToFirebase(result);
                    }
                }
            });

    private void uploadImageToFirebase(Uri imageUri) {
        // Create a reference to the file in Firebase Storage
        StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

        // Upload the file to Firebase Storage
        UploadTask uploadTask = imageRef.putFile(imageUri);

        // Register a listener to track the upload progress
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    // Image uploaded successfully
                    Toast.makeText(getContext(), "Profile Picture Uploaded successfully", Toast.LENGTH_SHORT).show();
                    // Retrieve the download URL of the uploaded image
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            // Handle the download URL as needed
                            String imageUrl = downloadUri.toString();
                            // You can save the imageUrl to a database or use it directly
                            // Store the image URL in Firebase Realtime Database
                            String imageKey = databaseRef.push().getKey();
                            databaseRef.child(imageKey).setValue(imageUrl)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Image URL successfully stored in the database
                                            Toast.makeText(getContext(), "Url stored successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle the failure case
                                            Toast.makeText(getContext(), "Url stored failure", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                } else {
                    // Handle the error case
                    Exception exception = task.getException();
                    // Handle the error accordingly
                }
            }
        });
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        databaseRef = FirebaseDatabase.getInstance().getReference("images");

        // Retrieve the image URL from Firebase Realtime Database
        // Query the database to get the latest uploaded image
        Query query = databaseRef.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String imageUrl = snapshot.getValue(String.class);
                        // Display the image URL in the ImageView
                        Picasso.get().load(imageUrl).into(profilePic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error case
            }
        });



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

        profilePic = binding.profilePic;

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });

        return root;
    }

    private void openImageSelector() {
        imagePickerLauncher.launch("image/*");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Update Option Menu
        ((MainActivity) getActivity()).updateMenu();
    }
}