package fr.ul.puzzle.cutting;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class PolygonalCutter {

    // top, right, bottom, left
    // 0 = bord plat
    // 1 = bosse vers l'extérieur
    // -1 = creux vers l'intérieur
    public Bitmap decouperPiece(Bitmap imageSource,
                                int x,
                                int y,
                                int largeur,
                                int hauteur,
                                int top,
                                int right,
                                int bottom,
                                int left) {

        int margeX = (int) (largeur * 0.22f);
        int margeY = (int) (hauteur * 0.22f);

        int xSource = Math.max(0, x - margeX);
        int ySource = Math.max(0, y - margeY);

        int largeurSource = Math.min(imageSource.getWidth() - xSource, largeur + 2 * margeX);
        int hauteurSource = Math.min(imageSource.getHeight() - ySource, hauteur + 2 * margeY);

        Bitmap morceau = Bitmap.createBitmap(
                imageSource,
                xSource,
                ySource,
                largeurSource,
                hauteurSource
        );

        Bitmap resultat = Bitmap.createBitmap(largeurSource, hauteurSource, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultat);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Path path = new Path();

        float gauche = x - xSource;
        float haut = y - ySource;
        float droite = gauche + largeur;
        float bas = haut + hauteur;

        float encocheX = largeur * 0.18f;
        float encocheY = hauteur * 0.18f;

        float milieuX = (gauche + droite) / 2f;
        float milieuY = (haut + bas) / 2f;

        path.moveTo(gauche, haut);

        // HAUT
        ajouterCoteHorizontal(path, gauche, droite, haut, milieuX, encocheX, encocheY, top, true);

        // DROITE
        ajouterCoteVertical(path, haut, bas, droite, milieuY, encocheX, encocheY, right, true);

        // BAS
        ajouterCoteHorizontalInverse(path, droite, gauche, bas, milieuX, encocheX, encocheY, bottom);

        // GAUCHE
        ajouterCoteVerticalInverse(path, bas, haut, gauche, milieuY, encocheX, encocheY, left);

        path.close();

        canvas.drawPath(path, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(morceau, 0, 0, paint);

        return resultat;
    }

    private void ajouterCoteHorizontal(Path path,
                                       float gauche,
                                       float droite,
                                       float y,
                                       float milieuX,
                                       float encocheX,
                                       float encocheY,
                                       int type,
                                       boolean haut) {

        if (type == 0) {
            path.lineTo(droite, y);
            return;
        }

        path.lineTo(milieuX - encocheX, y);

        if (type == 1) {
            // bosse extérieure
            path.lineTo(milieuX, haut ? y - encocheY : y + encocheY);
        } else {
            // creux intérieur
            path.lineTo(milieuX, haut ? y + encocheY : y - encocheY);
        }

        path.lineTo(milieuX + encocheX, y);
        path.lineTo(droite, y);
    }

    private void ajouterCoteHorizontalInverse(Path path,
                                              float droite,
                                              float gauche,
                                              float y,
                                              float milieuX,
                                              float encocheX,
                                              float encocheY,
                                              int type) {

        if (type == 0) {
            path.lineTo(gauche, y);
            return;
        }

        path.lineTo(milieuX + encocheX, y);

        if (type == 1) {
            path.lineTo(milieuX, y + encocheY);
        } else {
            path.lineTo(milieuX, y - encocheY);
        }

        path.lineTo(milieuX - encocheX, y);
        path.lineTo(gauche, y);
    }

    private void ajouterCoteVertical(Path path,
                                     float haut,
                                     float bas,
                                     float x,
                                     float milieuY,
                                     float encocheX,
                                     float encocheY,
                                     int type,
                                     boolean droite) {

        if (type == 0) {
            path.lineTo(x, bas);
            return;
        }

        path.lineTo(x, milieuY - encocheY);

        if (type == 1) {
            path.lineTo(droite ? x + encocheX : x - encocheX, milieuY);
        } else {
            path.lineTo(droite ? x - encocheX : x + encocheX, milieuY);
        }

        path.lineTo(x, milieuY + encocheY);
        path.lineTo(x, bas);
    }

    private void ajouterCoteVerticalInverse(Path path,
                                            float bas,
                                            float haut,
                                            float x,
                                            float milieuY,
                                            float encocheX,
                                            float encocheY,
                                            int type) {

        if (type == 0) {
            path.lineTo(x, haut);
            return;
        }

        path.lineTo(x, milieuY + encocheY);

        if (type == 1) {
            path.lineTo(x - encocheX, milieuY);
        } else {
            path.lineTo(x + encocheX, milieuY);
        }

        path.lineTo(x, milieuY - encocheY);
        path.lineTo(x, haut);
    }
}