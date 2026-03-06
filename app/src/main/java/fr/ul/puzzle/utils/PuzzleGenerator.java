package fr.ul.puzzle.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import fr.ul.puzzle.model.Piece;

public class PuzzleGenerator {

    public static List<Piece> genererPieces(Bitmap imageSource,
                                            int nbLignes,
                                            int nbColonnes,
                                            File dossierPuzzle) throws Exception {

        List<Piece> pieces = new ArrayList<>();

        int largeurImage = imageSource.getWidth();
        int hauteurImage = imageSource.getHeight();

        int largeurPiece = largeurImage / nbColonnes;
        int hauteurPiece = hauteurImage / nbLignes;

        int idPiece = 1;

        for (int ligne = 0; ligne < nbLignes; ligne++) {
            for (int colonne = 0; colonne < nbColonnes; colonne++) {

                int x = colonne * largeurPiece;
                int y = ligne * hauteurPiece;

                int largeurReelle = largeurPiece;
                int hauteurReelle = hauteurPiece;

                if (colonne == nbColonnes - 1) {
                    largeurReelle = largeurImage - x;
                }

                if (ligne == nbLignes - 1) {
                    hauteurReelle = hauteurImage - y;
                }

                Bitmap morceau = Bitmap.createBitmap(
                        imageSource,
                        x,
                        y,
                        largeurReelle,
                        hauteurReelle
                );

                String nomFichier = "piece_" + idPiece + ".png";
                File fichierPiece = new File(dossierPuzzle, nomFichier);

                sauvegarderBitmap(morceau, fichierPiece);

                Piece piece = new Piece(
                        idPiece,
                        ligne,
                        colonne,
                        0,
                        fichierPiece.getAbsolutePath()
                );

                pieces.add(piece);
                idPiece++;
            }
        }

        return pieces;
    }

    private static void sauvegarderBitmap(Bitmap bitmap, File fichier) throws Exception {
        FileOutputStream outputStream = new FileOutputStream(fichier);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outputStream.flush();
        outputStream.close();
    }
}