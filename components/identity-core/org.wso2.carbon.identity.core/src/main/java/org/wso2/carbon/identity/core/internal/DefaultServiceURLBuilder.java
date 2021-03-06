/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.core.internal;

import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.core.ServiceURL;
import org.wso2.carbon.identity.core.ServiceURLBuilder;
import org.wso2.carbon.identity.core.URLBuilderException;
import org.wso2.carbon.identity.core.util.IdentityCoreConstants;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.NetworkUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Implementation for {@link ServiceURLBuilder}.
 * Builder for Service URL instances.
 */
public class DefaultServiceURLBuilder implements ServiceURLBuilder {

    private String fragment;
    private String tenantDomain;
    private String[] urlPaths;
    private Map<String, String> parameters = new HashMap<>();
    private Map<String, String> fragmentParams = new HashMap<>();

    private class ServiceURLImpl implements ServiceURL {

        private String protocol;
        private String hostName;
        private int port;
        private String proxyContextPath;
        private String urlPath;
        private Map<String, String> parameters;
        private String fragment;
        private String absoluteUrl;
        private String relativePublicUrl;
        private String relativeInternalUrl;

        private ServiceURLImpl(String protocol, String hostName, int port, String proxyContextPath, String urlPath,
                               Map<String, String> parameters, String fragment)
                throws URLBuilderException {

            this.protocol = protocol;
            this.hostName = hostName;
            this.port = port;
            this.proxyContextPath = proxyContextPath;
            this.urlPath = urlPath;
            this.parameters = parameters;
            this.fragment = fragment;
            this.absoluteUrl = fetchAbsoluteUrl();
            this.relativePublicUrl = fetchRelativePublicUrl();
            this.relativeInternalUrl = fetchRelativeInternalUrl();
        }

        /**
         * Returns the protocol of Service URL.
         *
         * @return String of the protocol.
         */
        @Override
        public String getProtocol() {

            return protocol;
        }

        /**
         * Returns the host name of Service URL.
         *
         * @return String of the host name.
         */
        @Override
        public String getHostName() {

            return hostName;
        }

        /**
         * Returns the port of Service URL.
         *
         * @return value of the port.
         */
        @Override
        public int getPort() {

            return port;
        }

        /**
         * Returns the Url path of Service URL.
         *
         * @return String of the url path.
         */
        @Override
        public String getPath() {

            return urlPath;
        }

        /**
         * Returns the parameter value when the key is provided.
         *
         * @param key Key of the parameter.
         * @return The value of the parameter.
         */
        @Override
        public String getParameter(String key) {

            return parameters.get(key);
        }

        /**
         * Returns a map of the parameters.
         *
         * @return The parameters.
         */
        @Override
        public Map<String, String> getParameters() {

            return Collections.unmodifiableMap(parameters);
        }

        /**
         * Returns decoded fragment from the url.
         *
         * @return The decoded fragment.
         */
        @Override
        public String getFragment() {

            return fragment;
        }

        /**
         * Returns the tenant domain of the service URL.
         *
         * @return The tenant domain.
         */
        @Override
        public String getTenantDomain() {

            return tenantDomain;
        }

        /**
         * Returns the absolute URL used for Identity Server internal calls.
         * Concatenate the protocol, host name, port, proxy context path, web context root, url context, query params and
         * the fragment to return the internal absolute URL.
         *
         * @return The internal absolute URL from the Service URL instance.
         */
        @Override
        public String getAbsoluteInternalURL() {

            return absoluteUrl;
        }

        /**
         * Returns the proxy server url when the Identity Server is fronted with a proxy.
         * Concatenate the protocol, host name, port, proxy context path, web context root, url context, query params and
         * the fragment to return the public absolute URL.
         *
         * @return The public absolute URL from the Service URL instance.
         */
        @Override
        public String getAbsolutePublicURL() {

            return absoluteUrl;
        }

        /**
         * Returns the relative url, relative to the proxy the server is fronted with.
         * Concatenate the url context, query params and the fragment to the url path to return the public relative URL.
         *
         * @return The public relative URL from the Service URL instance.
         */
        @Override
        public String getRelativePublicURL() {

            return relativePublicUrl;
        }

        /**
         * Returns the relative url, relative to the internal server host.
         * Concatenate the query params and the fragment to the url path to return the internal relative URL.
         *
         * @return The internal relative URL from the Service URL instance.
         */
        @Override
        public String getRelativeInternalURL() {

            return relativeInternalUrl;
        }

        private String fetchAbsoluteUrl() throws URLBuilderException {

            StringBuilder absoluteUrl = new StringBuilder();
            absoluteUrl.append(protocol).append("://");
            absoluteUrl.append(hostName.toLowerCase());
            // If it's well known HTTPS port, skip adding port.
            if (port != IdentityCoreConstants.DEFAULT_HTTPS_PORT) {
                absoluteUrl.append(":").append(port);
            }
            absoluteUrl.append(fetchRelativePublicUrl());
            return absoluteUrl.toString();
        }

        private String fetchRelativePublicUrl() throws URLBuilderException {

            StringBuilder relativeUrl = new StringBuilder();
            appendContextToUri(relativeUrl, proxyContextPath);
            appendContextToUri(relativeUrl, urlPath);
            String resolvedParamsString = getResolvedParamString(parameters);
            appendParamsToUri(relativeUrl, resolvedParamsString, "?");
            appendParamsToUri(relativeUrl, fragment, "#");
            return relativeUrl.toString();
        }

        private String fetchRelativeInternalUrl() throws URLBuilderException {

            StringBuilder relativeUrl = new StringBuilder();
            appendContextToUri(relativeUrl, urlPath);
            String resolvedParamsString = getResolvedParamString(parameters);
            appendParamsToUri(relativeUrl, resolvedParamsString, "?");
            appendParamsToUri(relativeUrl, fragment, "#");
            return relativeUrl.toString();
        }
    }

    /**
     * Returns {@link ServiceURLBuilder} appended the URL path.
     *
     * @param paths Context paths. Can provide multiple context paths with a comma separated string.
     * @return {@link ServiceURLBuilder}.
     */
    @Override
    public ServiceURLBuilder addPath(String... paths) {

        this.urlPaths = paths;
        return this;
    }

    /**
     * Returns a ServiceURL with the protocol, hostname, port, proxy context path, a web context
     * root and the tenant domain (appended if required).
     *
     * @return {@link ServiceURL}.
     * @throws URLBuilderException If error occurred while constructing the URL.
     */
    @Override
    public ServiceURL build() throws URLBuilderException {

        String protocol = fetchProtocol();
        String hostName = null;
        hostName = fetchHostName();
        int port = fetchPort();
        String proxyContextPath = ServerConfiguration.getInstance().getFirstProperty(IdentityCoreConstants
                .PROXY_CONTEXT_PATH);
        String resolvedUrlContext = buildUrlPath(urlPaths);
        String resolvedFragment = buildFragment(fragment, fragmentParams);
        tenantDomain = resolveTenantDomain();

        StringBuilder resolvedUrlStringBuilder = new StringBuilder();

        if (IdentityTenantUtil.isTenantQualifiedUrlsEnabled()) {
            if (StringUtils.isNotBlank(tenantDomain) &&
                    !MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {
                resolvedUrlStringBuilder.append("/t/").append(tenantDomain);
            }
        }

        if (StringUtils.isNotBlank(resolvedUrlContext)) {
            if (resolvedUrlContext.trim().charAt(0) != '/') {
                resolvedUrlStringBuilder.append("/").append(resolvedUrlContext.trim());
            } else {
                resolvedUrlStringBuilder.append(resolvedUrlContext.trim());
            }
        }

        return new ServiceURLImpl(protocol, hostName, port, proxyContextPath, resolvedUrlStringBuilder.toString(),
                parameters,
                resolvedFragment);
    }

    /**
     * Returns {@link ServiceURLBuilder} appended with other parameters. Such parameters should be
     * entered as <k,v> pairs.
     *
     * @param key   Key.
     * @param value Value.
     * @return {@link ServiceURLBuilder}.
     */
    @Override
    public ServiceURLBuilder addParameter(String key, String value) {

        parameters.put(key, value);
        return this;
    }

    /**
     * Returns {@link ServiceURLBuilder} appended with a fragment.
     *
     * @param fragment Fragment.
     * @return {@link ServiceURLBuilder}.
     */
    @Override
    public ServiceURLBuilder setFragment(String fragment) {

        this.fragment = fragment;
        return this;
    }

    /**
     * Returns {@link ServiceURLBuilder} appended with parameters. Such parameters should be
     * entered as <k,v> pairs. These parameters will get appended with an "&".
     *
     * @param key   Key.
     * @param value Value.
     * @return {@link ServiceURLBuilder}.
     */
    @Override
    public ServiceURLBuilder addFragmentParameter(String key, String value) {

        fragmentParams.put(key, value);
        return this;
    }

    private String resolveTenantDomain() {

        String tenantDomain = IdentityTenantUtil.getTenantDomainFromContext();
        if (StringUtils.isBlank(tenantDomain)) {
            tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        }
        return tenantDomain;
    }

    private String buildFragment(String fragment, Map<String, String> fragmentParams) {

        if (StringUtils.isNotBlank(fragment)) {
            return fragment;
        } else {
            return getResolvedParamString(fragmentParams);
        }
    }

    private String getResolvedParamString(Map<String, String> parameters) {

        StringJoiner joiner = new StringJoiner("&");
        if (MapUtils.isNotEmpty(parameters)) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                StringBuilder paramBuilder = new StringBuilder();
                paramBuilder.append(entry.getKey()).append("=").append(entry.getValue());
                joiner.add(paramBuilder.toString());
            }
        }
        return joiner.toString();
    }

    private String buildUrlPath(String[] urlPaths) {

        StringBuilder urlPathBuilder = new StringBuilder();
        if (ArrayUtils.isNotEmpty(urlPaths)) {
            for (String path : urlPaths) {
                if (StringUtils.isNotBlank(path)) {
                    if (path.endsWith("/")) {
                        path = path.substring(0, path.length() - 1);
                    }
                    if (path.startsWith("/")) {
                        path = path.substring(1);
                    }
                    urlPathBuilder.append(path).append("/");
                }
            }
            if (urlPathBuilder.length() > 0 && urlPathBuilder.charAt(urlPathBuilder.length() - 1) == '/') {
                urlPathBuilder.setLength(urlPathBuilder.length() - 1);
            }
        }
        return urlPathBuilder.toString();
    }

    private String fetchProtocol() {

        return CarbonUtils.getManagementTransport();
    }

    private String fetchHostName() throws URLBuilderException {

        String hostName = ServerConfiguration.getInstance().getFirstProperty(IdentityCoreConstants.HOST_NAME);
        try {
            if (StringUtils.isBlank(hostName)) {
                hostName = NetworkUtils.getLocalHostname();
            }
        } catch (SocketException e) {
            throw new URLBuilderException(String.format("Error while trying to resolve the hostname %s from the " +
                    "system", hostName), e);
        }

        if (StringUtils.isNotBlank(hostName) && hostName.endsWith("/")) {
            hostName = hostName.substring(0, hostName.length() - 1);
        }
        return hostName;
    }

    private Integer fetchPort() {

        String mgtTransport = CarbonUtils.getManagementTransport();
        AxisConfiguration axisConfiguration = IdentityCoreServiceComponent.getConfigurationContextService().
                getServerConfigContext().getAxisConfiguration();
        int port = CarbonUtils.getTransportProxyPort(axisConfiguration, mgtTransport);
        if (port <= 0) {
            port = CarbonUtils.getTransportPort(axisConfiguration, mgtTransport);
        }
        return port;
    }

    private void appendParamsToUri(StringBuilder serverUrl, String resolvedParamsString, String delimiter)
            throws URLBuilderException {

        if (serverUrl.length() > 0 && serverUrl.charAt(serverUrl.length() - 1) == '/') {
            serverUrl.setLength(serverUrl.length() - 1);
        }
        if (StringUtils.isNotBlank(resolvedParamsString)) {
            try {
                serverUrl.append(delimiter).append(URLEncoder.encode(resolvedParamsString,
                        StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                throw new URLBuilderException(String.format("Error while trying to build the url. %s is not supported" +
                        ".", StandardCharsets.UTF_8.name()), e);
            }
        }
    }

    private void appendContextToUri(StringBuilder serverUrl, String contextPath) {

        if (StringUtils.isNotBlank(contextPath)) {
            if (contextPath.endsWith("/")) {
                contextPath = contextPath.substring(0, contextPath.length() - 1);
            }
            if (StringUtils.isNotBlank(contextPath) && contextPath.trim().charAt(0) != '/') {
                serverUrl.append("/").append(contextPath.trim());
            } else {
                serverUrl.append(contextPath.trim());
            }
        }
    }
}
