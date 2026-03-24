package fr.ul.puzzle.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.ul.puzzle.R;

public class ListePuzzlesActivity extends AppCompatActivity {

    private ListView listViewPuzzles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_puzzles);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewPuzzles = findViewById(R.id.listViewPuzzles);

        chargerPuzzles();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void chargerPuzzles() {

        File dossierPuzzles = getExternalFilesDir("puzzles");

        if (dossierPuzzles == null || !dossierPuzzles.exists()) {
            return;
        }

        File[] dossiers = dossierPuzzles.listFiles(File::isDirectory);

        List<String> nomsPuzzles = new ArrayList<>();
        List<File> fichiersPuzzles = new ArrayList<>();

        if (dossiers != null) {
            for (File dossier : dossiers) {
                File fichierTermine = new File(dossier, "termine.txt");

                if (fichierTermine.exists()) {
                    nomsPuzzles.add(dossier.getName());
                    fichiersPuzzles.add(dossier);
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                nomsPuzzles
        );

        listViewPuzzles.setAdapter(adapter);

        listViewPuzzles.setOnItemClickListener((parent, view, position, id) -> {

            File dossierSelectionne = fichiersPuzzles.get(position);

            Intent intent = new Intent(ListePuzzlesActivity.this, JeuPuzzleActivity.class);
            intent.putExtra("dossierPuzzle", dossierSelectionne.getAbsolutePath());
            intent.putExtra("modeReprise", false);
            intent.putExtra("modeTermine", true);
            startActivity(intent);
        });
    }
}