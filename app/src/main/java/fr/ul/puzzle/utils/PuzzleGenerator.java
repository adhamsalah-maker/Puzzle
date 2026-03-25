package fr.ul.puzzle.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

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

                Bitmap morceauRectangulaire = Bitmap.createBitmap(
                        imageSource,
                        x,
                        y,
                        largeurReelle,
                        hauteurReelle
                );

                Bitmap morceauFinal;

                switch (typeDecoupage) {
                    case POLYGONAL:
                        morceauFinal = creerPiecePolygonale(morceauRectangulaire);
                        break;

                    case ARRONDI:
                        morceauFinal = creerPieceArrondie(morceauRectangulaire);
                        break;

                    case DROIT:
                    default:
                        morceauFinal = morceauRectangulaire;
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

    private static Bitmap creerPiecePolygonale(Bitmap source) {
        int largeur = source.getWidth();
        int hauteur = source.getHeight();

        Bitmap resultat = Bitmap.createBitmap(largeur, hauteur, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultat);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Path path = new Path();

        float dx = largeur * 0.22f;
        float dy = hauteur * 0.18f;

        // Forme polygonale bien visible
        path.moveTo(dx, 0);                        // haut-gauche intérieur
        path.lineTo(largeur - dx, 0);             // haut-droite intérieur
        path.lineTo(largeur, dy);                 // pointe droite haute
        path.lineTo(largeur - dx * 0.6f, hauteur / 2f); // milieu droit
        path.lineTo(largeur, hauteur - dy);       // pointe droite basse
        path.lineTo(largeur - dx, hauteur);       // bas-droite intérieur
        path.lineTo(dx, hauteur);                 // bas-gauche intérieur
        path.lineTo(0, hauteur - dy);             // pointe gauche basse
        path.lineTo(dx * 0.6f, hauteur / 2f);     // milieu gauche
        path.lineTo(0, dy);                       // pointe gauche haute
        path.close();

        canvas.drawPath(path, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);

        return resultat;
    }
    private static Bitmap creerPieceArrondie(Bitmap source) {
        int largeur = source.getWidth();
        int hauteur = source.getHeight();

        Bitmap resultat = Bitmap.createBitmap(largeur, hauteur, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultat);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Path path = new Path();

        float rayon = Math.min(largeur, hauteur) * 0.18f;
        path.addRoundRect(0, 0, largeur, hauteur, rayon, rayon, Path.Direction.CW);

        canvas.drawPath(path, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);

        return resultat;
    }

    private static void sauvegarderBitmap(Bitmap bitmap, File fichier) throws Exception {
        FileOutputStream outputStream = new FileOutputStream(fichier);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outputStream.flush();
        outputStream.close();
    }
}