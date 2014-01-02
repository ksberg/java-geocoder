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


/**
 * Describes the information returned from forward or reverse geocode lookup.
 *
 */
public class GeocodeResponse {

    private String  countryName;
    private String  countryNameCode;
    private String  state;
    private String  county;
    private String  city;
    private String  township;
    private String  street;
    private String  buildingNumber;
    private Integer buildingNumberHigh;
    private Integer buildingNumberLow;
    private String  buildingSegmentName;
    private String  postalCode;
    private LatLng  position;

    /**
     * Default Constructor
     */
    public GeocodeResponse() {
    }

    /**
     * Parameterized Constructor
     *
     * @param buildingNumber String
     * @param buildingSegmentName String
     * @param street String
     * @param township String
     * @param city String
     * @param state String
     * @param countryName String
     * @param countryNameCode String
     * @param postalCode String
     */
    public GeocodeResponse(String buildingNumber, String buildingSegmentName, String street,
                           String township, String city, String state, String countryName,
                           String countryNameCode, String postalCode) {
        this.buildingNumber = buildingNumber;
        this.buildingSegmentName = buildingSegmentName;
        this.street = street;
        this.township = township;
        this.city = city;
        this.state = state;
        this.countryName = countryName;
        this.countryNameCode = countryNameCode;
        this.postalCode = postalCode;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    /**
     * If specific building number is available will be returned otherwise if a range exists then that will
     * be returned otherwise empty string
     * @return building number, building number range, or empty string
     */
    public String getBuildingNumber() {
        if (this.buildingNumber != null) {
            return this.buildingNumber;
        } else if (buildingNumberHigh != null && buildingNumberLow != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(buildingNumberLow).append("-").append(buildingNumberHigh);
            return sb.toString();
        } else {
            return "";
        }
    }

    public void setBuildingSegmentName(String buildingSegmentName) {
        this.buildingSegmentName = buildingSegmentName;
    }

    public String getBuildingSegmentName() {
        return this.buildingSegmentName;
    }

    public void setStreet(String street) {
        int index;
        if (street != null && (index = street.indexOf("\\:")) > -1) {
            street = street.substring(0, index);
        }
        this.street = street;
    }

    public String getStreet() {
        return this.street;
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public String gettownship() {
        return this.township;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return this.city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public void setCountryNameCode(String countryNameCode) {
        this.countryNameCode = countryNameCode;
    }

    public String getCountryNameCode() {
        return this.countryNameCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public LatLng getPosition() {
        return this.position;
    }

    /**
     * @return the county
     */
    public String getCounty() {
        return county;
    }

    /**
     * @param county the county to set
     */
    public void setCounty(String county) {
        this.county = county;
    }

    /**
     * @return the township
     */
    public String getTownship() {
        return township;
    }

    /**
     * @return the buildingNumberHigh
     */
    public Integer getBuildingNumberHigh() {
        return buildingNumberHigh;
    }

    /**
     * @param buildingNumberHigh the buildingNumberHigh to set
     */
    public void setBuildingNumberHigh(Integer buildingNumberHigh) {
        this.buildingNumberHigh = buildingNumberHigh;
    }

    /**
     * @return the buildingNumberLow
     */
    public Integer getBuildingNumberLow() {
        return buildingNumberLow;
    }

    /**
     * @param buildingNumberLow the buildingNumberLow to set
     */
    public void setBuildingNumberLow(Integer buildingNumberLow) {
        this.buildingNumberLow = buildingNumberLow;
    }
}
