import com.google.inject.*;

import java.util.*;

import controllers.routes;
import org.junit.runners.MethodSorters;
import play.*;
import play.inject.guice.*;
import play.mvc.*;

import play.test.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.*;

import javax.inject.Inject;

// Use FixMethodOrder to run the tests sequentially
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FunctionalTest {

    @Inject
    Application application;

    @Before
    public void setup() {
        Module testModule = new AbstractModule() {
            @Override
            public void configure() {
                // Install custom test binding here
            }
        };

        GuiceApplicationBuilder builder = new GuiceApplicationLoader()
                .builder(new ApplicationLoader.Context(Environment.simple()))
                .overrides(testModule);
        Guice.createInjector(builder.applicationModule()).injectMembers(this);

        Helpers.start(application);
    }

    @After
    public void teardown() {
        Helpers.stop(application);
    }

    @Test
    public void homePage() {
        Result result = Helpers.route(application, controllers.routes.UserController.loginPage());

        assertThat(result.status(), equalTo(Helpers.OK));
    }
    @Test
    public void createUser() {
        Result result = Helpers.route(application, controllers.routes.UserController.create());

        assertThat(result.status(), equalTo(Helpers.OK));
    }
    @Test
    public void forgotPost() {
        Result result = Helpers.route(application, controllers.routes.UserController.forgotPost());

        assertThat(result.status(), equalTo(Helpers.OK));
    }
}
