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
import android.util.DisplayMetrics;
import android.view.View;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.ul.puzzle.R;
import fr.ul.puzzle.model.PositionCase;


public class JeuPuzzleActivity extends AppCompatActivity {

    private GridLayout gridPieces;
    private TextView tvTitreJeu;
    private int nbLignes;
    private int nbColonnes;
    private GridLayout gridZonePuzzle;
    private ImageView pieceSelectionnee = null;
    private int largeurImage;
    private int hauteurImage;

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
        largeurImage = getIntent().getIntExtra("largeurImage", 1);
        hauteurImage = getIntent().getIntExtra("hauteurImage", 1);

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

                String nomFichier = fichierPiece.getName();
                String numero = nomFichier.replace("piece_", "").replace(".png", "");
                int indexPiece = Integer.parseInt(numero) - 1;

                int ligneCorrecte = indexPiece / nbColonnes;
                int colonneCorrecte = indexPiece % nbColonnes;

                PositionCase positionCorrecte = new PositionCase(ligneCorrecte, colonneCorrecte);
                imageView.setTag(positionCorrecte);

                imageView.setOnClickListener(v -> {
                    if (pieceSelectionnee != null) {
                        pieceSelectionnee.setAlpha(1.0f);
                    }

                    pieceSelectionnee = imageView;
                    pieceSelectionnee.setAlpha(0.5f);
                });

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.setGravity(Gravity.FILL_HORIZONTAL);

                imageView.setPadding(4, 4, 4, 4);
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

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int largeurDisponible = metrics.widthPixels - dpVersPx(32);

        int largeurCase = largeurDisponible / nbColonnes;
        int hauteurCase = (largeurCase * hauteurImage * nbColonnes) / (largeurImage * nbLignes);

        for (int ligne = 0; ligne < nbLignes; ligne++) {
            for (int colonne = 0; colonne < nbColonnes; colonne++) {

                ImageView caseVide = new ImageView(this);
                caseVide.setImageDrawable(null);
                caseVide.setScaleType(ImageView.ScaleType.FIT_XY);
                caseVide.setBackgroundResource(R.drawable.case_puzzle_vide);
                caseVide.setPadding(dpVersPx(2), dpVersPx(2), dpVersPx(2), dpVersPx(2));

                PositionCase positionCase = new PositionCase(ligne, colonne);
                caseVide.setTag(R.id.tag_position_case, positionCase);
                caseVide.setTag(R.id.tag_piece_placee, null);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = hauteurCase;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.setMargins(1, 1, 1, 1);

                caseVide.setLayoutParams(params);

                caseVide.setOnClickListener(v -> {
                    ImageView pieceDansLaCase = (ImageView) caseVide.getTag(R.id.tag_piece_placee);

                    if (pieceSelectionnee != null) {
                        if (pieceDansLaCase != null) {
                            pieceDansLaCase.setVisibility(View.VISIBLE);
                            pieceDansLaCase.setAlpha(1.0f);
                        }

                        caseVide.setImageDrawable(pieceSelectionnee.getDrawable());
                        caseVide.setTag(R.id.tag_piece_placee, pieceSelectionnee);

                        PositionCase positionCorrecte = (PositionCase) pieceSelectionnee.getTag();
                        PositionCase positionCouranteCase = (PositionCase) caseVide.getTag(R.id.tag_position_case);

                        if (positionCorrecte != null && positionCouranteCase != null
                                && positionCorrecte.getLigne() == positionCouranteCase.getLigne()
                                && positionCorrecte.getColonne() == positionCouranteCase.getColonne()) {

                            caseVide.setBackgroundResource(R.drawable.case_puzzle_correct);
                        } else {

                            caseVide.setBackgroundResource(R.drawable.case_puzzle_faux);                        }

                        pieceSelectionnee.setVisibility(View.GONE);
                        pieceSelectionnee.setAlpha(1.0f);
                        pieceSelectionnee = null;

                    } else {
                        if (pieceDansLaCase != null) {
                            pieceDansLaCase.setVisibility(View.VISIBLE);
                            pieceDansLaCase.setAlpha(1.0f);

                            caseVide.setImageDrawable(null);
                            caseVide.setTag(R.id.tag_piece_placee, null);
                            caseVide.setBackgroundResource(R.drawable.case_puzzle_vide);
                                                    }
                    }
                });

                gridZonePuzzle.addView(caseVide);
            }
        }
    }
        private int dpVersPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

}