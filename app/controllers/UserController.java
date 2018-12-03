package controllers;
import com.mashape.unirest.http.exceptions.UnirestException;
import models.BackendCalls;
import models.Forgot;
import models.UpdateUser;
import models.User;
import org.mindrot.jbcrypt.BCrypt;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

public class UserController extends Controller {
    private FormFactory formFactory;
    GlobalObjects globalObjects = new GlobalObjects();
    BackendCalls calls = new BackendCalls();

    @Inject
    public UserController(FormFactory formFactory) {
        this.formFactory = formFactory;

    }

    public Result loginPage() {
        return ok(views.html.login.render(formFactory.form(User.class)));
    }

    public Result login() {
        Form<User> userForm = formFactory.form(User.class).bindFromRequest();
        if (userForm.hasErrors()) {
            return ok(views.html.login.render(formFactory.form(User.class)));
        }
        try {
            User usr = calls.getUser(userForm.get().email);
            System.out.println(usr.password);
            if (BCrypt.checkpw(userForm.get().password,usr.password)) {
                session("id", String.valueOf(usr.email));
                return Results.redirect(
                        routes.UserController.edit()
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        flash("failure", "Error logging in check email and password!");
        return ok(views.html.login.render(formFactory.form(User.class)));
    }

    public Result logout() {
        session().remove("id");
        return Results.redirect(
                routes.UserController.loginPage()
        );
    }

    public Result edit() {
        String email = session("id");
        if (email == null) {
            return Results.redirect(
                    routes.UserController.loginPage()
            );
        }
        try {
            UpdateUser user = new UpdateUser(calls.getUser(email));
            Form<UpdateUser> userForm = formFactory.form(UpdateUser.class).fill(
                    user
            );
            return ok(
                    views.html.editUser.render(userForm, user.authority)
            );
        } catch (UnirestException e) {
            return Results.redirect(
                    routes.UserController.loginPage()
            );
        }
    }

    public Result save() {
        Form<User> userForm = formFactory.form(User.class).bindFromRequest();
        if (userForm.hasErrors()) {
            return badRequest(views.html.createUser.render(userForm));
        }
        userForm.get().password = BCrypt.hashpw(userForm.get().password, BCrypt.gensalt());
        try {
            User usr = calls.saveUser(userForm.get());
            flash("success", "User " + usr.firstName + " has been created");
            session("id", String.valueOf(usr.email));
            return Results.redirect(
                    routes.UserController.edit()
            );
        } catch (UnirestException e) {
            flash("failure", "Error creating user!");
            return ok(
                    views.html.createUser.render(formFactory.form(User.class))
            );
        }
    }

    public Result create() {
        Form<User> userForm = formFactory.form(User.class);
        return ok(
                views.html.createUser.render(userForm)
        );
    }

    public Result forgotPost() {
        Form<UpdateUser> userForm = formFactory.form(UpdateUser.class).bindFromRequest();
        try {
            User user = calls.getUser(userForm.get().email);
            Mail mail = new Mail();
            String passcode = mail.send(user.email);
            globalObjects.getForgotPasscodes().put(passcode, user);
            return ok(
                    views.html.sentMail.render()
            );
        } catch (UnirestException e) {
            flash("failure", "No such user found");
            return Results.redirect(
                    routes.UserController.loginPage()
            );
        }
    }

    public Result reset(String id) {
        Form<User> userForm = formFactory.form(User.class);
        return ok(
                views.html.reset.render(userForm, id)
        );
    }

    public Result resetPassword() {
        Form<Forgot> userForm = formFactory.form(Forgot.class).bindFromRequest();
        if (globalObjects.getForgotPasscodes().get(userForm.get().passcode)==null) {
            flash("failure", "Unable to use link generate a new link");
            return Results.redirect(
                    routes.UserController.loginPage()
            );
        }
        User user = globalObjects.getForgotPasscodes().get(userForm.get().passcode);
        globalObjects.getForgotPasscodes().remove(userForm.get().passcode);
        user.password = BCrypt.hashpw(userForm.get().password, BCrypt.gensalt());
        try {
            calls.updatePassword(user);
            session("id", user.email);
            flash("success", "User " + user.firstName + " password has been updated");
            return Results.redirect(
                    routes.UserController.edit()
            );
        } catch (UnirestException e) {
            flash("failure", "Unable to use link generate a new link");
            return Results.redirect(
                    routes.UserController.loginPage()
            );
        }
    }

    public Result forgot() {
        Form<User> userForm = formFactory.form(User.class);
        return ok(
                views.html.forgot.render(userForm)
        );
    }

    /**
     * Handle the 'edit form' submission
     */
    public Result update() throws PersistenceException {
        String email = session("id");
        Form<UpdateUser> userForm = formFactory.form(UpdateUser.class).bindFromRequest();
        User savedUser;
        try {
            savedUser = calls.getUser(email);
        } catch (UnirestException e) {
            return Results.redirect(
                    routes.UserController.loginPage()
            );
        }
        if (userForm.hasErrors()) {
            return badRequest(views.html.editUser.render(userForm, savedUser.authority));
        }

        try {
            calls.updateUser(savedUser.email,userForm.get());
            flash("success", "User " + userForm.get().firstName + " has been updated");
            return ok(
                    views.html.editUser.render(userForm, savedUser.authority)
            );
        } catch (UnirestException e) {
            return Results.redirect(
                    routes.UserController.loginPage()
            );
        }
    }
}
