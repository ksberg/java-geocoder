/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2001-2013, Kevin Sven Berg
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

import org.junit.Test;
import static org.junit.Assert.*;


public class TestLatLng {

    public static final boolean VERBOSE = true;
    @Test
    public void testDistance() {
        LatLng seattle = new LatLng(47.609722, -122.333056);
        LatLng sandiego = new LatLng(32.715, -117.1625);

        double san2sea = sandiego.distanceTo(seattle);
        double sea3san = seattle.distanceTo(sandiego);

        assertTrue(san2sea == sea3san);

        if (VERBOSE) {
            System.out.println();
            System.out.println("Distance Test");
            System.out.println(san2sea);
            System.out.println(sea3san);
            System.out.println();
            System.out.println();
        }
    }

    @Test
    public void testDirection() {
        LatLng seattle = new LatLng(47.609722, -122.333056);
        LatLng sandiego = new LatLng(32.715, -117.1625);

        double san2sea = sandiego.directionTo(seattle);
        double sea3san = seattle.directionTo(sandiego);

        // TODO: revisit formula .. accuracy not as expected

        if (VERBOSE) {
            System.out.println();
            System.out.println("Direction Test");
            System.out.println(san2sea);
            System.out.println(sea3san);
            System.out.println(180.0 + sea3san);
            System.out.println();
            System.out.println();
        }
    }

//    public void testEquality() {
//
//    }

}
