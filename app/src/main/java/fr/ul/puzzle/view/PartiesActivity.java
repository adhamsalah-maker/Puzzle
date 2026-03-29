package fr.ul.puzzle.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ul.puzzle.R;
import fr.ul.puzzle.model.PartieSauvegardee;
import java.util.LinkedHashMap;
import android.view.Menu;
import android.view.MenuItem;

public class PartiesActivity extends AppCompatActivity {

    private ListView listViewParties;
    private final List<File> fichiersParties = new ArrayList<>();
    private final List<PartieSauvegardee> parties = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.appliquerTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parties);
        View root = findViewById(R.id.rootParties);
        root.setPadding(16, 120, 16, 16);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listViewParties = findViewById(R.id.listViewParties);

        chargerListeParties();
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

    private void chargerListeParties() {
        File dossierParties = getExternalFilesDir("parties");

        if (dossierParties == null || !dossierParties.exists()) {
            Toast.makeText(this, "Aucune partie sauvegardée", Toast.LENGTH_SHORT).show();
            return;
        }

        File[] fichiers = dossierParties.listFiles((dir, name) -> name.endsWith(".txt"));

        if (fichiers == null || fichiers.length == 0) {
            Toast.makeText(this, "Aucune partie sauvegardée", Toast.LENGTH_SHORT).show();
            return;
        }

        Arrays.sort(fichiers, Comparator.comparing(File::getName).reversed());

        fichiersParties.clear();
        parties.clear();

        LinkedHashMap<String, File> dernierFichierParPuzzle = new LinkedHashMap<>();

        for (File fichier : fichiers) {
            Map<String, String> donnees = lireFichierPartie(fichier);

            String cheminDossierPuzzle = donnees.get("cheminDossierPuzzle");
            if (cheminDossierPuzzle == null || cheminDossierPuzzle.isEmpty()) {
                continue;
            }

            File dossierPuzzle = new File(cheminDossierPuzzle);
            String nomPuzzle = dossierPuzzle.getName();

            // comme les fichiers sont déjà triés du plus récent au plus ancien,
            // on garde seulement le premier rencontré pour chaque puzzle
            if (!dernierFichierParPuzzle.containsKey(nomPuzzle)) {
                dernierFichierParPuzzle.put(nomPuzzle, fichier);
            }
        }

        for (String nomPuzzle : dernierFichierParPuzzle.keySet()) {
            File fichier = dernierFichierParPuzzle.get(nomPuzzle);
            Map<String, String> donnees = lireFichierPartie(fichier);

            String cheminDossierPuzzle = donnees.get("cheminDossierPuzzle");
            if (cheminDossierPuzzle == null || cheminDossierPuzzle.isEmpty()) {
                continue;
            }

            File dossierPuzzle = new File(cheminDossierPuzzle);
            File fichierImage = new File(dossierPuzzle, "image_originale.png");
            String cheminImage = fichierImage.exists() ? fichierImage.getAbsolutePath() : "";

            fichiersParties.add(fichier);
            parties.add(new PartieSauvegardee(
                    nomPuzzle,
                    fichier.getName(),
                    cheminImage,
                    cheminDossierPuzzle
            ));
        }

        listViewParties.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return parties.size();
            }

            @Override
            public Object getItem(int position) {
                return parties.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View vue = convertView;

                if (vue == null) {
                    vue = LayoutInflater.from(PartiesActivity.this)
                            .inflate(R.layout.item_partie, parent, false);
                }

                ImageView imgPuzzle = vue.findViewById(R.id.imgPuzzle);
                TextView tvNomPuzzle = vue.findViewById(R.id.tvNomPuzzle);
                TextView tvDatePartie = vue.findViewById(R.id.tvDatePartie);
                TextView tvProgressionPartie = vue.findViewById(R.id.tvProgressionPartie);
                ProgressBar progressBarPartie = vue.findViewById(R.id.progressBarPartie);
                ImageButton btnSupprimerPartie = vue.findViewById(R.id.btnSupprimerPartie);

                PartieSauvegardee partie = parties.get(position);
                File fichierPartie = fichiersParties.get(position);
                Map<String, String> donnees = lireFichierPartie(fichierPartie);

                vue.setOnClickListener(v -> {
                    File fichierSelectionne = fichiersParties.get(position);
                    Map<String, String> donneesPartie = lireFichierPartie(fichierSelectionne);

                    String cheminDossierPuzzle = donneesPartie.get("cheminDossierPuzzle");

                    if (cheminDossierPuzzle == null || cheminDossierPuzzle.isEmpty()) {
                        Toast.makeText(PartiesActivity.this, "Partie invalide", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(PartiesActivity.this, JeuPuzzleActivity.class);
                    intent.putExtra("dossierPuzzle", cheminDossierPuzzle);
                    intent.putExtra("nbLignes", Integer.parseInt(donneesPartie.get("nbLignes")));
                    intent.putExtra("nbColonnes", Integer.parseInt(donneesPartie.get("nbColonnes")));
                    intent.putExtra("largeurImage", Integer.parseInt(donneesPartie.get("largeurImage")));
                    intent.putExtra("hauteurImage", Integer.parseInt(donneesPartie.get("hauteurImage")));
                    intent.putExtra("modeReprise", true);
                    intent.putExtra("cheminFichierPartie", fichierSelectionne.getAbsolutePath());
                    startActivity(intent);
                });

                tvNomPuzzle.setText(partie.getNomPuzzle());

                String dateSauvegarde = donnees.get("date");
                String dateFormatee = formaterDateSauvegarde(dateSauvegarde);
                tvDatePartie.setText("Dernière sauvegarde : " + dateFormatee);
                int progression = calculerProgression(donnees);
                tvProgressionPartie.setText("Progression : " + progression + "%");
                progressBarPartie.setProgress(progression);

                btnSupprimerPartie.setOnClickListener(v -> {
                    new androidx.appcompat.app.AlertDialog.Builder(PartiesActivity.this)
                            .setTitle("Supprimer")
                            .setMessage("Voulez-vous supprimer cette partie ?")
                            .setPositiveButton("Oui", (dialog, which) -> {
                                File fichierASupprimer = fichiersParties.get(position);

                                if (fichierASupprimer.exists()) {
                                    fichierASupprimer.delete();
                                }

                                chargerListeParties();
                            })
                            .setNegativeButton("Non", null)
                            .show();
                });

                if (!partie.getCheminImage().isEmpty()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(partie.getCheminImage());
                    imgPuzzle.setImageBitmap(bitmap);
                } else {
                    imgPuzzle.setImageDrawable(null);
                }

                return vue;

            }



        });


    }

    private int calculerProgression(Map<String, String> donnees) {
        try {
            String etatCases = donnees.get("etat_cases");
            String nbLignesStr = donnees.get("nbLignes");
            String nbColonnesStr = donnees.get("nbColonnes");

            if (etatCases == null || nbLignesStr == null || nbColonnesStr == null) {
                return 0;
            }

            int nbLignes = Integer.parseInt(nbLignesStr);
            int nbColonnes = Integer.parseInt(nbColonnesStr);
            int totalPieces = nbLignes * nbColonnes;

            if (totalPieces == 0) {
                return 0;
            }

            if (etatCases.trim().isEmpty()) {
                return 0;
            }

            String[] placements = etatCases.split(";");
            int nbPiecesPlacees = 0;

            for (String placement : placements) {
                if (!placement.trim().isEmpty()) {
                    nbPiecesPlacees++;
                }
            }

            return (nbPiecesPlacees * 100) / totalPieces;

        } catch (Exception e) {
            return 0;
        }
    }

    private String formaterDateSauvegarde(String dateBrute) {
        try {
            if (dateBrute == null || dateBrute.isEmpty()) {
                return "--";
            }

            SimpleDateFormat formatEntree = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            SimpleDateFormat formatSortie = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm", Locale.getDefault());

            Date date = formatEntree.parse(dateBrute);

            if (date == null) {
                return dateBrute;
            }

            return formatSortie.format(date);

        } catch (Exception e) {
            return dateBrute;
        }
    }
}