package fr.ul.puzzle.cutting;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class PolygonalCutter {

    public Bitmap decouperPiece(Bitmap imageSource, int x, int y, int largeur, int hauteur) {
        Bitmap morceau = Bitmap.createBitmap(imageSource, x, y, largeur, hauteur);

        Bitmap resultat = Bitmap.createBitmap(largeur, hauteur, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultat);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Path path = new Path();

        float dx = largeur * 0.22f;
        float dy = hauteur * 0.18f;

        path.moveTo(dx, 0);
        path.lineTo(largeur - dx, 0);
        path.lineTo(largeur, dy);
        path.lineTo(largeur - dx * 0.6f, hauteur / 2f);
        path.lineTo(largeur, hauteur - dy);
        path.lineTo(largeur - dx, hauteur);
        path.lineTo(dx, hauteur);
        path.lineTo(0, hauteur - dy);
        path.lineTo(dx * 0.6f, hauteur / 2f);
        path.lineTo(0, dy);
        path.close();

        canvas.drawPath(path, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(morceau, 0, 0, paint);

        return resultat;
    }
}