package fr.ul.puzzle.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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

import android.content.ClipData;
import android.view.DragEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import android.widget.ProgressBar;
import android.widget.Toast;

public class JeuPuzzleActivity extends AppCompatActivity {

    private GridLayout gridPieces;
    private TextView tvTitreJeu;
    private int nbLignes;
    private int nbColonnes;
    private GridLayout gridZonePuzzle;
    private ImageView pieceSelectionnee = null;
    private int largeurImage;
    private int hauteurImage;
    private android.widget.Button btnRotationGauche;
    private android.widget.Button btnRotationDroite;
    private ProgressBar progressBarPuzzle;
    private TextView tvProgressionPourcentage;
    private android.widget.Button btnAide;
    private TextView tvNbAides;
    private int nbAidesRestantes = 3;
    private String cheminDossierPuzzle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu_puzzle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gridPieces = findViewById(R.id.gridPieces);
        gridZonePuzzle = findViewById(R.id.gridZonePuzzle);
        tvTitreJeu = findViewById(R.id.tvTitreJeu);

        progressBarPuzzle = findViewById(R.id.progressBarPuzzle);
        tvProgressionPourcentage = findViewById(R.id.tvProgressionPourcentage);
        btnRotationGauche = findViewById(R.id.btnRotationGauche);
        btnRotationDroite = findViewById(R.id.btnRotationDroite);

        Button btnApercu = findViewById(R.id.btnApercu);
        btnRotationGauche.setOnClickListener(v -> tournerPieceSelectionnee(-90));
        btnRotationDroite.setOnClickListener(v -> tournerPieceSelectionnee(90));
        btnApercu.setOnClickListener(v -> afficherApercuModele());
        cheminDossierPuzzle = getIntent().getStringExtra("dossierPuzzle");        tvNbAides = findViewById(R.id.tvNbAides);
        tvNbAides.setText(String.valueOf(nbAidesRestantes));
        btnAide = findViewById(R.id.btnAide);
        btnAide.setOnClickListener(v -> {
            if (nbAidesRestantes > 0) {
                utiliserAide();
                nbAidesRestantes--;

                tvNbAides.setText(String.valueOf(nbAidesRestantes));

                if (nbAidesRestantes == 0) {
                    btnAide.setEnabled(false);
                    btnAide.setAlpha(0.5f);
                }
            }
        });



        nbLignes = getIntent().getIntExtra("nbLignes", 1);
        nbColonnes = getIntent().getIntExtra("nbColonnes", 2);
        largeurImage = getIntent().getIntExtra("largeurImage", 1);
        hauteurImage = getIntent().getIntExtra("hauteurImage", 1);

        gridPieces.setColumnCount(nbColonnes);
        gridPieces.setRowCount(nbLignes);

        gridZonePuzzle.setColumnCount(nbColonnes);
        gridZonePuzzle.setRowCount(nbLignes);

        gridPieces.setClipChildren(true);
        gridPieces.setClipToPadding(true);

        gridZonePuzzle.setClipChildren(true);
        gridZonePuzzle.setClipToPadding(true);
        progressBarPuzzle.setMax(100);
        progressBarPuzzle.setProgress(0);
        tvProgressionPourcentage.setText("0%");

        if (cheminDossierPuzzle != null) {
            gridZonePuzzle.post(() -> {
                afficherZoneVide();
                afficherPieces(cheminDossierPuzzle);
            });
        } else {
            afficherZoneVide();
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

// rotation correcte attendue
                imageView.setTag(R.id.tag_rotation_cible, 0);

                int[] rotationsPossibles = {0, 90, 180, 270};
                int rotationInitiale = rotationsPossibles[(int) (Math.random() * 4)];

                imageView.setTag(R.id.tag_rotation_piece, rotationInitiale);
                imageView.setRotation(rotationInitiale);


                configurerDragPourPiece(imageView);

                imageView.setOnClickListener(v -> {
                    if (pieceSelectionnee != null) {
                        pieceSelectionnee.setAlpha(1.0f);
                    }

                    pieceSelectionnee = imageView;
                    pieceSelectionnee.setAlpha(0.5f);
                });

                int largeurDisponible = gridPieces.getWidth();
                int hauteurDisponible = gridPieces.getHeight();

                int largeurCase = largeurDisponible / nbColonnes;
                int hauteurCase = hauteurDisponible / nbLignes;

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = largeurCase;
                params.height = hauteurCase;
                params.setMargins(2, 2, 2, 2);

                FrameLayout conteneurPiece = new FrameLayout(this);
                conteneurPiece.setLayoutParams(params);
                conteneurPiece.setClipChildren(true);
                conteneurPiece.setClipToPadding(true);
                conteneurPiece.setPadding(2, 2, 2, 2);
                FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );

                imageView.setLayoutParams(imageParams);
                imageView.setAdjustViewBounds(false);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                conteneurPiece.addView(imageView);
                gridPieces.addView(conteneurPiece);
            }
        }
    }
    private void afficherZoneVide() {
        gridZonePuzzle.removeAllViews();

        int largeurDisponible = gridZonePuzzle.getWidth();
        int hauteurDisponible = gridZonePuzzle.getHeight();

        int largeurCase = largeurDisponible / nbColonnes;
        int hauteurCase = hauteurDisponible / nbLignes;

        for (int ligne = 0; ligne < nbLignes; ligne++) {
            for (int colonne = 0; colonne < nbColonnes; colonne++) {

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = largeurCase;
                params.height = hauteurCase;
                params.setMargins(2, 2, 2, 2);

                FrameLayout caseVide = new FrameLayout(this);
                caseVide.setLayoutParams(params);
                caseVide.setPadding(dpVersPx(4), dpVersPx(4), dpVersPx(4), dpVersPx(4));
                caseVide.setBackgroundResource(R.drawable.case_puzzle_vide);

                PositionCase positionCase = new PositionCase(ligne, colonne);
                caseVide.setTag(R.id.tag_position_case, positionCase);
                caseVide.setTag(R.id.tag_piece_placee, null);

                ImageView fondCase = new ImageView(this);
                fondCase.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));
                fondCase.setScaleType(ImageView.ScaleType.FIT_XY);
                fondCase.setBackgroundColor(0x00FFFFFF);

                caseVide.addView(fondCase);

                configurerDropPourCase(caseVide);

                caseVide.setOnClickListener(v -> {
                    ImageView pieceDansLaCase = (ImageView) caseVide.getTag(R.id.tag_piece_placee);

                    if (pieceSelectionnee != null) {

                        if (pieceDansLaCase != null) {
                            remettrePieceDansGrille(pieceDansLaCase, caseVide, fondCase);
                        }

                        retirerPieceDeSonParent(pieceSelectionnee);

                        FrameLayout.LayoutParams pieceParams = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                        );
                        pieceSelectionnee.setLayoutParams(pieceParams);
                        pieceSelectionnee.setScaleType(ImageView.ScaleType.FIT_XY);

                        caseVide.addView(pieceSelectionnee);
                        caseVide.setTag(R.id.tag_piece_placee, pieceSelectionnee);

                        ImageView piecePlacee = pieceSelectionnee;
                        piecePlacee.setOnClickListener(v2 -> remettrePieceDansGrille(piecePlacee, caseVide, fondCase));

                        if (estPieceBienPlacee(pieceSelectionnee, caseVide)) {
                            caseVide.setBackgroundResource(R.drawable.case_puzzle_correct);
                        } else {
                            caseVide.setBackgroundResource(R.drawable.case_puzzle_faux);
                        }

                        pieceSelectionnee.setAlpha(1.0f);
                        pieceSelectionnee = null;

                        mettreAJourProgression();
                        verifierVictoire();

                    } else {
                        if (pieceDansLaCase != null) {
                            remettrePieceDansGrille(pieceDansLaCase, caseVide, fondCase);
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

    private void verifierVictoire() {

        boolean puzzleTermine = true;

        for (int i = 0; i < gridZonePuzzle.getChildCount(); i++) {

            FrameLayout casePuzzle = (FrameLayout) gridZonePuzzle.getChildAt(i);
            ImageView piece = (ImageView) casePuzzle.getTag(R.id.tag_piece_placee);

            if (piece == null || !estPieceBienPlacee(piece, casePuzzle)) {
                puzzleTermine = false;
                break;
            }
        }

        if (puzzleTermine) {
            afficherVictoire();
        }
    }

    private void mettreAJourProgression() {
        int nbPiecesBienPlacees = 0;
        int nbTotalCases = gridZonePuzzle.getChildCount();

        for (int i = 0; i < nbTotalCases; i++) {
            FrameLayout casePuzzle = (FrameLayout) gridZonePuzzle.getChildAt(i);
            ImageView piece = (ImageView) casePuzzle.getTag(R.id.tag_piece_placee);

            if (piece != null && estPieceBienPlacee(piece, casePuzzle)) {
                nbPiecesBienPlacees++;
            }
        }

        int progression = 0;
        if (nbTotalCases > 0) {
            progression = (nbPiecesBienPlacees * 100) / nbTotalCases;
        }

        progressBarPuzzle.setProgress(progression);
        tvProgressionPourcentage.setText(progression + "%");
    }

    private void afficherVictoire() {

        new AlertDialog.Builder(this)
                .setTitle("Puzzle terminé")
                .setMessage("Bravo ! Vous avez réussi le puzzle.")
                .setPositiveButton("OK", null)
                .show();
    }

    private boolean estPieceBienPlacee(ImageView piece, FrameLayout casePuzzle) {
        if (piece == null || casePuzzle == null) {
            return false;
        }

        PositionCase positionCorrecte = (PositionCase) piece.getTag();
        PositionCase positionCase = (PositionCase) casePuzzle.getTag(R.id.tag_position_case);

        Integer rotationCourante = (Integer) piece.getTag(R.id.tag_rotation_piece);
        Integer rotationCible = (Integer) piece.getTag(R.id.tag_rotation_cible);

        if (positionCorrecte == null || positionCase == null
                || rotationCourante == null || rotationCible == null) {
            return false;
        }

        boolean bonnePosition =
                positionCorrecte.getLigne() == positionCase.getLigne()
                        && positionCorrecte.getColonne() == positionCase.getColonne();

        boolean bonneRotation = rotationCourante.equals(rotationCible);

        return bonnePosition && bonneRotation;
    }

    private void configurerDragPourPiece(ImageView pieceView) {
        pieceView.setOnLongClickListener(v -> {
            ClipData data = ClipData.newPlainText("", "");

            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);

            v.startDragAndDrop(data, shadowBuilder, v, 0);

            v.setVisibility(View.INVISIBLE); // 🔥 important

            return true;
        });
    }


    private void configurerDropPourCase(FrameLayout caseContainer) {
        caseContainer.setOnDragListener((v, event) -> {
            switch (event.getAction()) {

                case DragEvent.ACTION_DRAG_STARTED:
                    return true;

                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setScaleX(1.05f);
                    v.setScaleY(1.05f);
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    v.setScaleX(1f);
                    v.setScaleY(1f);
                    return true;

                case DragEvent.ACTION_DROP:
                    v.setScaleX(1f);
                    v.setScaleY(1f);

                    View pieceDragged = (View) event.getLocalState();
                    FrameLayout targetCase = (FrameLayout) v;
                    ImageView fondCase = (ImageView) targetCase.getChildAt(0);

                    // enlever la pièce de son ancien parent
                    retirerPieceDeSonParent((ImageView) pieceDragged);

                    // si une pièce existe déjà dans la case, on l’enlève et on la remet en haut
                    if (targetCase.getChildCount() > 1) {
                        ImageView anciennePiece = (ImageView) targetCase.getChildAt(1);
                        remettrePieceDansGrille(anciennePiece, targetCase, fondCase);
                    }

                    FrameLayout.LayoutParams pieceParams = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                    );
                    pieceDragged.setLayoutParams(pieceParams);

                    if (pieceDragged instanceof ImageView) {
                        ((ImageView) pieceDragged).setScaleType(ImageView.ScaleType.FIT_XY);
                    }

                    // ajouter la nouvelle pièce dans la case
                    targetCase.addView(pieceDragged);
                    targetCase.setTag(R.id.tag_piece_placee, pieceDragged);

                    ImageView piecePlacee = (ImageView) pieceDragged;
                    piecePlacee.setOnClickListener(v2 ->
                            remettrePieceDansGrille(piecePlacee, targetCase, fondCase)
                    );

                    // vérifier si la pièce est bien placée
                    PositionCase positionCorrecte = (PositionCase) pieceDragged.getTag();
                    PositionCase positionCase = (PositionCase) targetCase.getTag(R.id.tag_position_case);

                    if (estPieceBienPlacee(piecePlacee, targetCase)) {
                        targetCase.setBackgroundResource(R.drawable.case_puzzle_correct);
                    } else {
                        targetCase.setBackgroundResource(R.drawable.case_puzzle_faux);
                    }

                    mettreAJourProgression();
                    verifierVictoire();
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setScaleX(1f);
                    v.setScaleY(1f);

                    ((View) event.getLocalState()).setVisibility(View.VISIBLE);

                    return true;

                default:
                    return false;
            }
        });
    }
    private void remettrePieceDansGrille(ImageView piece, FrameLayout caseVide, ImageView fondCase) {
        caseVide.removeView(piece);

        int largeurDisponible = gridPieces.getWidth();
        int hauteurDisponible = gridPieces.getHeight();

        int largeurCase = largeurDisponible / nbColonnes;
        int hauteurCase = hauteurDisponible / nbLignes;

        GridLayout.LayoutParams paramsPiece = new GridLayout.LayoutParams();
        paramsPiece.width = largeurCase;
        paramsPiece.height = hauteurCase;
        paramsPiece.setMargins(2, 2, 2, 2);

        FrameLayout conteneurPiece = new FrameLayout(this);
        conteneurPiece.setLayoutParams(paramsPiece);
        conteneurPiece.setClipChildren(true);
        conteneurPiece.setClipToPadding(true);
        conteneurPiece.setPadding(2, 2, 2, 2);

        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        piece.setLayoutParams(imageParams);
        piece.setImageDrawable(piece.getDrawable());
        piece.setAdjustViewBounds(false);
        piece.setScaleType(ImageView.ScaleType.FIT_XY);
        piece.setVisibility(View.VISIBLE);
        piece.setAlpha(1.0f);

        // remettre le comportement normal de sélection
        piece.setOnClickListener(v -> {
            if (pieceSelectionnee != null) {
                pieceSelectionnee.setAlpha(1.0f);
            }

            pieceSelectionnee = piece;
            pieceSelectionnee.setAlpha(0.5f);
        });

        // remettre aussi le drag
        configurerDragPourPiece(piece);

        conteneurPiece.addView(piece);
        gridPieces.addView(conteneurPiece);

        caseVide.setTag(R.id.tag_piece_placee, null);
        caseVide.setBackgroundResource(R.drawable.case_puzzle_vide);

        mettreAJourProgression();
    }

    private void retirerPieceDeSonParent(ImageView piece) {
        if (piece.getParent() instanceof FrameLayout) {
            FrameLayout parentFrame = (FrameLayout) piece.getParent();

            if (parentFrame.getParent() == gridPieces) {
                // La pièce vient de la grille du haut
                parentFrame.removeView(piece);
                gridPieces.removeView(parentFrame);
            } else {
                // La pièce vient d'une case de reconstruction
                parentFrame.removeView(piece);
            }
        } else if (piece.getParent() instanceof ViewGroup) {
            ((ViewGroup) piece.getParent()).removeView(piece);
        }
    }



    private void tournerPieceSelectionnee(int angleAAjouter) {
        if (pieceSelectionnee == null) {
            return;
        }

        Integer rotationActuelle = (Integer) pieceSelectionnee.getTag(R.id.tag_rotation_piece);

        if (rotationActuelle == null) {
            rotationActuelle = 0;
        }

        int nouvelleRotation = (rotationActuelle + angleAAjouter + 360) % 360;

        pieceSelectionnee.setRotation(nouvelleRotation);
        pieceSelectionnee.setTag(R.id.tag_rotation_piece, nouvelleRotation);
    }

    private void utiliserAide() {

        for (int i = 0; i < gridZonePuzzle.getChildCount(); i++) {

            FrameLayout casePuzzle = (FrameLayout) gridZonePuzzle.getChildAt(i);
            ImageView pieceDejaPlacee = (ImageView) casePuzzle.getTag(R.id.tag_piece_placee);

            // On cherche une case vide
            if (pieceDejaPlacee == null) {

                PositionCase positionCase = (PositionCase) casePuzzle.getTag(R.id.tag_position_case);

                // Chercher la bonne pièce dans la grille du haut
                for (int j = 0; j < gridPieces.getChildCount(); j++) {

                    FrameLayout conteneur = (FrameLayout) gridPieces.getChildAt(j);

                    if (conteneur.getChildCount() > 0) {

                        ImageView piece = (ImageView) conteneur.getChildAt(0);
                        PositionCase positionCorrecte = (PositionCase) piece.getTag();

                        if (positionCorrecte != null &&
                                positionCorrecte.getLigne() == positionCase.getLigne() &&
                                positionCorrecte.getColonne() == positionCase.getColonne()) {

                            retirerPieceDeSonParent(piece);

                            // Mettre bonne rotation
                            piece.setRotation(0);
                            piece.setTag(R.id.tag_rotation_piece, 0);

                            // Ajouter dans la case
                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT
                            );

                            piece.setLayoutParams(params);
                            piece.setScaleType(ImageView.ScaleType.FIT_XY);

                            casePuzzle.addView(piece);
                            casePuzzle.setTag(R.id.tag_piece_placee, piece);

                            animerPieceAidee(piece);

                            ImageView fondCase = (ImageView) casePuzzle.getChildAt(0);
                            casePuzzle.setBackgroundResource(R.drawable.case_puzzle_correct);

                            piece.setOnClickListener(v ->
                                    remettrePieceDansGrille(piece, casePuzzle, fondCase)
                            );

                            mettreAJourProgression();
                            verifierVictoire();

                            return; // 💥 IMPORTANT → une seule pièce à la fois
                        }
                    }
                }
            }
        }
    }


    private void animerPieceAidee(ImageView piece) {
        piece.setAlpha(0f);
        piece.setScaleX(0.7f);
        piece.setScaleY(0.7f);

        piece.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .start();
    }

    private void afficherApercuModele() {
        if (cheminDossierPuzzle == null) {
            return;
        }

        File fichierImageOriginale = new File(cheminDossierPuzzle, "image_originale.png");

        if (!fichierImageOriginale.exists()) {
            Toast.makeText(this, "Image modèle introuvable", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeFile(fichierImageOriginale.getAbsolutePath());

        if (bitmap == null) {
            Toast.makeText(this, "Impossible de charger l’image modèle", Toast.LENGTH_SHORT).show();
            return;
        }

        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(20, 20, 20, 20);

        new AlertDialog.Builder(this)
                .setTitle("Aperçu du modèle")
                .setView(imageView)
                .setPositiveButton("Fermer", null)
                .show();
    }



}