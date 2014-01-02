/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2013, Kevin Sven Berg
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ***** END LICENSE BLOCK ***** */

package org.ksb.util.geo;

import java.io.Serializable;
import java.text.NumberFormat;

/**
 * Latitude and longitude of a geopoint.
 *
 */

public class LatLng implements Serializable {
    // ---------- CONSTANTS ----------

    /** Radius of the earth, in meters, at the equator. */
    protected static final double GLOBE_RADIUS_EQUATOR = 6378000;

    /** Radius of the earth, in meters, at the poles. */
    protected static final double GLOBE_RADIUS_POLES = 6357000;

    private static final long serialVersionUID = 7813189715823367699L;
    private double latitude, longitude;

    /**
     * Constructor
     */
    public LatLng() {
    }

    /**
     * Lat/Lng Constructor
     *
     * @param latitutde double
     * @param longitude double
     */
    public LatLng(double latitutde, double longitude) {
        super();
        this.latitude = latitutde;
        this.longitude = longitude;
    }

    /**
     * String Constructor
     *
     * @param latlng String with 2, comma-separated doubles
     */
    public LatLng(String latlng) {
        super();
        String[] parts = latlng.split(",");
        if (parts.length != 2)
            throw new IllegalArgumentException(
                    "Expected latlng to be in format 'lat,lon' but found ["
                            + latlng + "]");

        this.latitude = Double.parseDouble(parts[0]);
        this.longitude = Double.parseDouble(parts[1]);
    }

    /**
     * Double String Constructor
     *
     * @param lat String
     * @param lng String
     */
    public LatLng(String lat, String lng) {
        this.latitude = Double.parseDouble(lat);
        this.longitude = Double.parseDouble(lng);
    }

    public String toString() {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(5);

        return new StringBuilder().append(format.format(latitude)).append(',')
                .append(format.format(longitude)).toString();
    }

    // From http://www.cs.jhu.edu/~jason/226/hw10/source/geography/Point.java
    // ---------- DISTANCE AND DIRECTION BETWEEN POINTS ----------

    /**
     * Distance from this point to another point, using the Haversine formula.
     * We take the squashed shape of the earth into account (approximately).
     *
     * Q: Why can't we just use the Pythagorean theorem? We could just return
     * sqrt(dx*dx + dy*dy), where dx is the change in latitude and dy is the
     * change in longitude. A: That doesn't take the curvature of the earth into
     * account. Q: But if we're just driving short distances, isn't the
     * curvature of the earth too slight to matter? A: True, but we don't want
     * our code to mysteriously break if we start using it to drive around the
     * world. Anyway, your formula is still more complicated than you think,
     * since you have to find dx in meters. That's harder than you thought: 1
     * degree of longitude is a long way at the equator, but isn't a lot near
     * the poles.
     */
    public double distanceTo(LatLng p) {
        // WARNING: These two lines of code are duplicated in another method.
        double lat1 = radians(latitude), lat2 = radians(p.latitude), dlat = lat2  - lat1;
        double dlong = radians(p.longitude) - radians(longitude);

        // Formula from http://williams.best.vwh.net/avform.htm#Dist
        // See http://mathforum.org/library/drmath/view/51879.html for a
        // derivation.
        //
        // I've adapted the formula slightly to deal with the squashed
        // earth. We still make an approximation by taking the radius of
        // curvature r to be constant throughout the route. It actually
        // changes, so we should integrate over the whole route. But such
        // "elliptic integrals" don't have a closed form and can't be
        // found using trigonometry.

        double a = square(Math.sin(dlat / 2)) + Math.cos(lat1) * Math.cos(lat2)
                * square(Math.sin(dlong / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); // angle in
        // radians
        // formed by
        // start
        // point,
        // earth's
        // center, &
        // end point
        double r = globeRadiusOfCurvature((lat1 + lat2) / 2); // radius of earth
        // at midpoint
        // of route
        return r * c;
    }

    /**
     * The direction that you have to go from this point to get to p.
     *
     * Answer is returned in degrees, between -180.0 and 180.0. Here -180, -90,
     * 0, 90, and 180 correspond to west, south, east, north, and west again --
     * just like theta in polar coordinates.
     *
     * It would be tempting to just return atan2(dy,dx); see the comment in
     * distanceTo() for why we don't do that.
     */
    public double directionTo(LatLng p) {
        // WARNING: These two lines of code are duplicated in another method.
        double lat1 = radians(latitude);
        double lat2 = radians(p.latitude);
        double dlat = lat2 - lat1;
        double dlong = radians(p.longitude) - radians(longitude);

        // Formula from http://williams.best.vwh.net/avform.htm#GCF
        double radians = Math.atan2(Math.sin(-dlong) * Math.cos(lat2), Math
                .cos(lat1)
                * Math.sin(lat2)
                - Math.sin(lat1)
                * Math.cos(lat2)
                * Math.cos(-dlong));

        double deg = degrees(radians);

        // That formula has 0 degrees being due north. Rotate it
        // so that 90 degrees is due east.
        deg += 90;
        if (deg > 180)  deg -= 360;

        return deg;
    }

    /**
     * Hash using lat/lng components
     *
     * @return int
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(this.latitude);
        result = PRIME * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.longitude);
        result = PRIME * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LatLng other = (LatLng) obj;
        if (Double.doubleToLongBits(this.latitude) != Double
                .doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(this.longitude) != Double
                .doubleToLongBits(other.longitude))
            return false;
        return true;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // ---------- MATHEMATICAL UTILITY FUNCTIONS ----------

    protected static final double radians(double degrees) {
        return degrees * (2 * Math.PI) / 360;
    }

    protected static final double degrees(double radians) {
        return radians * 360 / (2 * Math.PI);
    }

    protected static final double square(double d) {
        return d * d;
    }

    /**
     * Computes the earth's radius of curvature at a particular latitude,
     * assuming that the earth is a squashed sphere with elliptical
     * cross-section. Since we supposedly have latitude and longitude to lots of
     * decimal places, I decided to worry about the squashing, just for fun.
     *
     * The radius of curvature at a latitude is NOT the same as the actual
     * radius. The actual radius is smaller at the poles than at the equator,
     * but the earth is less curved there, as if it were the surface of a
     * *bigger* sphere!
     *
     * The actual radius could be computed by return
     * Math.sqrt(square(GLOBE_RADIUS_EQUATOR*Math.cos(lat)) +
     * square(GLOBE_RADIUS_POLES*Math.sin(lat)));
     *
     * The radius of curvature depends not only on the latitude you're at but
     * also on the direction you are traveling. But I'll use the approximate
     * formula recommended at http://www.census.gov/cgi-bin/geo/gisfaq?Q5.1
     * which ignores the direction. There is a whole range of possible answers
     * depending on direction; the formula returns the geometric mean of the max
     * and min of that range.
     *
     * @param lat
     *            - latitude in radians. This is the angle that a point at this
     *            latitude makes with the horizontal.
     */
    protected static final double globeRadiusOfCurvature(double lat) {
        double a = GLOBE_RADIUS_EQUATOR; // major axis
        double b = GLOBE_RADIUS_POLES; // minor axis
        double e = Math.sqrt(1 - square(b / a)); // eccentricity
        return a * Math.sqrt(1 - square(e)) / (1 - square(e * Math.sin(lat)));
    }
}
