package fr.ul.puzzle.utils;

import java.io.File;

public class FileUtils {

    public static File creerDossierPuzzle(File dossierBase, String nomPuzzle) {
        String nomNettoye = nomPuzzle.trim().replaceAll("[^a-zA-Z0-9_-]", "_");
        File dossierPuzzle = new File(dossierBase, nomNettoye);

        if (!dossierPuzzle.exists()) {
            dossierPuzzle.mkdirs();
        }

        return dossierPuzzle;
    }
}