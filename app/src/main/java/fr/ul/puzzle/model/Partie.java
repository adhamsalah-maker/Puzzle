package fr.ul.puzzle.model;

import java.util.ArrayList;
import java.util.List;

public class Partie {

    private Puzzle puzzle;
    private List<Placement> placements;
    private int indexPieceCourante;
    private boolean terminee;
    private int progression;

    public Partie(Puzzle puzzle) {
        this.puzzle = puzzle;
        this.placements = new ArrayList<>();
        this.indexPieceCourante = 0;
        this.terminee = false;
        this.progression = 0;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public List<Placement> getPlacements() {
        return placements;
    }

    public int getIndexPieceCourante() {
        return indexPieceCourante;
    }

    public boolean estTerminee() {
        return terminee;
    }

    public int getProgression() {
        return progression;
    }

    public void setPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    public void setPlacements(List<Placement> placements) {
        this.placements = placements;
        recalculerProgression();
    }

    public void setIndexPieceCourante(int indexPieceCourante) {
        this.indexPieceCourante = indexPieceCourante;
    }

    public void setTerminee(boolean terminee) {
        this.terminee = terminee;
    }

    public void ajouterPlacement(Placement placement) {
        this.placements.add(placement);
        recalculerProgression();
    }

    public void supprimerPlacement(Placement placement) {
        this.placements.remove(placement);
        recalculerProgression();
    }

    public void recalculerProgression() {
        if (puzzle == null || puzzle.getPieces().isEmpty()) {
            progression = 0;
            terminee = false;
            return;
        }

        progression = (placements.size() * 100) / puzzle.getPieces().size();
        terminee = placements.size() == puzzle.getPieces().size();
    }
}