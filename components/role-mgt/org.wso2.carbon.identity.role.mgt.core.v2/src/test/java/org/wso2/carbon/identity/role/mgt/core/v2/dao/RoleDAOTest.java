package org.wso2.carbon.identity.role.mgt.core.v2.dao;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.common.testng.WithCarbonHome;
import org.wso2.carbon.identity.core.util.IdentityCoreConstants;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.role.mgt.core.v2.Permission;
import org.wso2.carbon.identity.role.mgt.core.v2.RoleBasicInfo;
import org.wso2.carbon.user.api.RealmConfiguration;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.util.UserCoreUtil;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@WithCarbonHome
@PrepareForTest({IdentityDatabaseUtil.class, IdentityTenantUtil.class, IdentityUtil.class, UserCoreUtil.class,
        CarbonContext.class, RoleDAOImpl.class})
@PowerMockIgnore("org.mockito.*")
public class RoleDAOTest extends PowerMockTestCase {


    private static final int SAMPLE_TENANT_ID = 1;
    private static final String SAMPLE_TENANT_DOMAIN = "wso2.com";
    private static final String DB_NAME = "ROLE_DB";
    private static final String ORGANIZATION_AUD  = "Organization";
    private static final String APPLICATION_AUD  = "Application";
    private static Map<String, BasicDataSource> dataSourceMap = new HashMap<>();
    private RoleDAO roleDAO;
    private List<String> userNamesList = new ArrayList<>();
    private List<String> emptyList = new ArrayList<>();
    private List<String> groupNamesList = new ArrayList<>();
    private Map<String, String> groupNamesMap = new HashMap<>();
    private Map<String, String> emptyMap = new HashMap<>();
    private Map<String, String> groupIdsMap = new HashMap<>();
    private List<String> userIDsList = new ArrayList<>();
    private List<String> groupIDsList = new ArrayList<>();
    private List<Permission> permissions = new ArrayList<>();

    @Mock
    UserRealm mockUserRealm;

    @BeforeMethod
    public void setUp() throws Exception {

        userNamesList.add("user1");
        userNamesList.add("user2");
        groupNamesList.add("group2");
        groupNamesList.add("group1");

        groupNamesMap.put("groupID2", "group2");
        groupNamesMap.put("groupID1", "group1");
        groupIdsMap.put("group2", "groupID2");
        groupIdsMap.put("group1", "groupID1");
        userIDsList.add("userID1");
        userIDsList.add("userID2");
        groupIDsList.add("groupID1");
        groupIDsList.add("groupID2");
        permissions.add(new Permission("read", "read"));
        permissions.add(new Permission("write", "write"));
        mockStatic(IdentityDatabaseUtil.class);
        mockStatic(IdentityTenantUtil.class);
        initializeDataSource(getFilePath("h2.sql"));
        populateData();
    }

    @AfterMethod
    public void tearDown() throws Exception {

        userNamesList = new ArrayList<>();
        userIDsList = new ArrayList<>();
        groupIdsMap = new HashMap<>();
        groupNamesMap = new HashMap<>();
        groupNamesList = new ArrayList<>();
        groupIDsList = new ArrayList<>();
        permissions = new ArrayList<>();
        clearDataSource();
    }

    @Test
    public void testAddOrgRole() throws Exception {

        try (Connection connection1 = getConnection();
             Connection connection2 = getConnection();
             Connection connection3 = getConnection();
             Connection connection4 = getConnection()) {

            roleDAO = spy(RoleMgtDAOFactory.getInstance().getRoleDAO());
            mockCacheClearing();
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection1);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection2);
            addRole("role1", ORGANIZATION_AUD, "test-org-id");
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection3);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection4);
            doCallRealMethod().when(roleDAO, "isExistingRoleName", anyString(), anyString(), anyString(),
                    anyString());
            assertTrue(roleDAO.isExistingRoleName("role1", ORGANIZATION_AUD, "test-org-id",
                    SAMPLE_TENANT_DOMAIN));
        }
    }

    @Test
    public void testAddAppRole() throws Exception {

        try (Connection connection1 = getConnection();
             Connection connection2 = getConnection();
             Connection connection3 = getConnection();
             Connection connection4 = getConnection()) {

            roleDAO = spy(RoleMgtDAOFactory.getInstance().getRoleDAO());
            mockCacheClearing();
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection1);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection2);
            addRole("role1", APPLICATION_AUD, "test-app-id");
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection3);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection4);
            doCallRealMethod().when(roleDAO, "isExistingRoleName", anyString(), anyString(), anyString(),
                    anyString());
            assertTrue(roleDAO.isExistingRoleName("role1", APPLICATION_AUD, "test-app-id",
                    SAMPLE_TENANT_DOMAIN));
        }
    }

    @Test
    public void testGetRoles() throws Exception {

        try (Connection connection1 = getConnection();
             Connection connection2 = getConnection();
             Connection connection3 = getConnection();
             Connection connection4 = getConnection();
             Connection connection5 = getConnection();
             Connection connection6 = getConnection();
             Connection connection7 = getConnection();
             Connection connection8 = getConnection()) {

            roleDAO = spy(RoleMgtDAOFactory.getInstance().getRoleDAO());
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection1);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection2);
            addRole("role1", APPLICATION_AUD, "test-app-id");
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection3);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection4);
            addRole("role2", APPLICATION_AUD, "test-app-id");
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection5);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection6);
            addRole("role3", ORGANIZATION_AUD, "test-org-id");

            List<String> expectedRoles = new ArrayList<>();
            expectedRoles.add("role2");
            expectedRoles.add("role3");

            mockRealmConfiguration();
            mockStatic(UserCoreUtil.class);
            when(UserCoreUtil.isEveryoneRole(anyString(), any(RealmConfiguration.class))).thenReturn(false);

            when(IdentityUtil.getDefaultItemsPerPage()).thenReturn(IdentityCoreConstants.DEFAULT_ITEMS_PRE_PAGE);
            when(IdentityUtil.getMaximumItemPerPage()).thenReturn(IdentityCoreConstants.DEFAULT_MAXIMUM_ITEMS_PRE_PAGE);
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection7);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection8);
            doCallRealMethod().when(IdentityUtil.class, "extractDomainFromName", anyString());
            doCallRealMethod().when(UserCoreUtil.class, "removeDomainFromName", anyString());
            List<RoleBasicInfo> roles = roleDAO.getRoles(2, 1, null, null,
                    SAMPLE_TENANT_DOMAIN);
            Assert.assertEquals(getRoleNamesList(roles), expectedRoles);
        }
    }

    @Test
    public void testGetPermissionListOfRole() throws Exception {

        try (Connection connection1 = getConnection();
             Connection connection2 = getConnection();
             Connection connection3 = getConnection();
             Connection connection4 = getConnection()) {

            roleDAO = spy(RoleMgtDAOFactory.getInstance().getRoleDAO());
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection1);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection2);
            RoleBasicInfo role = addRole("role1", APPLICATION_AUD, "test-app-id");
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection3);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection4);
            List<Permission> rolePermissions = roleDAO.getPermissionListOfRole(role.getId(), SAMPLE_TENANT_DOMAIN);
            Assert.assertEquals(getPermissionNameList(rolePermissions), getPermissionNameList(permissions));
        }
    }

    @Test
    public void testUpdatePermissionListOfRole() throws Exception {

        try (Connection connection1 = getConnection();
             Connection connection2 = getConnection();
             Connection connection3 = getConnection();
             Connection connection4 = getConnection();
             Connection connection5 = getConnection();
             Connection connection6 = getConnection()) {

            roleDAO = spy(RoleMgtDAOFactory.getInstance().getRoleDAO());
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection1);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection2);
            RoleBasicInfo role = addRole("role1", APPLICATION_AUD, "test-app-id");
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection3);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection4);
            List<Permission> newPermissions = new ArrayList<>();
            newPermissions.add(new Permission("view", "view"));
            newPermissions.add(new Permission("update", "update"));
            roleDAO.updatePermissionListOfRole(role.getId(), newPermissions, permissions, SAMPLE_TENANT_DOMAIN);
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection5);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection6);
            List<Permission> rolePermissions = roleDAO.getPermissionListOfRole(role.getId(), SAMPLE_TENANT_DOMAIN);
            Assert.assertEquals(getPermissionNameList(rolePermissions), getPermissionNameList(newPermissions));
        }
    }

    @Test
    public void testGetRoleBasicInfoById() throws Exception {

        try (Connection connection1 = getConnection();
             Connection connection2 = getConnection();
             Connection connection3 = getConnection();
             Connection connection4 = getConnection()) {

            roleDAO = spy(RoleMgtDAOFactory.getInstance().getRoleDAO());
            mockCacheClearing();
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection1);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection2);
            RoleBasicInfo  role = addRole("role1", APPLICATION_AUD, "test-app-id");
            when(IdentityDatabaseUtil.getUserDBConnection(anyBoolean())).thenReturn(connection3);
            when(IdentityDatabaseUtil.getDBConnection(anyBoolean())).thenReturn(connection4);
            RoleBasicInfo roleBasicInfo =  roleDAO.getRoleBasicInfoById(role.getId(), SAMPLE_TENANT_DOMAIN);
            assertEquals(roleBasicInfo.getAudience(), APPLICATION_AUD);
            assertEquals(roleBasicInfo.getAudienceId(), "test-app-id");
            assertEquals(roleBasicInfo.getAudienceName(), "TEST_APP_NAME");
        }
    }

    private RoleBasicInfo addRole(String roleName, String audience, String audienceId) throws Exception {

        mockCacheClearing();
        mockStatic(IdentityUtil.class);
        when(IdentityUtil.getPrimaryDomainName()).thenReturn("PRIMARY");
        doCallRealMethod().when(IdentityUtil.class, "extractDomainFromName", anyString());
        doReturn(new ArrayList<>()).when(roleDAO, "getUserNamesByIDs", anyCollection(), anyString());
        doReturn(new HashMap<>()).when(roleDAO, "getGroupNamesByIDs", anyCollection(), anyString());
        doReturn(false).when(roleDAO, "isExistingRoleName", anyString(), anyString(),
                anyString(), anyString());
        doReturn(groupIdsMap).when(roleDAO, "getGroupIDsByNames", anyCollection(), anyString());
        doReturn(roleName).when(roleDAO, "getRoleNameByID", anyString(), anyString());
        doReturn("test-org").when(roleDAO, "getOrganizationName", anyString());
        when(IdentityTenantUtil.getTenantId(anyString())).thenReturn(SAMPLE_TENANT_ID);
        return roleDAO.addRole(roleName, userIDsList, groupIDsList, permissions, audience, audienceId,
                SAMPLE_TENANT_DOMAIN);
    }

    private void mockCacheClearing() throws Exception {

        doNothing().when(roleDAO, "clearUserRolesCache", anyString(), anyInt());
    }

    private void mockRealmConfiguration() throws UserStoreException {

        mockStatic(CarbonContext.class);
        CarbonContext carbonContext = mock(CarbonContext.class);
        when(CarbonContext.getThreadLocalCarbonContext()).thenReturn(carbonContext);
        when(CarbonContext.getThreadLocalCarbonContext().getUserRealm()).thenReturn(mockUserRealm);
        RealmConfiguration realmConfiguration = mock(RealmConfiguration.class);
        when(mockUserRealm.getRealmConfiguration()).thenReturn(realmConfiguration);
    }

    private List<String> getRoleNamesList(List<RoleBasicInfo> roles) {

        List<String> roleNames = new ArrayList<>();
        for (RoleBasicInfo role : roles) {
            roleNames.add(role.getName());
        }
        return roleNames.stream().sorted().collect(Collectors.toList());
    }

    private List<String> getPermissionNameList(List<Permission> permissions) {

        List<String> permissionNames = new ArrayList<>();
        for (Permission permission : permissions) {
            permissionNames.add(permission.getName());
        }
        return permissionNames.stream().sorted().collect(Collectors.toList());
    }

    private void initializeDataSource(String scriptPath) throws Exception {

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUsername("username");
        dataSource.setPassword("password");
        dataSource.setUrl("jdbc:h2:mem:" + RoleDAOTest.DB_NAME);

        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeUpdate("RUNSCRIPT FROM '" + scriptPath + "'");
        }
        dataSourceMap.put(RoleDAOTest.DB_NAME, dataSource);
    }

    private String getFilePath(String fileName) {
        if (StringUtils.isNotBlank(fileName)) {
            return Paths.get(System.getProperty("user.dir"), "src", "test", "resources", "dbscripts", fileName)
                    .toString();
        }
        return null;
    }

    private void populateData() throws Exception {

        String domainDataSQL = "INSERT INTO UM_DOMAIN (UM_DOMAIN_ID, UM_DOMAIN_NAME, UM_TENANT_ID) VALUES "
                + "(1,'PRIMARY',-1234), (2,'SYSTEM',-1234), (3,'INTERNAL',-1234), (4,'APPLICATION',-1234), "
                + "(5,'WORKFLOW',-1234), (6,'PRIMARY',1), (7,'SYSTEM',1), (8,'INTERNAL',1), (9,'APPLICATION',1), "
                + "(10,'WORKFLOW',1)";
        String aPIResourceSQL = "INSERT INTO API_RESOURCE (ID, NAME, IDENTIFIER, TENANT_ID, DESCRIPTION, TYPE," +
                " REQUIRES_AUTHORIZATION) VALUES (1,'DOC','DOC',1,'DOC','RBAC',true);";
        String scopeSQL = "INSERT INTO SCOPE (ID,API_ID,NAME,DISPLAY_NAME,TENANT_ID,DESCRIPTION) VALUES " +
                "(1,1,'read','read',1,'read'), (2,1,'write','write',1,'write'), (3,1,'view','view',1,'view') " +
                ", (4,1,'update','update',1,'update')";
        String spAppSQL = "INSERT INTO SP_APP (ID, TENANT_ID, APP_NAME, USER_STORE, USERNAME, AUTH_TYPE, UUID) " +
                "VALUES (1, 1, 'TEST_APP_NAME','TEST_USER_STORE', 'TEST_USERNAME', 'TEST_AUTH_TYPE', 'test-app-id')";

        try (Connection connection = getConnection()) {
            connection.createStatement().executeUpdate(domainDataSQL);
            connection.createStatement().executeUpdate(aPIResourceSQL);
            connection.createStatement().executeUpdate(scopeSQL);
            connection.createStatement().executeUpdate(spAppSQL);
        } catch (SQLException e) {
            String errorMessage = "Error while Adding test data for tables";
            throw new Exception(errorMessage, e);
        }
    }

    private Connection getConnection() throws Exception {
        if (dataSourceMap.get(RoleDAOTest.DB_NAME) != null) {
            return dataSourceMap.get(RoleDAOTest.DB_NAME).getConnection();
        }
        throw new RuntimeException("Invalid datasource.");
    }

    private void clearDataSource() throws Exception {

        BasicDataSource dataSource = dataSourceMap.get(DB_NAME);
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeUpdate("DROP ALL OBJECTS;");
        }
    }
}
