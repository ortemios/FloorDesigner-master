package com.daniils.floordesigner;

import com.daniils.floordesigner.util.Binsearch;
import com.daniils.floordesigner.util.Maths;

import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BuildingHelper {
    private static final double MIN_DIST = 50;
    private static final double MIN_DIST_LAST = 80;
    private static final double MIN_THETA = Math.toRadians(30);

    public static ArrayList<Point> buildPolyFromPath(List<Point> path, double scale) {
        if (path.size() < 3)
            return null;

        ArrayList<Point> out = new ArrayList<>();

        Point c = path.get(0);
        out.add(c);

        Iterator<Point> itA = path.iterator();
        Iterator<Point> itB = path.iterator(); itB.next();
        while(itB.hasNext()) {
            Point a = itA.next();
            Point b = itB.next();
            double theta = Maths.theta(c, a, a, b);
            double dist = Math.min(
                    Math.min(
                            Maths.dist(c, a),
                            Maths.dist(c, b)),
                    Maths.dist(path.get(0), b)
            );
            if (dist > MIN_DIST / scale && theta > MIN_THETA) {
                out.add(b);
                c = a;
            }
            if (out.size() > 1) {
                if (Maths.dist(path.get(0), out.get(out.size() - 1)) < MIN_DIST_LAST / scale) {
                    out.remove(out.size() - 1);
                }
            }
        }

        if (out.size() < 3)
            return null;
        processCorners(out);

        if (out.size() == 4) {
            out.set(3, out.get(0).sub(out.get(1)).add(out.get(2)));
        }
        return out;
    }

    private static void processCorners(ArrayList<Point> points) {
        for (int i = 0; i < points.size() - 1; i++) {
            Point a = points.get((i + 0) % points.size());
            Point b = points.get((i + 1) % points.size());
            Point c = points.get((i + 2) % points.size());
            Point[] dir = MovementCorrector.getBestDirection(new Point [] { a, b }, c);
            if (i < points.size() - 2) {
                points.set((i + 2) % points.size(), Maths.projectTo(c, dir[0], dir[1]));
            } else {
                Point d = points.get((i + 3) % points.size());
                points.set((i + 2) % points.size(), Maths.intersection(c, d, dir[0], dir[1]));
            }
        }
    }
}
