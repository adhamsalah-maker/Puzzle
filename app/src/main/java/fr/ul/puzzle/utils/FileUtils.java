package fr.ul.puzzle.utils;

import java.io.File;

public class FileUtils {

    public static File creerDossierPuzzle(File dossierBase, String nomPuzzle) {
        String nomNettoye = nomPuzzle.trim().replaceAll("[^a-zA-Z0-9_-]", "_");
        File dossierPuzzle = new File(dossierBase, nomNettoye);

        if (dossierPuzzle.exists()) {
            viderDossier(dossierPuzzle);
        } else {
            dossierPuzzle.mkdirs();
        }

        return dossierPuzzle;
    }

    private static void viderDossier(File dossier) {
        File[] fichiers = dossier.listFiles();

        if (fichiers != null) {
            for (File fichier : fichiers) {
                if (fichier.isDirectory()) {
                    viderDossier(fichier);
                }
                fichier.delete();
            }
        }
    }
}