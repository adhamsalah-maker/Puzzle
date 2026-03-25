package fr.ul.puzzle.model;

import android.graphics.PointF;

public class PolygonPiece {

    private int id;
    private PointF[] points;
    private String cheminImage;
    private int rotationCourante;
    private boolean placee;
    private int zoneCibleId;

    public PolygonPiece(int id, PointF[] points, String cheminImage, int zoneCibleId) {
        this.id = id;
        this.points = points;
        this.cheminImage = cheminImage;
        this.zoneCibleId = zoneCibleId;
        this.rotationCourante = 0;
        this.placee = false;
    }

    public int getId() {
        return id;
    }

    public PointF[] getPoints() {
        return points;
    }

    public String getCheminImage() {
        return cheminImage;
    }

    public int getRotationCourante() {
        return rotationCourante;
    }

    public boolean estPlacee() {
        return placee;
    }

    public int getZoneCibleId() {
        return zoneCibleId;
    }

    public void setRotationCourante(int rotationCourante) {
        this.rotationCourante = rotationCourante;
    }

    public void setPlacee(boolean placee) {
        this.placee = placee;
    }
}