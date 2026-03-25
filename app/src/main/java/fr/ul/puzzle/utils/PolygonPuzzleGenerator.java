package fr.ul.puzzle.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import fr.ul.puzzle.model.PolygonPiece;
import fr.ul.puzzle.model.PolygonZone;

public class PolygonPuzzleGenerator {

    public static class ResultatPolygonal {
        private final List<PolygonPiece> pieces;
        private final List<PolygonZone> zones;

        public ResultatPolygonal(List<PolygonPiece> pieces, List<PolygonZone> zones) {
            this.pieces = pieces;
            this.zones = zones;
        }

        public List<PolygonPiece> getPieces() {
            return pieces;
        }

        public List<PolygonZone> getZones() {
            return zones;
        }
    }

    public static ResultatPolygonal genererPuzzlePolygonal(Bitmap imageSource,
                                                           int nbLignes,
                                                           int nbColonnes,
                                                           File dossierPuzzle) throws Exception {

        List<PolygonPiece> pieces = new ArrayList<>();
        List<PolygonZone> zones = new ArrayList<>();

        int largeurImage = imageSource.getWidth();
        int hauteurImage = imageSource.getHeight();

        int largeurCase = largeurImage / nbColonnes;
        int hauteurCase = hauteurImage / nbLignes;

        int idPiece = 1;

        for (int ligne = 0; ligne < nbLignes; ligne++) {
            for (int colonne = 0; colonne < nbColonnes; colonne++) {

                int x = colonne * largeurCase;
                int y = ligne * hauteurCase;

                int largeurReelle = largeurCase;
                int hauteurReelle = hauteurCase;

                if (colonne == nbColonnes - 1) {
                    largeurReelle = largeurImage - x;
                }

                if (ligne == nbLignes - 1) {
                    hauteurReelle = hauteurImage - y;
                }

                PointF hautGauche = new PointF(0, 0);
                PointF hautDroite = new PointF(largeurReelle, 0);
                PointF basGauche = new PointF(0, hauteurReelle);
                PointF basDroite = new PointF(largeurReelle, hauteurReelle);

                // Triangle 1 : haut-gauche, haut-droite, bas-gauche
                PointF[] triangle1 = new PointF[]{
                        hautGauche, hautDroite, basGauche
                };

                Bitmap piece1 = decouperTriangle(
                        Bitmap.createBitmap(imageSource, x, y, largeurReelle, hauteurReelle),
                        triangle1
                );

                String nomFichier1 = "piece_" + idPiece + ".png";
                File fichierPiece1 = new File(dossierPuzzle, nomFichier1);
                sauvegarderBitmap(piece1, fichierPiece1);

                PolygonZone zone1 = new PolygonZone(idPiece, triangle1);
                PolygonPiece polygonPiece1 = new PolygonPiece(
                        idPiece,
                        triangle1,
                        fichierPiece1.getAbsolutePath(),
                        zone1.getId()
                );

                zones.add(zone1);
                pieces.add(polygonPiece1);
                idPiece++;

                // Triangle 2 : haut-droite, bas-droite, bas-gauche
                PointF[] triangle2 = new PointF[]{
                        hautDroite, basDroite, basGauche
                };

                Bitmap piece2 = decouperTriangle(
                        Bitmap.createBitmap(imageSource, x, y, largeurReelle, hauteurReelle),
                        triangle2
                );

                String nomFichier2 = "piece_" + idPiece + ".png";
                File fichierPiece2 = new File(dossierPuzzle, nomFichier2);
                sauvegarderBitmap(piece2, fichierPiece2);

                PolygonZone zone2 = new PolygonZone(idPiece, triangle2);
                PolygonPiece polygonPiece2 = new PolygonPiece(
                        idPiece,
                        triangle2,
                        fichierPiece2.getAbsolutePath(),
                        zone2.getId()
                );

                zones.add(zone2);
                pieces.add(polygonPiece2);
                idPiece++;
            }
        }

        return new ResultatPolygonal(pieces, zones);
    }

    private static Bitmap decouperTriangle(Bitmap source, PointF[] points) {
        int largeur = source.getWidth();
        int hauteur = source.getHeight();

        Bitmap resultat = Bitmap.createBitmap(largeur, hauteur, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultat);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Path path = new Path();

        path.moveTo(points[0].x, points[0].y);
        path.lineTo(points[1].x, points[1].y);
        path.lineTo(points[2].x, points[2].y);
        path.close();

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