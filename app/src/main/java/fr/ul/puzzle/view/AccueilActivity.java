package fr.ul.puzzle.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import fr.ul.puzzle.R;

public class AccueilActivity extends AppCompatActivity {

    private Button btnCreerPuzzle;
    private Button btnMesPuzzles;
    private Button btnMesParties;
    private Button btnStatistiques;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.appliquerTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        btnCreerPuzzle = findViewById(R.id.btnCreerPuzzle);
        btnMesPuzzles = findViewById(R.id.btnMesPuzzles);
        btnMesParties = findViewById(R.id.btnMesParties);
        btnStatistiques = findViewById(R.id.btnStatistiques);

        btnCreerPuzzle.setOnClickListener(v -> {
            Intent intent = new Intent(AccueilActivity.this, CreationPuzzleActivity.class);
            startActivity(intent);
        });

        btnMesPuzzles.setOnClickListener(v -> {
            Intent intent = new Intent(AccueilActivity.this, ListePuzzlesActivity.class);
            startActivity(intent);
        });

        btnMesParties.setOnClickListener(v -> {
            Intent intent = new Intent(AccueilActivity.this, PartiesActivity.class);
            startActivity(intent);
        });

        btnStatistiques.setOnClickListener(v -> {
            Intent intent = new Intent(AccueilActivity.this, StatistiquesActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_theme, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_theme) {
            ThemeUtils.basculerTheme(this);
            recreate();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}