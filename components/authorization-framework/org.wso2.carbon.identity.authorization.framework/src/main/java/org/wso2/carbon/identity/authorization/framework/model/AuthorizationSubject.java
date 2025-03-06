package org.wso2.carbon.identity.authorization.framework.model;

import java.util.Map;

/**
 * todo
 */
public class AuthorizationSubject {

    private String type;
    private String id;
    private Map<String, Object> properties = null;

    public AuthorizationSubject() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
