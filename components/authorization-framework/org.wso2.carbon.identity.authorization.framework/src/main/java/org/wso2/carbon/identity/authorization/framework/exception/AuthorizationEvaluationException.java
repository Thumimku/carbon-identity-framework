/*
 *  Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.identity.authorization.framework.exception;

import org.wso2.carbon.identity.base.IdentityException;

/**
 * Exception class for handling client attestation metadata management errors.
 * todo:
 */
public class AuthorizationEvaluationException extends IdentityException {

    /**
     * Constructs a new `AuthorizationException` with the specified error message.
     *
     * @param message The error message.
     */
    public AuthorizationEvaluationException(String message) {

        super(message);
    }

    /**
     * Constructs a new `AuthorizationException` with the specified error code and error message.
     *
     * @param errorCode The error code.
     * @param message   The error message.
     */
    public AuthorizationEvaluationException(String errorCode, String message) {

        super(errorCode, message);
    }

    /**
     * Constructs a new `AuthorizationException` with the specified error message and a throwable cause.
     *
     * @param message The error message.
     * @param cause   The throwable cause of the exception.
     */
    public AuthorizationEvaluationException(String message, Throwable cause) {

        super(message, cause);
    }

    /**
     * Constructs a new `AuthorizationException` with the specified error code, error message,
     * and a throwable cause.
     *
     * @param errorCode The error code.
     * @param message   The error message.
     * @param cause     The throwable cause of the exception.
     */
    public AuthorizationEvaluationException(String errorCode, String message, Throwable cause) {

        super(errorCode, message, cause);
    }
}

