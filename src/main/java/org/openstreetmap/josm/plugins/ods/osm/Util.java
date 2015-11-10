package org.openstreetmap.josm.plugins.ods.osm;

import org.openstreetmap.josm.data.coor.EastNorth;

public class Util {
    
    /**
     * Computes the perpendicular distance from a point p to the (infinite) line
     * containing the points AB
     * 
     * This code was borrowed from com.vividsolutions.jts.algorithm.CGAlgorithms 
     * 
     * @param en
     *          the EastNorth coordinate to compute the distance for
     * @param en1
     *          one coordinate of the line
     * @param en2
     *          the other coordinate of the line
     * @return the distance from en to line en1-en2
     */
public static double distancePointLine(EastNorth en, EastNorth en1, EastNorth en2)
        {
          // if start = end, then just compute distance to one of the endpoints
          if (en1.east() == en2.east() && en1.north() == en2.north())
            return en.distance(en1);
          
          // otherwise use comp.graphics.algorithms Frequently Asked Questions method
          /*
           * (1) r = AC dot AB 
           *         --------- 
           *         ||AB||^2 
           *         
           * r has the following meaning: 
           *   r=0 P = A 
           *   r=1 P = B 
           *   r<0 P is on the backward extension of AB 
           *   r>1 P is on the forward extension of AB 
           *   0<r<1 P is interior to AB
           */

          double len2 = (en2.east() - en1.east()) * (en2.east() - en1.east()) + (en2.north() - en1.north()) * (en2.north() - en1.north());
          double r = ((en.east() - en1.east()) * (en2.east() - en1.east()) + (en.north() - en1.north()) * (en2.north() - en1.north()))
              / len2;

          if (r <= 0.0)
            return en.distance(en1);
          if (r >= 1.0)
            return en.distance(en2);

          /*
           * (2) s = (Ay-Cy)(Bx-Ax)-(Ax-Cx)(By-Ay) 
           *         ----------------------------- 
           *                    L^2
           * 
           * Then the distance from C to P = |s|*L.
           * 
           * This is the same calculation as {@link #distancePointLinePerpendicular}.
           * Unrolled here for performance.
           */
          double s = ((en1.north() - en.north()) * (en2.east() - en1.east()) - (en1.east() - en.east()) * (en2.north() - en1.north()))
              / len2;
          return Math.abs(s) * Math.sqrt(len2);
        }
}
