package fr.ul.puzzle.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import fr.ul.puzzle.R;

import android.content.Intent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class PartiesActivity extends AppCompatActivity {

    private ListView listViewParties;
    private final List<File> fichiersParties = new ArrayList<>();
    private final List<String> nomsParties = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parties);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewParties = findViewById(R.id.listViewParties);

        chargerListeParties();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void chargerListeParties() {
        File dossierParties = getExternalFilesDir("parties");

        if (dossierParties == null || !dossierParties.exists()) {
            Toast.makeText(this, "Aucune partie sauvegardée", Toast.LENGTH_SHORT).show();
            return;
        }

        File[] fichiers = dossierParties.listFiles((dir, name) ->
                name.endsWith(".txt")
        );

        if (fichiers == null || fichiers.length == 0) {
            Toast.makeText(this, "Aucune partie sauvegardée", Toast.LENGTH_SHORT).show();
            return;
        }

        Arrays.sort(fichiers, Comparator.comparing(File::getName).reversed());

        fichiersParties.clear();
        nomsParties.clear();

        for (File fichier : fichiers) {
            fichiersParties.add(fichier);
            nomsParties.add(fichier.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                nomsParties
        );

        listViewParties.setAdapter(adapter);

        listViewParties.setOnItemClickListener((parent, view, position, id) -> {
            File fichierSelectionne = fichiersParties.get(position);
            Map<String, String> donnees = lireFichierPartie(fichierSelectionne);

            String cheminDossierPuzzle = donnees.get("cheminDossierPuzzle");

            if (cheminDossierPuzzle == null || cheminDossierPuzzle.isEmpty()) {
                Toast.makeText(this, "Partie invalide", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(PartiesActivity.this, JeuPuzzleActivity.class);
            intent.putExtra("dossierPuzzle", cheminDossierPuzzle);
            intent.putExtra("nbLignes", Integer.parseInt(donnees.get("nbLignes")));
            intent.putExtra("nbColonnes", Integer.parseInt(donnees.get("nbColonnes")));
            intent.putExtra("largeurImage", Integer.parseInt(donnees.get("largeurImage")));
            intent.putExtra("hauteurImage", Integer.parseInt(donnees.get("hauteurImage")));
            startActivity(intent);
        });
    }

    private Map<String, String> lireFichierPartie(File fichier) {
        Map<String, String> donnees = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fichier));
            String ligne;

            while ((ligne = reader.readLine()) != null) {
                String[] morceaux = ligne.split("=", 2);
                if (morceaux.length == 2) {
                    donnees.put(morceaux[0], morceaux[1]);
                }
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return donnees;
    }
}