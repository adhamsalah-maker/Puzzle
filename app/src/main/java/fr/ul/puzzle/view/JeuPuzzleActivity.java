package fr.ul.puzzle.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.ul.puzzle.R;

public class JeuPuzzleActivity extends AppCompatActivity {

    private GridLayout gridPieces;
    private TextView tvTitreJeu;

    private int nbLignes;
    private int nbColonnes;
    private GridLayout gridZonePuzzle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu_puzzle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridPieces = findViewById(R.id.gridPieces);
        gridZonePuzzle = findViewById(R.id.gridZonePuzzle);
        tvTitreJeu = findViewById(R.id.tvTitreJeu);

        String cheminDossierPuzzle = getIntent().getStringExtra("dossierPuzzle");

        nbLignes = getIntent().getIntExtra("nbLignes", 1);
        nbColonnes = getIntent().getIntExtra("nbColonnes", 2);

        gridPieces.setColumnCount(nbColonnes);
        gridZonePuzzle.setColumnCount(nbColonnes);
        afficherZoneVide();

        if (cheminDossierPuzzle != null) {
            afficherPieces(cheminDossierPuzzle);
        } else {
            tvTitreJeu.setText("Aucun puzzle reçu");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void afficherPieces(String cheminDossierPuzzle) {
        File dossierPuzzle = new File(cheminDossierPuzzle);

        if (!dossierPuzzle.exists() || !dossierPuzzle.isDirectory()) {
            tvTitreJeu.setText("Dossier puzzle introuvable");
            return;
        }

        File[] fichiersPieces = dossierPuzzle.listFiles((dir, name) ->
                name.startsWith("piece_") && name.endsWith(".png")
        );

        if (fichiersPieces == null || fichiersPieces.length == 0) {
            tvTitreJeu.setText("Aucune pièce trouvée");
            return;
        }

        List<File> listePieces = new ArrayList<>(Arrays.asList(fichiersPieces));
        Collections.shuffle(listePieces);

        tvTitreJeu.setText("Pièces mélangées : " + listePieces.size() + " (" + nbLignes + " x " + nbColonnes + ")");
        gridPieces.removeAllViews();

        for (File fichierPiece : listePieces) {
            Bitmap bitmap = BitmapFactory.decodeFile(fichierPiece.getAbsolutePath());

            if (bitmap != null) {
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(bitmap);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(8, 8, 8, 8);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.setGravity(Gravity.FILL_HORIZONTAL);

                imageView.setLayoutParams(params);
                imageView.setMinimumHeight((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        120,
                        getResources().getDisplayMetrics()
                ));

                gridPieces.addView(imageView);
            }
        }
    }

    private void afficherZoneVide() {
        gridZonePuzzle.removeAllViews();

        for (int i = 0; i < nbLignes * nbColonnes; i++) {
            TextView caseVide = new TextView(this);
            caseVide.setText(" ");
            caseVide.setMinHeight(180);
            caseVide.setBackgroundResource(android.R.drawable.editbox_background);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

            caseVide.setLayoutParams(params);
            gridZonePuzzle.addView(caseVide);
        }
    }
}