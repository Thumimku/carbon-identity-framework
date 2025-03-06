package org.wso2.carbon.identity.authorization.framework.model;

import java.util.ArrayList;
import java.util.List;

/**
 * todo
 */
public class BatchAuthorizationEvaluationRequest {

    private List<AuthorizationEvaluationRequest> requests = new ArrayList<>();

    public List<AuthorizationEvaluationRequest> getRequests() {

        return requests;
    }

    public void setRequests(List<AuthorizationEvaluationRequest> requests) {

        this.requests = requests;
    }

    public void addRequestsItem(AuthorizationEvaluationRequest requestsItem) {

        this.requests.add(requestsItem);
    }
}
