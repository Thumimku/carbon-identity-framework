package org.wso2.carbon.identity.authorization.framework.service;

import org.wso2.carbon.identity.authorization.framework.exception.AuthorizationEvaluationException;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationEvaluationRequest;
import org.wso2.carbon.identity.authorization.framework.model.AuthorizationEvaluationResponse;
import org.wso2.carbon.identity.authorization.framework.model.BatchAuthorizationEvaluationRequest;
import org.wso2.carbon.identity.authorization.framework.model.BatchAuthorizationEvaluationResponse;

/**
 * todo:
 */
public interface AuthorizationEvaluationService {

    public AuthorizationEvaluationResponse evaluate(AuthorizationEvaluationRequest authorizationEvaluationRequest)
            throws AuthorizationEvaluationException;

    public BatchAuthorizationEvaluationResponse batchEvaluate(BatchAuthorizationEvaluationRequest
                                                                 batchAuthorizationEvaluationRequest)
            throws AuthorizationEvaluationException;
}
