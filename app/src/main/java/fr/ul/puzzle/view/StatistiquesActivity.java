package fr.ul.puzzle.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import fr.ul.puzzle.R;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.view.Menu;
import android.view.MenuItem;

public class StatistiquesActivity extends AppCompatActivity {

    private TextView tvNbPuzzlesTermines;
    private TextView tvMeilleurTemps;
    private TextView tvMeilleurScore;
    private TextView tvTempsMoyen;
    private TextView tvScoreMoyen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.appliquerTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistiques);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvNbPuzzlesTermines = findViewById(R.id.tvNbPuzzlesTermines);
        tvMeilleurTemps = findViewById(R.id.tvMeilleurTemps);
        tvMeilleurScore = findViewById(R.id.tvMeilleurScore);
        tvTempsMoyen = findViewById(R.id.tvTempsMoyen);
        tvScoreMoyen = findViewById(R.id.tvScoreMoyen);
        chargerStatistiques();
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void chargerStatistiques() {

        File dossierPuzzles = getExternalFilesDir("puzzles");

        if (dossierPuzzles == null || !dossierPuzzles.exists()) {
            return;
        }

        File[] dossiers = dossierPuzzles.listFiles(File::isDirectory);

        if (dossiers == null) return;

        int nbTermines = 0;

        int meilleurScore = 0;
        int totalScore = 0;

        int meilleurTempsSecondes = Integer.MAX_VALUE;
        int totalTempsSecondes = 0;

        for (File dossier : dossiers) {

            File fichierTermine = new File(dossier, "termine.txt");

            if (!fichierTermine.exists()) continue;

            nbTermines++;

            // === TEMPS ===
            File fichierTemps = new File(dossier, "temps.txt");
            int tempsSecondes = lireTempsEnSecondes(fichierTemps);

            if (tempsSecondes > 0) {
                totalTempsSecondes += tempsSecondes;
                if (tempsSecondes < meilleurTempsSecondes) {
                    meilleurTempsSecondes = tempsSecondes;
                }
            }

            // === SCORE ===
            File fichierScore = new File(dossier, "score.txt");
            int score = lireScore(fichierScore);

            if (score > 0) {
                totalScore += score;
                if (score > meilleurScore) {
                    meilleurScore = score;
                }
            }
        }

        afficherResultats(nbTermines, meilleurTempsSecondes, meilleurScore, totalTempsSecondes, totalScore);
    }

    private int lireTempsEnSecondes(File fichierTemps) {
        try {
            if (!fichierTemps.exists()) return 0;

            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(fichierTemps));
            String temps = reader.readLine();
            reader.close();

            if (temps == null || !temps.contains(":")) return 0;

            String[] parts = temps.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int secondes = Integer.parseInt(parts[1]);

            return minutes * 60 + secondes;

        } catch (Exception e) {
            return 0;
        }
    }

    private int lireScore(File fichierScore) {
        try {
            if (!fichierScore.exists()) return 0;

            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(fichierScore));
            String scoreStr = reader.readLine();
            reader.close();

            return Integer.parseInt(scoreStr);

        } catch (Exception e) {
            return 0;
        }
    }

    private void afficherResultats(int nb, int meilleurTemps, int meilleurScore, int totalTemps, int totalScore) {

        tvNbPuzzlesTermines.setText(String.valueOf(nb));
        if (nb == 0) return;

        // meilleur temps
        if (meilleurTemps != Integer.MAX_VALUE) {
            tvMeilleurScore.setText(String.valueOf(meilleurScore));
        }

        tvMeilleurScore.setText("Meilleur score : " + meilleurScore);

        // moyenne
        int tempsMoyen = totalTemps / nb;
        int scoreMoyen = totalScore / nb;

        tvTempsMoyen.setText(formatTemps(tempsMoyen));
        tvScoreMoyen.setText(String.valueOf(scoreMoyen));
    }

    private String formatTemps(int secondesTotal) {
        int minutes = secondesTotal / 60;
        int secondes = secondesTotal % 60;
        return String.format("%02d:%02d", minutes, secondes);
    }





}
