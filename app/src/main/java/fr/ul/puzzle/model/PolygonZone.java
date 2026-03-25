package fr.ul.puzzle.model;

import android.graphics.PointF;

public class PolygonZone {

    private int id;
    private PointF[] points;

    public PolygonZone(int id, PointF[] points) {
        this.id = id;
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public PointF[] getPoints() {
        return points;
    }
}