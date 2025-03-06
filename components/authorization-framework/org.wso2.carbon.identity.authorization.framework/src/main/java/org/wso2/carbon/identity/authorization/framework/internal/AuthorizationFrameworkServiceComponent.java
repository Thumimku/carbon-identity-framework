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

package org.wso2.carbon.identity.authorization.framework.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.application.mgt.ApplicationManagementService;

/**
 * OSGi declarative services component which handled registration and un-registration of
 * AuthorizationFrameworkServiceComponent.
 * Todo:
 */

@Component(
        name = "identity.authorization.framework.component",
        immediate = true
)
public class AuthorizationFrameworkServiceComponent {

    private static final Log LOG = LogFactory.getLog(AuthorizationFrameworkServiceComponent.class);

    @Activate
    protected void activate(ComponentContext context) {

        try {

//            context.getBundleContext().registerService(AuthorizationEvaluationService.class.getName(),
//                    new AuthorizationEvaluationImpl(), null);
            LOG.info("mathu authoization FW deployed");

            if (LOG.isDebugEnabled()) {
                LOG.debug("Client Attestation Service Component deployed.");
            }

        } catch (Throwable throwable) {
            LOG.error("Error while activating Client Attestation Service Component.", throwable);
        }
    }

//    /**
//     * Loads configurations for the Client Attestation Service.
//     */
//    private void loadConfigs() {
//
//        // Set the Apple attestation root certificate and revocation check status
//        ClientAttestationMgtDataHolder.getInstance()
//                .setAppleAttestationRootCertificate(getAppleAttestationRootCertificate());
//        ClientAttestationMgtDataHolder.getInstance()
//                .setAppleAttestationRevocationCheckEnabled(loadAppleAttestationRevocationCheckEnabled());
//    }

    @Deactivate
    protected void deactivate(ComponentContext context) {

        LOG.info("mathu authoization FW deactivated");

        if (LOG.isDebugEnabled()) {
            LOG.debug("Input Validation service component deactivated.");
        }
    }

    @Reference(
            service = ApplicationManagementService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetApplicationManagement"
    )
    public void setApplicationManagement(ApplicationManagementService applicationManagement) {

        AuthorizationFrameworkDataHolder.getInstance().setApplicationManagementService(applicationManagement);
    }

    public void unsetApplicationManagement(ApplicationManagementService applicationManagementService) {

        AuthorizationFrameworkDataHolder.getInstance().setApplicationManagementService(null);
    }
}
