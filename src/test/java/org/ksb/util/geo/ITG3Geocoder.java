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
import java.io.InputStream;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;
import org.ksb.test.IntegrationTest;

/**
 * Integration Test - Uses Google Service API
 */
@Category(IntegrationTest.class)
public class ITG3Geocoder {

    public final static boolean VERBOSE = false;

    /**
     * Utility to grab contents of test file resource. Resource
     * folder is located at the root of the test hierarchy.
     *
     * @param resourceName String
     * @return
     */
    public String getTestFileAsString(String resourceName) {
        InputStream in = getClass().getResourceAsStream("/" + resourceName);
        java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @Test
    public void testNetForwardGeocodePiecesOK() {
        try {
            G3Geocoder gc = new G3Geocoder();
            String request = gc.buildForwardGeocodeRequest("1488 Montgomery Highway Birmingham AL 35216");
            String response = gc.fetchUrlRequest(request);
            assertTrue( response.contains("OK") );

            if (VERBOSE) {
                System.out.println();
                System.out.println("FORWARD OK == " + response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNetForwardGeocodePiecesError() {
        try {
            G3Geocoder gc = new G3Geocoder();
            String request = gc.buildForwardGeocodeRequest("FRozBot, , AX, US");
            String response = gc.fetchUrlRequest(request);
            assertTrue( response.contains("ZERO_RESULTS") );

            if (VERBOSE) {
                System.out.println();
                System.out.println("FORWARD ERROR == " + response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testNetForwardGeocodeOK() {
        try {
            G3Geocoder gc = new G3Geocoder();
            GeocodeResponse gr = gc.getForwardGeocode("1488 Montgomery Highway Birmingham AL 35216");
            LatLng coords = gr.getPosition();

            assertEquals("latitude check",-86.804577, coords.getLongitude(), 0.0005);
            assertEquals("longitude check",33.414628, coords.getLatitude(), 0.0005);

            if (VERBOSE) {
                System.out.println();
                System.out.println("FORWARD OK == " + coords.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testNetReverseGeocodePiecesOK() {
        try {
            G3Geocoder gc = new G3Geocoder();
            String request = gc.buildReverseGeocodeRequest(40.714224,-72.961452);
            String response = gc.fetchUrlRequest(request);
            assertTrue( response.contains("OK") );

            if (VERBOSE) {
                System.out.println();
                System.out.println("FORWARD OK == " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNetReverseGeocodePiecesError() {
        try {
            G3Geocoder gc = new G3Geocoder();
            String request = gc.buildReverseGeocodeRequest(35.714224,-73.961452);
            String response = gc.fetchUrlRequest(request);
            assertTrue( response.contains("ZERO_RESULTS") );

            if (VERBOSE) {
                System.out.println();
                System.out.println("FORWARD ERROR == " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNetReverseGeocodeOK() {
        try {
            G3Geocoder gc = new G3Geocoder();
            GeocodeResponse response = gc.getReverseGeocode(40.714224,-72.961452);

            assertNotNull(response);

            assertTrue("33".equalsIgnoreCase(response.getBuildingNumber())) ;
            assertTrue("Dune Walk".equalsIgnoreCase(response.getStreet())) ;
            assertTrue("Brookhaven".equalsIgnoreCase(response.getCity())) ;
            assertTrue("Suffolk".equalsIgnoreCase(response.getCounty())) ;
            assertTrue("NY".equalsIgnoreCase(response.getState())) ;
            assertTrue("US".equalsIgnoreCase(response.getCountryName())) ;
            assertTrue("11772".equalsIgnoreCase(response.getPostalCode())) ;

            LatLng ll = response.getPosition();
            assertTrue(ll.getLatitude() == 37.09024);
            assertTrue(ll.getLongitude() == -95.712891);

            if (VERBOSE) {
                System.out.println();
                System.out.println("FORWARD OK == " + ll.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
