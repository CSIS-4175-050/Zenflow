package com.mehla.zenflow.ui.timer;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.TextView;

import com.mehla.zenflow.databinding.ActivityTimerBinding;
import com.mehla.zenflow.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Timer extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;

    private TextView timerTextView;
    private Button startButton;
    private Button stopButton;
    private Button resetButton;
    private Button increaseButton;
    private Button decreaseButton;

    private long initialTime = 5 * 60 * 1000; // 5 minutes in milliseconds
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };
    private ActivityTimerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTimerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mVisible = true;
        mControlsView = binding.fullscreenContentControls;
        mContentView = binding.timerTextView;

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        // binding.cancelTimer.setOnTouchListener(mDelayHideTouchListener);

        binding.cancelTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        timerTextView = binding.timerTextView;
        startButton = binding.startButton;
        stopButton = binding.stopButton;
        resetButton = binding.resetButton;
        increaseButton = binding.increaseButton;
        decreaseButton = binding.decreaseButton;

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseTimerByOneMinute();
            }
        });

        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseTimerByOneMinute();
            }
        });

        resetTimer();
    }

    private void startTimer() {
        if (!isTimerRunning) {
            countDownTimer = new CountDownTimer(initialTime, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    initialTime = millisUntilFinished;
                    updateTimerText(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    isTimerRunning = false;
                    updateTimerText(0);
                    stopButton.setVisibility(View.GONE);
                    resetButton.setVisibility(View.VISIBLE);
                }
            };

            countDownTimer.start();
            isTimerRunning = true;
            stopButton.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.GONE);
            increaseButton.setEnabled(false);
            decreaseButton.setEnabled(false);
        }
    }

    private void stopTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel();
            isTimerRunning = false;
            stopButton.setVisibility(View.GONE);
            resetButton.setVisibility(View.VISIBLE);
            increaseButton.setEnabled(true);
            decreaseButton.setEnabled(true);
        }
    }

    private void resetTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel();
        }

        initialTime = 5 * 60 * 1000; // 5 minutes in milliseconds
        updateTimerText(initialTime);
        stopButton.setVisibility(View.GONE);
        resetButton.setVisibility(View.GONE);
        increaseButton.setEnabled(true);
        decreaseButton.setEnabled(true);
    }

    private void increaseTimerByOneMinute() {
        if (!isTimerRunning) {
            initialTime += 1 * 60 * 1000; // Increase by 1 minute
            updateTimerText(initialTime);
        }
    }

    private void decreaseTimerByOneMinute() {
        if (!isTimerRunning && initialTime >= 2 * 60 * 1000) {
            initialTime -= 1 * 60 * 1000; // Decrease by 1 minute
            updateTimerText(initialTime);
        }
    }

    private void updateTimerText(long milliseconds) {
        int minutes = (int) (milliseconds / 1000) / 60;
        int seconds = (int) (milliseconds / 1000) % 60;
        String time = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(time);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}