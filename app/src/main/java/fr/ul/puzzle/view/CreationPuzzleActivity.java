package fr.ul.puzzle.view;

import android.content.Intent;
import android.graphics.Bitmap;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import fr.ul.puzzle.R;
import fr.ul.puzzle.model.Piece;
import fr.ul.puzzle.model.Puzzle;
import fr.ul.puzzle.model.TypeDecoupage;
import fr.ul.puzzle.utils.FileUtils;
import fr.ul.puzzle.utils.GridUtils;
import fr.ul.puzzle.utils.PuzzleGenerator;

public class CreationPuzzleActivity extends AppCompatActivity {

    private EditText etNomPuzzle;
    private Spinner spNbPieces;
    private Spinner spTypeDecoupage;
    private Button btnChoisirImage;
    private Button btnGenererPuzzle;
    private ImageView ivApercuImage;

    private Uri imageSelectionneeUri;
    private Button btnPrendrePhoto;
    private File fichierPhotoCamera;

    private final ActivityResultLauncher<Intent> galerieLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            imageSelectionneeUri = result.getData().getData();

                            if (imageSelectionneeUri != null) {
                                ivApercuImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                ivApercuImage.setAdjustViewBounds(true);
                                ivApercuImage.setImageURI(imageSelectionneeUri);                            }
                        }
                    }
            );
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {

                            if (fichierPhotoCamera != null && fichierPhotoCamera.exists()) {
                                imageSelectionneeUri = Uri.fromFile(fichierPhotoCamera);
                                ivApercuImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                ivApercuImage.setAdjustViewBounds(true);
                                ivApercuImage.setImageURI(imageSelectionneeUri);                            }
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initialiserVues() {
        etNomPuzzle = findViewById(R.id.etNomPuzzle);
        spNbPieces = findViewById(R.id.spNbPieces);        spTypeDecoupage = findViewById(R.id.spTypeDecoupage);
        btnChoisirImage = findViewById(R.id.btnChoisirImage);
        btnPrendrePhoto = findViewById(R.id.btnPrendrePhoto);
        btnGenererPuzzle = findViewById(R.id.btnGenererPuzzle);
        ivApercuImage = findViewById(R.id.ivApercuImage);
    }

    private void initialiserSpinner() {
        String[] nombresPieces = {"16", "32", "64", "128"};
        ArrayAdapter<String> adapterNbPieces = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nombresPieces
        );
        adapterNbPieces.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spNbPieces.setAdapter(adapterNbPieces);

        String[] typesDecoupage = {"DROIT", "POLYGONAL", "ARRONDI"};
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                typesDecoupage
        );
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTypeDecoupage.setAdapter(adapterType);
    }

    private void initialiserListeners() {
        btnChoisirImage.setOnClickListener(v -> ouvrirGalerie());

        btnPrendrePhoto.setOnClickListener(v -> ouvrirCamera());

        btnGenererPuzzle.setOnClickListener(v -> genererPuzzle());
    }

    private void ouvrirGalerie() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galerieLauncher.launch(intent);
    }

    private void genererPuzzle() {
        String nomPuzzle = etNomPuzzle.getText().toString().trim();
        String nbPiecesTexte = spNbPieces.getSelectedItem().toString();        String typeChoisi = spTypeDecoupage.getSelectedItem().toString();



        if (nomPuzzle.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir un nom pour le puzzle", Toast.LENGTH_SHORT).show();
            return;
        }



        if (imageSelectionneeUri == null) {
            Toast.makeText(this, "Veuillez choisir une image", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int nbPiecesSouhaite = Integer.parseInt(nbPiecesTexte);

            Bitmap bitmapOriginal = chargerBitmapDepuisUri(imageSelectionneeUri);
            if (bitmapOriginal == null) {
                Toast.makeText(this, "Impossible de lire l'image", Toast.LENGTH_SHORT).show();
                return;
            }

            int largeurImage = bitmapOriginal.getWidth();
            int hauteurImage = bitmapOriginal.getHeight();

            int[] grille = GridUtils.calculerGrille(largeurImage, hauteurImage, nbPiecesSouhaite);
            int nbLignes = grille[0];
            int nbColonnes = grille[1];

            File dossierBase = getExternalFilesDir("puzzles");
            if (dossierBase == null) {
                Toast.makeText(this, "Erreur dossier de stockage", Toast.LENGTH_SHORT).show();
                return;
            }

            File dossierPuzzle = FileUtils.creerDossierPuzzle(dossierBase, nomPuzzle);

            File fichierImageOriginale = new File(dossierPuzzle, "image_originale.png");
            sauvegarderBitmap(bitmapOriginal, fichierImageOriginale);

            TypeDecoupage typeDecoupage = TypeDecoupage.valueOf(typeChoisi);

            Puzzle puzzle = new Puzzle(
                    1,
                    nomPuzzle,
                    fichierImageOriginale.getAbsolutePath(),
                    largeurImage,
                    hauteurImage,
                    nbLignes,
                    nbColonnes,
                    typeDecoupage
            );

            List<Piece> pieces = PuzzleGenerator.genererPieces(
                    bitmapOriginal,
                    nbLignes,
                    nbColonnes,
                    dossierPuzzle,
                    typeDecoupage
            );

            for (Piece piece : pieces) {
                puzzle.ajouterPiece(piece);
            }

            Toast.makeText(
                    this,
                    "Puzzle généré avec succès : " + pieces.size() + " pièces",
                    Toast.LENGTH_LONG
            ).show();

            Intent intent = new Intent(CreationPuzzleActivity.this, JeuPuzzleActivity.class);
            intent.putExtra("dossierPuzzle", dossierPuzzle.getAbsolutePath());
            intent.putExtra("nbLignes", nbLignes);
            intent.putExtra("nbColonnes", nbColonnes);
            intent.putExtra("largeurImage", largeurImage);
            intent.putExtra("hauteurImage", hauteurImage);
            getSharedPreferences("puzzle_save", MODE_PRIVATE).edit().clear().apply();
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la génération du puzzle", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap chargerBitmapDepuisUri(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        if (inputStream != null) {
            inputStream.close();
        }

        return bitmap;
    }

    private void sauvegarderBitmap(Bitmap bitmap, File fichier) throws Exception {
        FileOutputStream outputStream = new FileOutputStream(fichier);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outputStream.flush();
        outputStream.close();
    }



    private void ouvrirCamera() {
        try {
            File dossierTemp = getExternalFilesDir("temp");
            if (dossierTemp != null && !dossierTemp.exists()) {
                dossierTemp.mkdirs();
            }

            fichierPhotoCamera = new File(dossierTemp, "photo_hd.png");

            Uri photoUri = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    fichierPhotoCamera
            );

            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);

            cameraLauncher.launch(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur ouverture caméra", Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}