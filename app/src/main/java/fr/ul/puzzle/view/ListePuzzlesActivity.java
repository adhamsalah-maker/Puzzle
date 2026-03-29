package fr.ul.puzzle.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.ul.puzzle.R;
import android.view.Menu;
import android.view.MenuItem;

public class ListePuzzlesActivity extends AppCompatActivity {

    private ListView listViewPuzzles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.appliquerTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_puzzles);

        View root = findViewById(android.R.id.content);
        root.setPadding(0, 120, 0, 0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewPuzzles = findViewById(R.id.listViewPuzzles);

        chargerPuzzles();

        getWindow().getDecorView().setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(0, insets.getSystemWindowInsetTop(), 0, 0);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_theme, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_help) {
            AideUtils.afficherAide(
                    this,
                    "Aide - Mes puzzles",
                    "Cet écran affiche les puzzles terminés.\n\n" +
                            "- Touchez un puzzle pour l'ouvrir en mode terminé.\n" +
                            "- Le temps réalisé peut être affiché.\n" +
                            "- Vous pouvez supprimer un puzzle si nécessaire."
            );
            return true;
        }

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

    private void supprimerDossier(File dossier) {
        if (dossier.isDirectory()) {
            File[] fichiers = dossier.listFiles();
            if (fichiers != null) {
                for (File file : fichiers) {
                    supprimerDossier(file);
                }
            }
        }
        dossier.delete();
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 0, nomsPuzzles) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.item_puzzle, parent, false);
                }

                TextView tvNom = convertView.findViewById(R.id.tvNomPuzzle);
                TextView tvTemps = convertView.findViewById(R.id.tvTempsPuzzle);
                ImageView img = convertView.findViewById(R.id.imgPuzzle);
                ImageButton btnSupprimer = convertView.findViewById(R.id.btnSupprimerPuzzle);
                TextView tvScore = convertView.findViewById(R.id.tvScorePuzzle);

                File dossier = fichiersPuzzles.get(position);

                // nom du puzzle
                tvNom.setText(dossier.getName());

                // temps
                File fichierTemps = new File(dossier, "temps.txt");
                String temps = "";

                if (fichierTemps.exists()) {
                    try {
                        java.util.Scanner scanner = new java.util.Scanner(fichierTemps);
                        if (scanner.hasNextLine()) {
                            temps = scanner.nextLine();
                        }
                        scanner.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                tvTemps.setText(temps);

                File fichierScore = new File(dossier, "score.txt");
                String score = "0";

                if (fichierScore.exists()) {
                    try {
                        java.util.Scanner scanner = new java.util.Scanner(fichierScore);
                        if (scanner.hasNextLine()) {
                            score = scanner.nextLine();
                        }
                        scanner.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                tvScore.setText("Score : " + score);

                // image du puzzle
                File fichierImage = new File(dossier, "image_originale.png");
                if (fichierImage.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(fichierImage.getAbsolutePath());
                    img.setImageBitmap(bitmap);
                } else {
                    img.setImageDrawable(null);
                }

                // suppression
                btnSupprimer.setFocusable(false);
                btnSupprimer.setOnClickListener(v -> {
                    supprimerDossier(dossier);
                    fichiersPuzzles.remove(position);
                    nomsPuzzles.remove(position);
                    notifyDataSetChanged();
                });

                // clic sur la ligne -> ouvrir puzzle terminé
                convertView.setOnClickListener(v -> {
                    Intent intent = new Intent(ListePuzzlesActivity.this, JeuPuzzleActivity.class);
                    intent.putExtra("dossierPuzzle", dossier.getAbsolutePath());
                    intent.putExtra("modeReprise", false);
                    intent.putExtra("modeTermine", true);
                    startActivity(intent);
                });

                return convertView;
            }
        };

        listViewPuzzles.setAdapter(adapter);
    }}