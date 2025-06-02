package com.example.magic8ball;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class HistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView historyList = findViewById(R.id.historyList);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("history", MODE_PRIVATE);
        String historyString = prefs.getString("answers_history", "");
        ArrayList<String> history = new ArrayList<>();
        if (!historyString.isEmpty()) {
            String[] split = historyString.split(",");
            history = new ArrayList<>(Arrays.asList(split));
        } else {
            history.add("No answers yet!");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                history
        );
        historyList.setAdapter(adapter);
    }
}
