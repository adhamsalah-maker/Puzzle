package fr.ul.puzzle.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.ul.puzzle.R;
import fr.ul.puzzle.model.PolygonPiece;
import fr.ul.puzzle.utils.PolygonPuzzleGenerator;

public class JeuPuzzlePolygonalActivity extends AppCompatActivity {

    private FrameLayout zonePieces;
    private FrameLayout zoneReconstruction;

    private String cheminDossierPuzzle;
    private int nbLignes;
    private int nbColonnes;
    private int largeurImage;
    private int hauteurImage;

    private final List<ImageView> imagesPieces = new ArrayList<>();
    private final List<PolygonPiece> piecesPolygonales = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu_puzzle_polygonal);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        zonePieces = findViewById(R.id.zonePiecesPolygonales);
        zoneReconstruction = findViewById(R.id.zoneReconstructionPolygonale);

        cheminDossierPuzzle = getIntent().getStringExtra("dossierPuzzle");
        nbLignes = getIntent().getIntExtra("nbLignes", 4);
        nbColonnes = getIntent().getIntExtra("nbColonnes", 4);
        largeurImage = getIntent().getIntExtra("largeurImage", 1);
        hauteurImage = getIntent().getIntExtra("hauteurImage", 1);

        zonePieces.post(() -> {
            afficherPiecesPolygonalesMelangees();
            afficherZonesPolygonales();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void afficherPiecesPolygonalesMelangees() {
        if (cheminDossierPuzzle == null) {
            return;
        }

        File dossierPuzzle = new File(cheminDossierPuzzle);
        File[] fichiersPieces = dossierPuzzle.listFiles((dir, name) ->
                name.startsWith("piece_") && name.endsWith(".png")
        );

        if (fichiersPieces == null || fichiersPieces.length == 0) {
            return;
        }

        List<File> listePieces = new ArrayList<>(Arrays.asList(fichiersPieces));
        Collections.shuffle(listePieces);

        zonePieces.removeAllViews();
        imagesPieces.clear();

        int largeurZone = zonePieces.getWidth();
        int hauteurZone = zonePieces.getHeight();

        int nbColonnesAffichage = Math.max(1, nbColonnes);
        int nbLignesAffichage = (int) Math.ceil((double) listePieces.size() / nbColonnesAffichage);

        int largeurCase = largeurZone / nbColonnesAffichage;
        int hauteurCase = hauteurZone / Math.max(1, nbLignesAffichage);

        int index = 0;

        for (File fichierPiece : listePieces) {
            Bitmap bitmap = BitmapFactory.decodeFile(fichierPiece.getAbsolutePath());

            if (bitmap != null) {
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(bitmap);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                int ligne = index / nbColonnesAffichage;
                int colonne = index % nbColonnesAffichage;

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        largeurCase,
                        hauteurCase
                );
                params.leftMargin = colonne * largeurCase;
                params.topMargin = ligne * hauteurCase;

                imageView.setLayoutParams(params);

                zonePieces.addView(imageView);
                imagesPieces.add(imageView);

                index++;
            }
        }
    }

    private void afficherZonesPolygonales() {
        zoneReconstruction.removeAllViews();

        int largeurZone = zoneReconstruction.getWidth();
        int hauteurZone = zoneReconstruction.getHeight();

        int largeurCase = largeurZone / nbColonnes;
        int hauteurCase = hauteurZone / nbLignes;

        int idZone = 1;

        for (int ligne = 0; ligne < nbLignes; ligne++) {
            for (int colonne = 0; colonne < nbColonnes; colonne++) {

                // triangle 1
                FrameLayout zone1 = new FrameLayout(this);
                FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
                        largeurCase,
                        hauteurCase
                );
                params1.leftMargin = colonne * largeurCase;
                params1.topMargin = ligne * hauteurCase;
                zone1.setLayoutParams(params1);
                zone1.setBackgroundColor(Color.parseColor("#22FF0000"));
                zoneReconstruction.addView(zone1);
                idZone++;

                // triangle 2
                FrameLayout zone2 = new FrameLayout(this);
                FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(
                        largeurCase,
                        hauteurCase
                );
                params2.leftMargin = colonne * largeurCase;
                params2.topMargin = ligne * hauteurCase;
                zone2.setLayoutParams(params2);
                zone2.setBackgroundColor(Color.TRANSPARENT);
                zoneReconstruction.addView(zone2);
                idZone++;
            }
        }
    }
}