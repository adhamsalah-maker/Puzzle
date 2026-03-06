package fr.ul.puzzle.view;

import android.content.Intent;
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

import fr.ul.puzzle.R;

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

        btnGenererPuzzle.setOnClickListener(v -> {
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

            Toast.makeText(
                    this,
                    "Puzzle : " + nomPuzzle + " | Pièces : " + nbPiecesTexte + " | Type : " + typeChoisi,
                    Toast.LENGTH_LONG
            ).show();
        });
    }

    private void ouvrirGalerie() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galerieLauncher.launch(intent);
    }
}