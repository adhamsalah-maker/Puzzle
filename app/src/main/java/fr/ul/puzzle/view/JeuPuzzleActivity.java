package fr.ul.puzzle.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import fr.ul.puzzle.R;

public class JeuPuzzleActivity extends AppCompatActivity {

    private LinearLayout layoutPieces;
    private TextView tvTitreJeu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeu_puzzle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layoutPieces = findViewById(R.id.layoutPieces);
        tvTitreJeu = findViewById(R.id.tvTitreJeu);

        String cheminDossierPuzzle = getIntent().getStringExtra("dossierPuzzle");

        if (cheminDossierPuzzle != null) {
            afficherPieces(cheminDossierPuzzle);
        } else {
            tvTitreJeu.setText("Aucun puzzle reçu");
        }
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

        Arrays.sort(fichiersPieces, Comparator.comparing(File::getName));

        tvTitreJeu.setText("Pièces générées : " + fichiersPieces.length);

        for (File fichierPiece : fichiersPieces) {
            Bitmap bitmap = BitmapFactory.decodeFile(fichierPiece.getAbsolutePath());

            if (bitmap != null) {
                TextView nomPiece = new TextView(this);
                nomPiece.setText(fichierPiece.getName());
                nomPiece.setTextSize(16f);
                nomPiece.setPadding(0, 16, 0, 8);

                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(bitmap);
                imageView.setAdjustViewBounds(true);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

                layoutPieces.addView(nomPiece);
                layoutPieces.addView(imageView);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}