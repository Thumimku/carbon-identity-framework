package org.wso2.carbon.identity.authorization.framework.model;

import java.util.Map;

/**
 * todo
 */
public class AuthorizationEvaluationRequest {

    private AuthorizationSubject authorizationSubject;
    private AuthorizationAction authorizationAction;
    private AuthorizationResource resource;
    private Map<String, Object> context;

    public AuthorizationEvaluationRequest() {

    }

    public AuthorizationSubject getAuthorizationSubject() {

        return authorizationSubject;
    }

    public void setAuthorizationSubject(AuthorizationSubject authorizationSubject) {

        this.authorizationSubject = authorizationSubject;
    }

    public AuthorizationAction getAuthorizationAction() {

        return authorizationAction;
    }

    public void setAuthorizationAction(AuthorizationAction authorizationAction) {

        this.authorizationAction = authorizationAction;
    }

    public AuthorizationResource getResource() {

        return resource;
    }

    public void setResource(AuthorizationResource resource) {

        this.resource = resource;
    }

    public Map<String, Object> getContext() {

        return context;
    }

    public void setContext(Map<String, Object> context) {

        this.context = context;
    }
}
