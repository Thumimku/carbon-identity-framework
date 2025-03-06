package org.wso2.carbon.identity.authorization.framework.model;

import java.util.Map;

/**
 * todo
 */
public class AuthorizationAction {

    private String name;
    private Map<String, Object> properties = null;

    public AuthorizationAction() {

    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
