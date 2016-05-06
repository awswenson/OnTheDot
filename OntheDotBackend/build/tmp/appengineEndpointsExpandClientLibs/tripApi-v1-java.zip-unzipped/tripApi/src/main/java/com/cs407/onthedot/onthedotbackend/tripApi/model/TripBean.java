/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2016-05-04 15:59:39 UTC)
 * on 2016-05-06 at 02:04:16 UTC 
 * Modify at your own risk.
 */

package com.cs407.onthedot.onthedotbackend.tripApi.model;

/**
 * Model definition for TripBean.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the tripApi. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class TripBean extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String date;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double destLat;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double destLong;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String friendsList;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double startLat;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double startLong;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean tripComplete;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDate() {
    return date;
  }

  /**
   * @param date date or {@code null} for none
   */
  public TripBean setDate(java.lang.String date) {
    this.date = date;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getDestLat() {
    return destLat;
  }

  /**
   * @param destLat destLat or {@code null} for none
   */
  public TripBean setDestLat(java.lang.Double destLat) {
    this.destLat = destLat;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getDestLong() {
    return destLong;
  }

  /**
   * @param destLong destLong or {@code null} for none
   */
  public TripBean setDestLong(java.lang.Double destLong) {
    this.destLong = destLong;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getFriendsList() {
    return friendsList;
  }

  /**
   * @param friendsList friendsList or {@code null} for none
   */
  public TripBean setFriendsList(java.lang.String friendsList) {
    this.friendsList = friendsList;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public TripBean setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getStartLat() {
    return startLat;
  }

  /**
   * @param startLat startLat or {@code null} for none
   */
  public TripBean setStartLat(java.lang.Double startLat) {
    this.startLat = startLat;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getStartLong() {
    return startLong;
  }

  /**
   * @param startLong startLong or {@code null} for none
   */
  public TripBean setStartLong(java.lang.Double startLong) {
    this.startLong = startLong;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getTripComplete() {
    return tripComplete;
  }

  /**
   * @param tripComplete tripComplete or {@code null} for none
   */
  public TripBean setTripComplete(java.lang.Boolean tripComplete) {
    this.tripComplete = tripComplete;
    return this;
  }

  @Override
  public TripBean set(String fieldName, Object value) {
    return (TripBean) super.set(fieldName, value);
  }

  @Override
  public TripBean clone() {
    return (TripBean) super.clone();
  }

}
