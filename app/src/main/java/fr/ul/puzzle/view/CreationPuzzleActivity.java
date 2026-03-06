package fr.ul.puzzle.view;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

import fr.ul.puzzle.R;
import fr.ul.puzzle.utils.GridUtils;

public class CreationPuzzleActivity extends AppCompatActivity {

    private EditText etNomPuzzle;
    private EditText etNbPieces;
    private Spinner spTypeDecoupage;
    private Button btnChoisirImage;
    private Button btnGenererPuzzle;
    private ImageView ivApercuImage;

    private Uri imageSelectionneeUri;

    private final ActivityResultLauncher<Intent> galerieLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            imageSelectionneeUri = result.getData().getData();

                            if (imageSelectionneeUri != null) {
                                ivApercuImage.setImageURI(imageSelectionneeUri);
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_puzzle);

        initialiserVues();
        initialiserSpinner();
        initialiserListeners();
    }

    private void initialiserVues() {
        etNomPuzzle = findViewById(R.id.etNomPuzzle);
        etNbPieces = findViewById(R.id.etNbPieces);
        spTypeDecoupage = findViewById(R.id.spTypeDecoupage);
        btnChoisirImage = findViewById(R.id.btnChoisirImage);
        btnGenererPuzzle = findViewById(R.id.btnGenererPuzzle);
        ivApercuImage = findViewById(R.id.ivApercuImage);
    }

    private void initialiserSpinner() {
        String[] typesDecoupage = {"DROIT", "POLYGONAL", "ARRONDI"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                typesDecoupage
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTypeDecoupage.setAdapter(adapter);
    }

    private void initialiserListeners() {
        btnChoisirImage.setOnClickListener(v -> ouvrirGalerie());

        btnGenererPuzzle.setOnClickListener(v -> genererPuzzle());
    }

    private void ouvrirGalerie() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galerieLauncher.launch(intent);
    }

    private void genererPuzzle() {
        String nomPuzzle = etNomPuzzle.getText().toString().trim();
        String nbPiecesTexte = etNbPieces.getText().toString().trim();
        String typeChoisi = spTypeDecoupage.getSelectedItem().toString();

        if (nomPuzzle.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un nom pour le puzzle", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nbPiecesTexte.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un nombre de pièces", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageSelectionneeUri == null) {
            Toast.makeText(this, "Veuillez choisir une image", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int nbPiecesSouhaite = Integer.parseInt(nbPiecesTexte);

            InputStream inputStream = getContentResolver().openInputStream(imageSelectionneeUri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            if (inputStream != null) {
                inputStream.close();
            }

            int largeurImage = options.outWidth;
            int hauteurImage = options.outHeight;

            int[] grille = GridUtils.calculerGrille(largeurImage, hauteurImage, nbPiecesSouhaite);
            int nbLignes = grille[0];
            int nbColonnes = grille[1];

            Toast.makeText(
                    this,
                    "Puzzle \"" + nomPuzzle + "\"\nType : " + typeChoisi +
                            "\nImage : " + largeurImage + "x" + hauteurImage +
                            "\nGrille : " + nbLignes + " lignes x " + nbColonnes + " colonnes",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la génération du puzzle", Toast.LENGTH_SHORT).show();
        }
    }
}