package com.example.magic8ball;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    private String[] responses;
    private Random random;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final float SHAKE_THRESHOLD_G = 2.5f;

    private Button askButton;
    private Button showHistoryButton;
    private ImageView ballImage;
    private TextView answerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responses      = getResources().getStringArray(R.array.magic8_responses);
        random         = new Random();

        askButton         = findViewById(R.id.askButton);
        showHistoryButton = findViewById(R.id.showHistoryButton);
        ballImage         = findViewById(R.id.ballImage);
        answerTextView    = findViewById(R.id.answerTextView);

        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askButton.setEnabled(false);
                answerTextView.setText("");

                ballImage.animate()
                        .translationYBy(-300f)
                        .setDuration(400)
                        .setInterpolator(new BounceInterpolator())
                        .withEndAction(() ->
                                ballImage.animate()
                                        .translationY(0f)
                                        .setDuration(400)
                                        .setInterpolator(new BounceInterpolator())
                                        .withEndAction(() -> {
                                            int idx = random.nextInt(responses.length);
                                            String answer = responses[idx];
                                            answerTextView.setText(answer);
                                            askButton.setEnabled(true);

                                            // Save answer in SharedPreferences
                                            SharedPreferences prefs = getSharedPreferences("history", MODE_PRIVATE);
                                            String oldHistory = prefs.getString("answers_history", "");
                                            String updatedHistory = oldHistory.isEmpty() ? answer : oldHistory + "," + answer;
                                            prefs.edit().putString("answers_history", updatedHistory).apply();
                                        })
                                        .start()
                        )
                        .start();
            }
        });

        showHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        // Shake detection setup
        sensorManager  = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer  = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0],
                y = event.values[1],
                z = event.values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;
        float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD_G) {
            sensorManager.unregisterListener(this);
            askButton.performClick();
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI),
                    1000
            );
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
