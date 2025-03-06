package org.wso2.carbon.identity.authorization.framework.service;

import org.wso2.carbon.identity.authorization.framework.exception.AuthorizationEvaluationException;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationEvaluationRequest;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationEvaluationResponse;
import org.wso2.carbon.identity.authorization.framework.model.BatchAuthorizationEvaluationRequest;
import org.wso2.carbon.identity.authorization.framework.model.BatchAuthorizationEvaluationResponse;

/**
 * todo:
 */
public class AuthorizationEvaluationImpl implements AuthorizationEvaluationService {


    @Override
    public AuthorizationEvaluationResponse evaluate(AuthorizationEvaluationRequest authorizationEvaluationRequest)
            throws AuthorizationEvaluationException {
        AuthorizationEvaluationResponse authorizationEvaluationResponse = new AuthorizationEvaluationResponse();
        authorizationEvaluationResponse.setDecision(true);
        return authorizationEvaluationResponse;
    }

    @Override
    public BatchAuthorizationEvaluationResponse batchEvaluate(BatchAuthorizationEvaluationRequest
                                                                          batchAuthorizationEvaluationRequest)
            throws AuthorizationEvaluationException {
        return null;
    }
}
