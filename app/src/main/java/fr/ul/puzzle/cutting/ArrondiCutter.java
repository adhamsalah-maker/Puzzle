package fr.ul.puzzle.cutting;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class ArrondiCutter {

    public Bitmap decouperPiece(Bitmap imageSource, int x, int y, int largeur, int hauteur) {
        Bitmap morceau = Bitmap.createBitmap(imageSource, x, y, largeur, hauteur);

        Bitmap resultat = Bitmap.createBitmap(largeur, hauteur, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultat);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Path path = new Path();

        float rayon = Math.min(largeur, hauteur) * 0.18f;
        path.addRoundRect(0, 0, largeur, hauteur, rayon, rayon, Path.Direction.CW);

        canvas.drawPath(path, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(morceau, 0, 0, paint);

        return resultat;
    }
}