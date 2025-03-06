package org.wso2.carbon.identity.authorization.framework.model;

import java.util.ArrayList;
import java.util.List;

/**
 * todo
 */
public class BatchAuthorizationEvaluationResponse {

    private List<AuthorizationEvaluationResponse> response = new ArrayList<>();

    public List<AuthorizationEvaluationResponse> getRequests() {

        return response;
    }

    public void setRequests(List<AuthorizationEvaluationResponse> requests) {

        this.response = requests;
    }

    public void addResponseItem(AuthorizationEvaluationResponse requestsItem) {

        this.response.add(requestsItem);
    }
}
