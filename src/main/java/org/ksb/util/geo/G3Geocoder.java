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


import java.io.*;
import java.net.URL;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.net.URISyntaxException;
import java.util.Properties;

public class G3Geocoder implements IGeocoder {

    public static final int RETRIES = 4;
    public static final int PAUSE = 300;

    public String       _baseURL = "http://maps.googleapis.com/maps/api/geocode";
    public String       _dataType = "xml";
    public String       _clientID = null;
    public String       _signingKey = null;
    public G3UrlSigner  _requestSigner = null;

    public G3Geocoder() {
        super();
    }

    // ------------------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------------------

    public void setBaseURL(String str) {
        _baseURL = str;
    }

    public String getBaseURL() {
        return _baseURL;
    }

    public void setDataType(String str) {
        if ("xml".equalsIgnoreCase(str)) {
            _dataType = "xml";
        } else if ("json".equalsIgnoreCase(str)) {
            _dataType = "json";
        } else {
            _dataType = "xml";
        }
    }

    public String getDataType() {
        return _dataType;
    }


    public String getGoogleMapsGeocodeUrl()     { return _baseURL; }
    public String getGoogleClientID()           { return _clientID; }
    public String getGoogleSigningKey()         { return _signingKey; }

    public void setGoogleMapsGeocodeUrl(String url) {
        _baseURL = url;
    }

    public void setGoogleClientID(String id) {
        _clientID = id;
    }

    /**
     * The Google V3 signing key is used for server-side signing of the REST web service request.
     * See Google Maps V3 reference for more information:
     * https://developers.google.com/maps/documentation/business/webservices/auth
     *
     * @param key
     */
    public void setGoogleSigningKey(String key) {
        if (key != null) {
            try {
                _requestSigner = new G3UrlSigner(key);
                _signingKey = key;
            } catch (Exception e) {
                _signingKey = null;
            }
        } else {
            _signingKey = key;
        }
    }


    // ------------------------------------------------------------------------------
    // IForwardGeocoder interface methods
    // ------------------------------------------------------------------------------

    /**
     * Returns the latitude/longitude coordinates of a location provided as a free-form address.
     * The address input should be of the form e.g. "10, Market Street, San Franscisco, USA, 94111".
     * The return response will include the address for the given coordinates, which may vary slightly
     * from the address provided, depending on geo-spatial information. This method can also return null
     * on getting an invalid service response.
     *
     * Example URL produced: http://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&sensor=true_or_false
     *
     * @param address String
     * @return GeocodeResponse
     * @throws Exception
     */
    public GeocodeResponse getForwardGeocode(String address) throws Exception {

        String urlRequest   = buildForwardGeocodeRequest(address);
        String urlResponse  = fetchUrlRequest(urlRequest);

        if (!testStatus(urlResponse)) return null;

        Properties p = xmlToProperties(urlResponse);
        GeocodeResponse gr = buildGeocodeResponse(p);

        return gr;
    }

    // ------------------------------------------------------------------------------
    // IGeocoder interface methods
    // ------------------------------------------------------------------------------

    /**
     * Returns an address given the latlon for a position
     * @param position A position that has Latitude and logitude values
     * @return <code>GeocodeResponse</code> A response object stuffed with Geocoding information
     */
    public GeocodeResponse getReverseGeocode(LatLng position) throws Exception {
        if (position == null) throw new NullPointerException();
        return getReverseGeocode(position.getLatitude(), position.getLongitude());
    }


    /**
     * Returns an address given the latlon for a position
     *
     *  http://maps.googleapis.com/maps/api/geocode/xml?latlng=35.714224,-73.961452&sensor=false
     *  http://maps.googleapis.com/maps/api/geocode/xml?latlng=40.714224,-72.961452&sensor=false
     *
     * @param lat Latitude of the position
     * @param lng Longitude of the position
     * @return <code>GeocodeResponse</code> A response object stuffed with Geocoding information
     */
    public GeocodeResponse getReverseGeocode(double lat, double lng) throws Exception {

        String urlRequest = buildReverseGeocodeRequest(lat,lng);
        String urlResponse = fetchUrlRequest(urlRequest);

        // should this throw?
        // check existing behavior
        if (!testStatus(urlResponse)) return null;

        Properties p = xmlToProperties(urlResponse);
        GeocodeResponse gr = buildGeocodeResponse(p);

        LatLng pos = gr.getPosition();
        if (pos.getLatitude() == 0.0) {
            gr.setPosition(new LatLng(lat,lng));
        }
        return gr;
    }


    // ------------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------------


    /**
     * Quick test on Google return string. Looks for valid return status.
     *
     * <ul>
     * <li>google.maps.GeocoderStatus.OK indicates that the geocode was successful.
     * </li>
     * <li>google.maps.GeocoderStatus.OVER_QUERY_LIMIT indicates that you are over your quota. Refer to the Usage limits exceeded article in the Maps for Business documentation, which also applies to non-Business users, for more information.
     * </li>
     * <li>google.maps.GeocoderStatus.REQUEST_DENIED indicates that your request was denied for some reason.
     * </li>
     * <li>google.maps.GeocoderStatus.INVALID_REQUEST generally indicates that the query (address or latLng) is missing.
     * </li>
     * </ul>
     *
     * @param xmlResponse Google response
     * @return
     */
    protected boolean testStatus(String xmlResponse) {
        return xmlResponse.contains("<status>OK</status>");
    }


    /**
     * Form the Google V3 geocode request for the given address
     *
     * @param address
     * @return String url request
     */
    protected String buildForwardGeocodeRequest(String address) {
        StringBuilder strb = new StringBuilder();
        strb.append("?address=").append(address.replaceAll(" ", "+"));
        String paramQuery = strb.toString();
        return buildGoogleRequest(strb, paramQuery);
    }

    /**
     * Form the Google V3 reverse geocode request for the given
     * latitude and longitude.
     *
     * @param lat decimal latitude
     * @param lon decimal longitude
     * @return String url request
     */
    protected String buildReverseGeocodeRequest(double lat, double lon) {
        StringBuilder strb = new StringBuilder();
        strb.append("?latlng=").append(lat).append(",").append(lon);
        String paramQuery = strb.toString();
        return buildGoogleRequest(strb, paramQuery);
    }

    /**
     * Construct Google URL request with (or without) signature and client ID.
     *
     * @param strb construction buffer
     * @param queryRequest the particular query (forward or reverse geocode)
     * @return String urlString
     */
    public String buildGoogleRequest(StringBuilder strb, String queryRequest) {
        strb.setLength(0);
        strb.append(_baseURL).append("/").append(_dataType).append(queryRequest).append("&sensor=false");
        if (_clientID != null) strb.append("&client=").append(_clientID);
        if (_requestSigner != null) {
            try {
                String urlString = strb.toString();
                URL url = new URL(urlString);
                String request = _requestSigner.signRequest(url.getPath(),url.getQuery());
                System.out.println("Signed URL :" + url.getProtocol() + "://" + url.getHost() + request);

                strb.setLength(0);
                strb.append(url.getProtocol()).append("://").append(url.getHost()).append(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return strb.toString();
    }


    /**
     * Execute a GET on the provided URL and answer the response string.
     * Has built-in retry for server unavailable error.
     *
     * @param urlRequest
     * @return response
     * @throws Exception
     */
    public String fetchUrlRequest(String urlRequest) throws Exception {
        StringBuilder strb = new StringBuilder();
        URL url = new URL(urlRequest);
        String urlResponse = null;
        int tries = RETRIES;
        while(tries > 0 && (urlResponse == null)) {
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader( new InputStreamReader( url.openStream() ) );
                String responseStringLine;
                strb.setLength(0);
                while ( ( responseStringLine = bufferedReader.readLine() ) != null )
                {
                    strb.append(responseStringLine);
                }
                urlResponse = strb.toString();
            } catch (IOException ioe) {
                ioe.getMessage().contains("500");  // HTTP 500 error - server unavailable
                tries--;
                try {
                    Thread.sleep(PAUSE);
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                throw new Exception(e);
            } finally {
                if (bufferedReader != null) bufferedReader.close();
            }

        }
        return urlResponse;
    }

    /**
     * Utility to fetch contents of named resource file. Used by XSLT mapping.
     *
     * @param resourceName
     * @return
     */
    public String getResourceFileAsString(String resourceName) {
        InputStream in = getClass().getResourceAsStream("/" + resourceName);
        java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    /**
     * Map element names with type conflict to innocuous names.

     * @param xmlInput
     * @return result of transform
     * @throws IOException
     * @throws URISyntaxException
     * @throws TransformerException
     */
    public Properties xmlToProperties(String xmlInput) throws IOException, URISyntaxException, TransformerException {
        String xslt = getResourceFileAsString("GoogleV3ReverseGeocodeToProperties.xsl");

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(
                new StreamSource(new StringReader(xslt)));

        StringReader reader = new StringReader(xmlInput);
        StringWriter writer = new StringWriter();

        transformer.transform(new StreamSource(reader),new StreamResult(writer));

        String result = writer.toString();

        Properties p = new Properties();
        p.load(new StringReader(result));
        return p;
    }


    /**
     *
     * country = country
     * state = administrative_area_level_1
     * county = administrative_area_level_2
     * city = administrative_area_level_3
     * street = route
     * address = street_number
     * establishment
     * locality
     * zip-code = postal_code
     *
     * @param p
     * @return
     */
    public GeocodeResponse buildGeocodeResponse(Properties p) {

        if (p.size() == 0) return null;

        GeocodeResponse gr = new GeocodeResponse();

        String str = null;

        str = p.getProperty("street_number","");
        if (str.length()>0) gr.setBuildingNumber(str);

        str = p.getProperty("route","");
        if (str.length()>0) gr.setStreet(str);

        // City can vary in Google response
        str = p.getProperty("administrative_area_level_3","");
        if (str.length()>0) {
            gr.setCity(str);
        } else {
            str = p.getProperty("locality","");
            if (str.length()>0) gr.setCity(str);
        }

        str = p.getProperty("administrative_area_level_2","");
        if (str.length()>0) gr.setCounty(str);

        str = p.getProperty("administrative_area_level_1","");
        if (str.length()>0) gr.setState(str);

        str = p.getProperty("country","");
        if (str.length()>0) gr.setCountryName(str);

        gr.setCountryNameCode("");
        gr.setBuildingSegmentName("");

        str = p.getProperty("postal_code","");
        if (str.length()>0) gr.setPostalCode(str);

        try {
            LatLng ll = new LatLng();

            str = p.getProperty("lat","");
            ll.setLatitude(Double.parseDouble(str));

            str = p.getProperty("lng","");
            ll.setLongitude(Double.parseDouble(str));

            if (str.length()>0) gr.setPosition(ll);

        } catch (Exception e) {

        }

        return gr;
    }

    // ------------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------------

    /**
     * The exists to support testing standalone JAR in a production environment,
     * which may be required due to commercial Google API domain registration.
     *
     * @param args (see usage)
     */
    public static void main(String[] args) {
        G3Geocoder coder = new G3Geocoder();

        if (args.length < 2) {
            System.out.println("USAGE: G3Geocoder <flag> <arg>");
            System.out.println("FLAGS: [-c -k] (-f | -r) GeoCodeParams");
            System.out.println("\t-c Client ID");
            System.out.println("\t-k Signing Key");
            System.out.println("\t-f Forward Geocoding");
            System.out.println("\t-r Reverse Geocoding");
            System.out.println("Replace Spaces with +");
            System.out.println();
        }
        String clientID = null;
        String signingKey = null;
        int instructionOffset = 0;

        if ("-c".equalsIgnoreCase(args[0])) { // provide ID and Key
            // 0 : -c
            // 1 : ClientID
            // 2 : -k
            // 3 : Key
            coder.setGoogleClientID(args[1]);
            coder.setGoogleSigningKey(args[3]);
            instructionOffset = 4;
        }
        String flag = args[instructionOffset + 0];
        String geoParam = args[instructionOffset + 1] ;

        try {

            if ("-f".equalsIgnoreCase(flag)) {
                GeocodeResponse fwdResponse = coder.getForwardGeocode(geoParam);
                LatLng coord = fwdResponse.getPosition();

                System.out.println();
                System.out.println("Coordinate: [" + coord.getLatitude() + "," + coord.getLongitude() + "]");
                System.out.println();

            } else if ("-r".equalsIgnoreCase(flag)) {
                String splits[] = geoParam.split(",");
                double lat = Double.parseDouble(splits[0]);
                double lng = Double.parseDouble(splits[1]);
                GeocodeResponse response = coder.getReverseGeocode(lat,lng);

                StringBuilder sb = new StringBuilder();

                if (response != null) {
                    printPair("BLDG #", response.getBuildingNumber(),sb) ;
                    printPair("STREET", response.getStreet(),sb);
                    printPair("CITY",response.getCity(),sb) ;
                    printPair("COUNTY",response.getCounty(),sb) ;
                    printPair("STATE", response.getState(),sb) ;
                    printPair("COUNTRY", response.getCountryName(),sb) ;
                    printPair("POST CODE", response.getPostalCode(),sb) ;
                } else {
                    sb.append("ERROR");
                }

                System.out.println();
                System.out.println(sb.toString());
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void printPair(String label, String value, StringBuilder sb) {
        final int labelColumnLen = 15;
        int beforeLen = sb.length();
        sb.append(label);
        int targetLen = beforeLen+labelColumnLen;

        while(sb.length() < targetLen) sb.append(" ");
        sb.append(value).append("\n");
    }

}

