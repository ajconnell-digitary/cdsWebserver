package org.pesc.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pesc.DirectoryApplication;
import org.pesc.api.model.DirectoryUser;
import org.pesc.api.model.Role;
//import org.pesc.config.H2DatabaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptException;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by prabhu on 11/23/16.
 */

//@ContextConfiguration(classes = H2DatabaseConfig.class)
//@SpringBootTest(classes = {DirectoryApplication.class})

@ActiveProfiles({"h2"})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {



    @Autowired
    JdbcTemplate  jdbc;

    //@Before
    public void before() throws ScriptException,SQLException {
       /* ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/dropTables.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.1/1-organization.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.1/3-contact.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.1/5-delivery_methods.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.1/4-document_format.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.1/6-endpoint.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.2/1-add-document-formats.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.2/2-default-organizations.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.3/1-user.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.3/2-default-users.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.3/3-school-codes.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.3/4-institutions-service-providers.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.3/5-messages.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.3/6-uploads.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.4/oauth-secret-column.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.4/default-oauth-token.sql"));
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/db.changelog-0.0.4/oauth-tokens.sql"));*/

        //ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/1-user.sql"));
        //ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/3-contact.sql"));
    }
    @After
    public void after() throws ScriptException,SQLException {
        ScriptUtils.executeSqlScript(jdbc.getDataSource().getConnection(), new ClassPathResource("db/scripts/dropTables.sql"));
        System.out.println("Its Done!!!");
    }

    @Autowired
    UserService userService;

    @Test
    public void createUser() {

        Role role = new Role();
        role.setId(1);
        Set<Role> roleSet = new HashSet<Role>();
        roleSet.add(role);

        String userName = "junit";
        DirectoryUser directoryUser = new DirectoryUser();
        //directoryUser.setId(10);
        directoryUser.setName("JnuitTest");
        directoryUser.setAddress("Jnuit Address");
        directoryUser.setCreatedTime(new Date());
        directoryUser.setModifiedTime(new Date());
        directoryUser.setEmail("junit@mail.com");
        directoryUser.setEnabled(true);
        directoryUser.setOrganizationId(1);
        directoryUser.setPassword("admin");
        directoryUser.setPhone("9161234567");
        directoryUser.setUsername(userName);
        directoryUser.setRoles(roleSet);

        int countRowsInTable =  JdbcTestUtils.countRowsInTable(jdbc,"users");
        System.err.println("OrgCount " + countRowsInTable);
        Assert.assertNotEquals(0, countRowsInTable);
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbc,"users","username = '" + userName + "'"));
        userService.create(directoryUser);
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbc,"users","username = '" + userName + "'"));


    }

    @Test
    public void deleteUser() {

        Role role = new Role();
        role.setId(1);
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);

        String userName = "junit";
        DirectoryUser directoryUser = new DirectoryUser();
        directoryUser.setId(4);
        /*directoryUser.setName("JnuitTest");
        directoryUser.setAddress("Jnuit Address");
        directoryUser.setCreatedTime(new Date());
        directoryUser.setModifiedTime(new Date());
        directoryUser.setEmail("junit@mail.com");
        directoryUser.setEnabled(true);
        directoryUser.setOrganizationId(1);
        directoryUser.setPassword("admin");
        directoryUser.setPhone("9161234567");
        directoryUser.setUsername(userName);*/

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbc,"users","username = '" + userName + "'"));
        userService.delete(directoryUser);
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbc,"users","username = '" + userName + "'"));
    }

        public JdbcTemplate getJdbc() {
            return jdbc;
        }

        public void setJdbc(JdbcTemplate jdbc) {
            this.jdbc = jdbc;
        }

        public UserService getUserService() {
            return userService;
        }

        public void setUserService(UserService userService) {
            this.userService = userService;
        }
}