package fr.ul.puzzle.utils;

public class GridUtils {

    public static int[] calculerGrille(int largeurImage, int hauteurImage, int nbPiecesSouhaite) {
        double ratio = (double) largeurImage / hauteurImage;

        int nbColonnes = (int) Math.round(Math.sqrt(nbPiecesSouhaite * ratio));
        if (nbColonnes < 1) {
            nbColonnes = 1;
        }

        int nbLignes = (int) Math.round((double) nbPiecesSouhaite / nbColonnes);
        if (nbLignes < 1) {
            nbLignes = 1;
        }

        return new int[]{nbLignes, nbColonnes};
    }
}