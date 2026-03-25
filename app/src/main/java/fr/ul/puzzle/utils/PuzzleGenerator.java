package fr.ul.puzzle.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import fr.ul.puzzle.cutting.ArrondiCutter;
import fr.ul.puzzle.cutting.DroitCutter;
import fr.ul.puzzle.cutting.PolygonalCutter;
import fr.ul.puzzle.model.Piece;
import fr.ul.puzzle.model.TypeDecoupage;

public class PuzzleGenerator {

    public static List<Piece> genererPieces(Bitmap imageSource,
                                            int nbLignes,
                                            int nbColonnes,
                                            File dossierPuzzle,
                                            TypeDecoupage typeDecoupage) throws Exception {

        List<Piece> pieces = new ArrayList<>();

        int largeurImage = imageSource.getWidth();
        int hauteurImage = imageSource.getHeight();

        int largeurPiece = largeurImage / nbColonnes;
        int hauteurPiece = hauteurImage / nbLignes;

        int[][] bordsVerticaux = new int[nbLignes][Math.max(0, nbColonnes - 1)];
        int[][] bordsHorizontaux = new int[Math.max(0, nbLignes - 1)][nbColonnes];

// Génération cohérente des bords partagés
        for (int ligne = 0; ligne < nbLignes; ligne++) {
            for (int colonne = 0; colonne < nbColonnes - 1; colonne++) {
                bordsVerticaux[ligne][colonne] = ((ligne + colonne) % 2 == 0) ? 1 : -1;
            }
        }

        for (int ligne = 0; ligne < nbLignes - 1; ligne++) {
            for (int colonne = 0; colonne < nbColonnes; colonne++) {
                bordsHorizontaux[ligne][colonne] = ((ligne + colonne) % 2 == 0) ? -1 : 1;
            }
        }

        int idPiece = 1;

        DroitCutter droitCutter = new DroitCutter();
        PolygonalCutter polygonalCutter = new PolygonalCutter();
        ArrondiCutter arrondiCutter = new ArrondiCutter();

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

                Bitmap morceauFinal;

                switch (typeDecoupage) {
                    case POLYGONAL:
                        int top = (ligne == 0) ? 0 : -bordsHorizontaux[ligne - 1][colonne];
                        int right = (colonne == nbColonnes - 1) ? 0 : bordsVerticaux[ligne][colonne];
                        int bottom = (ligne == nbLignes - 1) ? 0 : bordsHorizontaux[ligne][colonne];
                        int left = (colonne == 0) ? 0 : -bordsVerticaux[ligne][colonne - 1];

                        morceauFinal = polygonalCutter.decouperPiece(
                                imageSource,
                                x,
                                y,
                                largeurReelle,
                                hauteurReelle,
                                top,
                                right,
                                bottom,
                                left
                        );
                        break;

                    case ARRONDI:
                        morceauFinal = arrondiCutter.decouperPiece(
                                imageSource, x, y, largeurReelle, hauteurReelle
                        );
                        break;

                    case DROIT:
                    default:
                        morceauFinal = droitCutter.decouperPiece(
                                imageSource, x, y, largeurReelle, hauteurReelle
                        );
                        break;
                }

                String nomFichier = "piece_" + idPiece + ".png";
                File fichierPiece = new File(dossierPuzzle, nomFichier);

                sauvegarderBitmap(morceauFinal, fichierPiece);

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