/*
 * Androzic - android navigation client that uses OziExplorer maps (ozf2, ozfx3).
 * Copyright (C) 2010-2012  Andrey Novikov <http://andreynovikov.info/>
 *
 * This file is part of Androzic application.
 *
 * Androzic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Androzic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Androzic.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.androzic.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class StringFormatter {
    // http://code.google.com/p/android/issues/detail?id=2626
    final static DecimalFormat coordDegFormat = new DecimalFormat("#0.000000", new DecimalFormatSymbols(Locale.ENGLISH));
    final static DecimalFormat coordIntFormat = new DecimalFormat("00", new DecimalFormatSymbols(Locale.ENGLISH));
    final static DecimalFormat coordMinFormat = new DecimalFormat("00.0000", new DecimalFormatSymbols(Locale.ENGLISH));
    final static DecimalFormat coordSecFormat = new DecimalFormat("00.000", new DecimalFormatSymbols(Locale.ENGLISH));

    final static DecimalFormat timeFormat = new DecimalFormat("00");

    public static int coordinateFormat = 0;

    public static double distanceFactor = 1.0;
    public static String distanceAbbr = "km";
    public static double distanceShortFactor = 1.0;
    public static String distanceShortAbbr = "m";

    public static String precisionFormat = "%.0f";
    public static double speedFactor;
    public static String speedAbbr;

    public static String elevationFormat = "%.0f";
    public static double elevationFactor;
    public static String elevationAbbr;

    public static String angleFormat = "%.0f";
    public static double angleFactor = 1d;
    public static String angleAbbr;

    //FIXME Should localize:
    public static String secondAbbr = "sec";
    public static String minuteAbbr = "min";
    public static String hourAbbr = "h";

    public static String distanceH(final double distance) {
        return distanceH(distance, 2000);
    }

    public static String distanceH(double distance, int threshold) {
        String[] dist = distanceC(distance, threshold);
        return dist[0] + " " + dist[1];
    }

    public static String distanceH(double distance, String format) {
        return distanceH(distance, format, 2000);
    }

    public static String distanceH(double distance, String format, int threshold) {
        String[] dist = distanceC(distance, format, threshold);
        return dist[0] + " " + dist[1];
    }

    public static String[] distanceC(final double distance) {
        return distanceC(distance, 2000);
    }

    public static String[] distanceC(final double distance, int threshold) {
        return distanceC(distance, "%.0f", threshold);
    }

    public static String[] distanceC(final double distance, final String format) {
        return distanceC(distance, format, 2000);
    }

    public static String[] distanceC(final double distance, final String format, int threshold) {
        double dist = distance * distanceShortFactor;
        String distunit = distanceShortAbbr;
        if (Math.abs(dist) > threshold) {
            dist = dist / distanceShortFactor / 1000 * distanceFactor;
            distunit = distanceAbbr;
        }

        return new String[]{String.format(format, dist), distunit};
    }

    public static String speedH(final double speed) {
        return speedC(speed) + " " + speedAbbr;
    }

    public static String speedC(final double speed) {
        return String.format(precisionFormat, speed * speedFactor);
    }

    public static String elevationH(final double elevation) {
        return elevationC(elevation) + " " + elevationAbbr;
    }

    public static String elevationC(final double elevation) {
        return String.format(elevationFormat, elevation * elevationFactor);
    }

    public static String angleH(final double angle) {
        if (angleFactor == 1d) {
            // Special case for degrees: use symbol instead of abbreviation
            return String.format(angleFormat, angle) + "\u00B0";
        } else {
            return angleC(angle) + " " + angleAbbr;
        }
    }

    public static String angleC(final double angle) {
        return String.format(angleFormat, angle / angleFactor);
    }

    public static String coordinate(double coordinate) {
        return coordinate(coordinateFormat, coordinate);
    }

    public static String coordinate(int format, double coordinate) {
        switch (format) {
            case 0: {
                return coordDegFormat.format(coordinate);
            }
            case 1: {
                final double sign = Math.signum(coordinate);
                final double coord = Math.abs(coordinate);
                final int degrees = (int) Math.floor(coord);
                final double minutes = (coord - degrees) * 60;
                return coordIntFormat.format(sign * degrees) + "\u00B0 "
                        + coordMinFormat.format(minutes) + "'";
            }
            case 2: {
                final double sign = Math.signum(coordinate);
                final double coord = Math.abs(coordinate);
                final int degrees = (int) Math.floor(coord);
                final double min = (coord - degrees) * 60;
                final int minutes = (int) Math.floor(min);
                final double seconds = (min - minutes) * 60;
                return coordIntFormat.format(sign * degrees) + "\u00B0 "
                        + coordIntFormat.format(minutes) + "' "
                        + coordSecFormat.format(seconds) + "\"";
            }
        }
        return String.valueOf(coordinate);
    }

    /**
     * Formats coordinates according to currently selected format as one string with specified delimiter between coordinates (if applicable).
     *
     * @param delimiter Delimiter between latitude and longitude
     * @param latitude  Latitude
     * @param longitude Longitude
     * @return string representation of coordinates
     */
    public static String coordinates(String delimiter, double latitude, double longitude) {
        return coordinates(coordinateFormat, delimiter, latitude, longitude);
    }

    public static String coordinates(int format, String delimiter, double latitude, double longitude) {
        switch (format) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4: {
                return coordinate(format, latitude) + delimiter + coordinate(format, longitude);
            }
        }
        // On any error fall back to default lat/lon coordinates format
        return coordDegFormat.format(latitude) + delimiter + coordDegFormat.format(longitude);
    }

    public static String bearingSimpleH(double bearing) {
        if (bearing < 22 || bearing >= 338) return "\u2191"; // N
        if (bearing < 67 && bearing >= 22) return "\u2197"; // NE
        if (bearing < 112 && bearing >= 67) return "\u2192"; // E
        if (bearing < 158 && bearing >= 112) return "\u2198"; // SE
        if (bearing < 202 && bearing >= 158) return "\u2193"; // S
        if (bearing < 248 && bearing >= 202) return "\u2199"; // SW
        if (bearing < 292 && bearing >= 248) return "\u2190"; // W
        if (bearing < 338 && bearing >= 292) return "\u2196"; // NW
        return ".";
    }

    /**
     * Formats time period in four ways:<br/>
     * "< 1 min" - for 1 minute<br/>
     * "12 min" - for period less than 1 hour<br/>
     * "1:53 min" - for period more than 1 hour<br/>
     * "> 24 h" - for period more than 1 day
     *
     * @param minutes time in minutes
     * @return Time period
     */
    public static String[] timeC(int minutes) {
        int hour = 0;
        int min = minutes;

        if (min <= 1)
            return new String[]{"< 1", minuteAbbr};

        if (min > 59) {
            hour = (int) Math.floor(min / 60);
            min = min - hour * 60;
        }
        if (hour > 23)
            return new String[]{"> 24", hourAbbr};

        return new String[]{timeFormat.format(hour) + ":" + timeFormat.format(min), minuteAbbr};
    }

    /**
     * Formats time period in three ways:<br/>
     * "12 sec" - for period less than 1 minute<br/>
     * "34 min" - for period more than 1 minute<br/>
     * "> 40 min" - for period more than timeout (where 40 is timeout)
     *
     * @param seconds time period in seconds
     * @param timeout timeout in seconds
     * @return Time period
     */
    @SuppressWarnings("UnusedDeclaration")
    public static String[] timeCP(int seconds, int timeout) {
        boolean t = seconds > timeout;

        System.err.print("CP " + seconds + " " + timeout);
        if (seconds <= 59) {
            if (t)
                return new String[]{"> " + String.valueOf(timeout), secondAbbr};
            else
                return new String[]{String.valueOf(seconds), secondAbbr};
        }
        int min = (int) Math.floor(seconds / 60);
        if (t) {
            min = (int) Math.floor(timeout / 60);
            return new String[]{"> " + String.valueOf(min), minuteAbbr};
        } else
            return new String[]{String.valueOf(min), minuteAbbr};
    }

    public static String timeR(int minutes) {
        int hour = 0;
        int min = minutes;

        if (min > 59) {
            hour = (int) Math.floor(min / 60);
            min = min - hour * 60;
        }
        if (hour > 99) {
            return "--:--";
        }

        return timeFormat.format(hour) + ":" + timeFormat.format(min);
    }
}
