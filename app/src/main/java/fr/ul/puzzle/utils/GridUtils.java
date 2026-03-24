package fr.ul.puzzle.utils;

public class GridUtils {

    public static int[] calculerGrille(int largeurImage, int hauteurImage, int nbPiecesSouhaite) {

        boolean imagePaysage = largeurImage >= hauteurImage;

        switch (nbPiecesSouhaite) {
            case 16:
                return new int[]{4, 4};

            case 32:
                if (imagePaysage) {
                    return new int[]{4, 8};
                } else {
                    return new int[]{8, 4};
                }

            case 64:
                return new int[]{8, 8};

            case 128:
                if (imagePaysage) {
                    return new int[]{8, 16};
                } else {
                    return new int[]{16, 8};
                }

            default:
                return new int[]{4, 4};
        }
    }
}