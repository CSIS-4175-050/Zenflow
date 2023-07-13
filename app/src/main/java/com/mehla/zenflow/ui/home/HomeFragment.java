package com.mehla.zenflow.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mehla.zenflow.R;
import com.mehla.zenflow.databinding.FragmentHomeBinding;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Random random;

    static TextView dayQuote;
    Button btnRefreshQuote;

    static ArrayList<String> quotes;

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

        return root;
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

}