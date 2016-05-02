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
 * (build: 2016-04-08 17:16:44 UTC)
 * on 2016-05-02 at 05:06:32 UTC 
 * Modify at your own risk.
 */

package com.cs407.onthedot.onthedotbackend.myApi;

/**
 * Service definition for MyApi (v1).
 *
 * <p>
 * This is an API
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link MyApiRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class MyApi extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.21.0 of the myApi library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://poised-team-129420.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "myApi/v1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport HTTP transport, which should normally be:
   *        <ul>
   *        <li>Google App Engine:
   *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
   *        <li>Android: {@code newCompatibleTransport} from
   *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
   *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
   *        </li>
   *        </ul>
   * @param jsonFactory JSON factory, which may be:
   *        <ul>
   *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
   *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
   *        <li>Android Honeycomb or higher:
   *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
   *        </ul>
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public MyApi(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  MyApi(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "clearTasks".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link ClearTasks#execute()} method to invoke the remote operation.
   *
   * @return the request
   */
  public ClearTasks clearTasks() throws java.io.IOException {
    ClearTasks result = new ClearTasks();
    initialize(result);
    return result;
  }

  public class ClearTasks extends MyApiRequest<Void> {

    private static final String REST_PATH = "clearTasks";

    /**
     * Create a request for the method "clearTasks".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link ClearTasks#execute()} method to invoke the remote operation. <p>
     * {@link
     * ClearTasks#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @since 1.13
     */
    protected ClearTasks() {
      super(MyApi.this, "POST", REST_PATH, null, Void.class);
    }

    @Override
    public ClearTasks setAlt(java.lang.String alt) {
      return (ClearTasks) super.setAlt(alt);
    }

    @Override
    public ClearTasks setFields(java.lang.String fields) {
      return (ClearTasks) super.setFields(fields);
    }

    @Override
    public ClearTasks setKey(java.lang.String key) {
      return (ClearTasks) super.setKey(key);
    }

    @Override
    public ClearTasks setOauthToken(java.lang.String oauthToken) {
      return (ClearTasks) super.setOauthToken(oauthToken);
    }

    @Override
    public ClearTasks setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ClearTasks) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ClearTasks setQuotaUser(java.lang.String quotaUser) {
      return (ClearTasks) super.setQuotaUser(quotaUser);
    }

    @Override
    public ClearTasks setUserIp(java.lang.String userIp) {
      return (ClearTasks) super.setUserIp(userIp);
    }

    @Override
    public ClearTasks set(String parameterName, Object value) {
      return (ClearTasks) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "getTasks".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link GetTasks#execute()} method to invoke the remote operation.
   *
   * @return the request
   */
  public GetTasks getTasks() throws java.io.IOException {
    GetTasks result = new GetTasks();
    initialize(result);
    return result;
  }

  public class GetTasks extends MyApiRequest<com.cs407.onthedot.onthedotbackend.myApi.model.TaskBeanCollection> {

    private static final String REST_PATH = "taskbeancollection";

    /**
     * Create a request for the method "getTasks".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link GetTasks#execute()} method to invoke the remote operation. <p>
     * {@link
     * GetTasks#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @since 1.13
     */
    protected GetTasks() {
      super(MyApi.this, "GET", REST_PATH, null, com.cs407.onthedot.onthedotbackend.myApi.model.TaskBeanCollection.class);
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public GetTasks setAlt(java.lang.String alt) {
      return (GetTasks) super.setAlt(alt);
    }

    @Override
    public GetTasks setFields(java.lang.String fields) {
      return (GetTasks) super.setFields(fields);
    }

    @Override
    public GetTasks setKey(java.lang.String key) {
      return (GetTasks) super.setKey(key);
    }

    @Override
    public GetTasks setOauthToken(java.lang.String oauthToken) {
      return (GetTasks) super.setOauthToken(oauthToken);
    }

    @Override
    public GetTasks setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetTasks) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetTasks setQuotaUser(java.lang.String quotaUser) {
      return (GetTasks) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetTasks setUserIp(java.lang.String userIp) {
      return (GetTasks) super.setUserIp(userIp);
    }

    @Override
    public GetTasks set(String parameterName, Object value) {
      return (GetTasks) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "insertBean".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link InsertBean#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.cs407.onthedot.onthedotbackend.myApi.model.TaskBean}
   * @return the request
   */
  public InsertBean insertBean(com.cs407.onthedot.onthedotbackend.myApi.model.TaskBean content) throws java.io.IOException {
    InsertBean result = new InsertBean(content);
    initialize(result);
    return result;
  }

  public class InsertBean extends MyApiRequest<com.cs407.onthedot.onthedotbackend.myApi.model.TaskBean> {

    private static final String REST_PATH = "taskbean";

    /**
     * Create a request for the method "insertBean".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link InsertBean#execute()} method to invoke the remote operation. <p>
     * {@link
     * InsertBean#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link com.cs407.onthedot.onthedotbackend.myApi.model.TaskBean}
     * @since 1.13
     */
    protected InsertBean(com.cs407.onthedot.onthedotbackend.myApi.model.TaskBean content) {
      super(MyApi.this, "POST", REST_PATH, content, com.cs407.onthedot.onthedotbackend.myApi.model.TaskBean.class);
    }

    @Override
    public InsertBean setAlt(java.lang.String alt) {
      return (InsertBean) super.setAlt(alt);
    }

    @Override
    public InsertBean setFields(java.lang.String fields) {
      return (InsertBean) super.setFields(fields);
    }

    @Override
    public InsertBean setKey(java.lang.String key) {
      return (InsertBean) super.setKey(key);
    }

    @Override
    public InsertBean setOauthToken(java.lang.String oauthToken) {
      return (InsertBean) super.setOauthToken(oauthToken);
    }

    @Override
    public InsertBean setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (InsertBean) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public InsertBean setQuotaUser(java.lang.String quotaUser) {
      return (InsertBean) super.setQuotaUser(quotaUser);
    }

    @Override
    public InsertBean setUserIp(java.lang.String userIp) {
      return (InsertBean) super.setUserIp(userIp);
    }

    @Override
    public InsertBean set(String parameterName, Object value) {
      return (InsertBean) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "storeTask".
   *
   * This request holds the parameters needed by the myApi server.  After setting any optional
   * parameters, call the {@link StoreTask#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.cs407.onthedot.onthedotbackend.myApi.model.TaskBean}
   * @return the request
   */
  public StoreTask storeTask(com.cs407.onthedot.onthedotbackend.myApi.model.TaskBean content) throws java.io.IOException {
    StoreTask result = new StoreTask(content);
    initialize(result);
    return result;
  }

  public class StoreTask extends MyApiRequest<Void> {

    private static final String REST_PATH = "storeTask";

    /**
     * Create a request for the method "storeTask".
     *
     * This request holds the parameters needed by the the myApi server.  After setting any optional
     * parameters, call the {@link StoreTask#execute()} method to invoke the remote operation. <p>
     * {@link
     * StoreTask#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link com.cs407.onthedot.onthedotbackend.myApi.model.TaskBean}
     * @since 1.13
     */
    protected StoreTask(com.cs407.onthedot.onthedotbackend.myApi.model.TaskBean content) {
      super(MyApi.this, "POST", REST_PATH, content, Void.class);
    }

    @Override
    public StoreTask setAlt(java.lang.String alt) {
      return (StoreTask) super.setAlt(alt);
    }

    @Override
    public StoreTask setFields(java.lang.String fields) {
      return (StoreTask) super.setFields(fields);
    }

    @Override
    public StoreTask setKey(java.lang.String key) {
      return (StoreTask) super.setKey(key);
    }

    @Override
    public StoreTask setOauthToken(java.lang.String oauthToken) {
      return (StoreTask) super.setOauthToken(oauthToken);
    }

    @Override
    public StoreTask setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (StoreTask) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public StoreTask setQuotaUser(java.lang.String quotaUser) {
      return (StoreTask) super.setQuotaUser(quotaUser);
    }

    @Override
    public StoreTask setUserIp(java.lang.String userIp) {
      return (StoreTask) super.setUserIp(userIp);
    }

    @Override
    public StoreTask set(String parameterName, Object value) {
      return (StoreTask) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link MyApi}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
    }

    /** Builds a new instance of {@link MyApi}. */
    @Override
    public MyApi build() {
      return new MyApi(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link MyApiRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setMyApiRequestInitializer(
        MyApiRequestInitializer myapiRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(myapiRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
