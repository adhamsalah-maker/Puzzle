package fr.ul.puzzle.model;

import java.util.ArrayList;
import java.util.List;

public class Partie {

    private Puzzle puzzle;
    private List<Placement> placements;
    private int indexPieceCourante;
    private boolean terminee;

    public Partie(Puzzle puzzle) {
        this.puzzle = puzzle;
        this.placements = new ArrayList<>();
        this.indexPieceCourante = 0;
        this.terminee = false;
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

    public void setPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    public void setPlacements(List<Placement> placements) {
        this.placements = placements;
    }

    public void setIndexPieceCourante(int indexPieceCourante) {
        this.indexPieceCourante = indexPieceCourante;
    }

    public void setTerminee(boolean terminee) {
        this.terminee = terminee;
    }

    public void ajouterPlacement(Placement placement) {
        this.placements.add(placement);
    }
}