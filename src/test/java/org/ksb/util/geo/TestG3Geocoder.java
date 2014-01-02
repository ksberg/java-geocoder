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

import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.*;

import org.junit.Test;



/**
 * Non-network service tests
 */
public class TestG3Geocoder {

    /**
     * Utility to grab contents of test file resource. Resource
     * folder is located at the root of the test hierarchy.
     *
     * @param resourceName
     * @return
     */
    public String getTestFileAsString(String resourceName) {
        InputStream in = getClass().getResourceAsStream("/" + resourceName);
        java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    @Test
    public void testReturnStatus() {
        G3Geocoder gc = new G3Geocoder();
        String result = null;

        result =  getTestFileAsString("GoogleReverseGeocodeMinimum.xml");
        assertTrue( "valid status", gc.testStatus(result));

        result =  getTestFileAsString("GoogleReverseGeocodeError.xml");
        assertTrue( "error status", !gc.testStatus(result));
    }


    @Test
    public void testReverseGeocodeProperties() {
        try {
            G3Geocoder gc = new G3Geocoder();

            String stringResponse = getTestFileAsString("GoogleReverseGeocodeFull.xml");

            Properties p = gc.xmlToProperties(stringResponse);
            assertTrue("Parsed Properties", p.size() > 11);

        } catch (Exception e) {
            assertTrue( e.getMessage(), false);
        }

    }

    @Test
    public void testReverseGeocodeObject() {
        try {
            G3Geocoder gc = new G3Geocoder();

            String stringResponse = getTestFileAsString("GoogleReverseGeocodeFull.xml");

            Properties p = gc.xmlToProperties(stringResponse);
            assertTrue("Parsed Properties", p.size() > 11);

            GeocodeResponse response = gc.buildGeocodeResponse(p);

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

        } catch (Exception e) {
            assertTrue( e.getMessage(), false);
        }

    }

    @Test
    public void testReverseGeocodeObjectError() {
        try {
            G3Geocoder gc = new G3Geocoder();

            String stringResponse = getTestFileAsString("GoogleReverseGeocodeError.xml");

            Properties p = gc.xmlToProperties(stringResponse);

            System.out.println("Parsed Properties = " + p.size());
            assertTrue("Parsed Properties", p.size() == 0);

            GeocodeResponse response = gc.buildGeocodeResponse(p);
            assertNull(response);

        } catch (Exception e) {
            assertTrue( e.getMessage(), false);
        }

    }

    /**
     * <code>
     * Reference: https://developers.google.com/maps/documentation/business/webservices/auth
     *
     * URL:  http://maps.googleapis.com/maps/api/geocode/json?address=New+York&sensor=false&client=clientID
     * Private Key: vNIXE0xscrmjlyV-12Nj_BvUPaw=
     * URL Portion to Sign: /maps/api/geocode/json?address=New+York&sensor=false&client=clientID
     * Signature: KrU1TzVQM7Ur0i8i7K3huiw3MsA=
     * Full Signed URL: http://maps.googleapis.com/maps/api/geocode/json?address=New+York&sensor=false&client=clientID&signature=KrU1TzVQM7Ur0i8i7K3huiw3MsA=
     * </code>
     */
    @Test
    public void testSignedForwardURL() {
        try {
            G3Geocoder gc = new G3Geocoder();
            gc.setDataType("json");
            gc.setGoogleClientID("clientID");
            gc.setGoogleSigningKey("vNIXE0xscrmjlyV-12Nj_BvUPaw=");

            String result = gc.buildForwardGeocodeRequest("New+York");
            String expected = "http://maps.googleapis.com/maps/api/geocode/json?address=New+York&sensor=false&client=clientID&signature=KrU1TzVQM7Ur0i8i7K3huiw3MsA=";

            System.out.println(result);
            System.out.println(expected);

            assertTrue("Signed URL Matches Expected",result.contentEquals(expected));

            // From Google Maps: https://developers.google.com/maps/documentation/business/webservices/auth
        } catch (Exception e) {
            assertTrue( e.getMessage(), false);
        }
    }

}
