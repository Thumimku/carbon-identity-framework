package org.wso2.carbon.identity.authorization.framework.model;

import java.util.Map;

/**
 * todo
 */
public class AuthorizationEvaluationResponse {

    private boolean decision;
    private Map<String, Object> context;

    public boolean isDecision() {

        return decision;
    }

    public void setDecision(boolean decision) {

        this.decision = decision;
    }

    public Map<String, Object> getContext() {

        return context;
    }

    public void setContext(Map<String, Object> context) {

        this.context = context;
    }
}
