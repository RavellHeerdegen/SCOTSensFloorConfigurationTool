package app;

import app.models.CarpetCoordinate;

import java.util.List;

/**
 *  This class provides math methods for CarpetCoordinates.
 */
public class CarpetCoordinateMaths {

    /**
     * The sum Eucledian distances to a given coordinate
     *
     * @param coordinate
     * @param otherCoordinates
     * @return
     */
    public static double distSum(CarpetCoordinate coordinate, List<CarpetCoordinate> otherCoordinates) {
        double sum = 0;
        for (CarpetCoordinate otherCoordinate : otherCoordinates) {
            double distx = Math.abs(otherCoordinate.x - coordinate.x);
            double disty = Math.abs(otherCoordinate.y - coordinate.y);
            sum += Math.sqrt((distx * distx) + (disty * disty));
        }
        return sum;
    }

    /**
     * Returns the coordinate in the given list which has the least sum of distance to all the other points.
     *
     * @param cc
     * @return
     */
    public static CarpetCoordinate leastSumOfDistances(List<CarpetCoordinate> cc) {
        if (cc.isEmpty()) return null;

        double currentMinDist = distSum(cc.get(0), cc);
        CarpetCoordinate bestGuess = cc.get(0);

        for (int i = 1; i < cc.size(); i++) { // Loop starts at index 1 because first element already calculated before loop starts
            CarpetCoordinate c = cc.get(i);
            double newDist = distSum(c, cc);
            if (newDist < currentMinDist) {
                bestGuess = c;
                currentMinDist = newDist;
            }
        }
        return bestGuess;
    }

    /**
     * Calculates the geometric mid point
     * @param cc
     * @return
     */
    public static CarpetCoordinate midPoint(List<CarpetCoordinate> cc) {
        CarpetCoordinate midPoint = new CarpetCoordinate(0,0);

        for (CarpetCoordinate c: cc) {
            midPoint.x += c.x;
            midPoint.y += c.y;
        }

        midPoint.x = midPoint.x/cc.size();
        midPoint.y = midPoint.y/cc.size();

        return midPoint;
    }
}
