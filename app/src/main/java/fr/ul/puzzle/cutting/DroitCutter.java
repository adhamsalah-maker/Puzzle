package fr.ul.puzzle.cutting;

import android.graphics.Bitmap;

public class DroitCutter {

    public Bitmap decouperPiece(Bitmap imageSource, int x, int y, int largeur, int hauteur) {
        return Bitmap.createBitmap(imageSource, x, y, largeur, hauteur);
    }
}