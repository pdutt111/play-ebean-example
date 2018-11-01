package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import models.Computer;
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
import java.util.Arrays;
import java.util.HashMap;

public class UserController extends Controller {
    private FormFactory formFactory;
    GlobalObjects globalObjects = new GlobalObjects();
    @Inject
    public UserController(FormFactory formFactory) {
        this.formFactory = formFactory;

    }

    public Result GO_HOME = Results.redirect(
//        routes.HomeController.list(0, "name", "asc", "")
            routes.HomeController.list(0,"name","asc","")
    );
    public Result loginPage(){
        return ok(views.html.login.render(formFactory.form(User.class)));
    }
    public Result login() {
        Form<User> userForm = formFactory.form(User.class).bindFromRequest();
        if(userForm.hasErrors()) {
            return ok(views.html.login.render(formFactory.form(User.class)));
        }
        User usr = User.find.where().eq("email",userForm.get().email).findUnique();
        if(BCrypt.checkpw(userForm.get().password, usr.password)){
            session("id",String.valueOf(usr.id));
            return Results.redirect(
                    routes.UserController.edit()
            );
        }else{
            flash("failure", "Error logging in check email and password!");
            return ok(views.html.login.render(formFactory.form(User.class)));
        }
    }
    public Result logout(){
        session().remove("id");
        return Results.redirect(
                routes.UserController.loginPage()
        );
    }
    public Result edit() {
        UpdateUser user = new UpdateUser(User.find.byId(Long.parseLong(session("id"))));
        Form<UpdateUser> userForm = formFactory.form(UpdateUser.class).fill(
                user
        );
        return ok(
                views.html.editUser.render(Long.parseLong(session("id")), userForm)
        );
    }
    public Result save() {
        Form<User> userForm = formFactory.form(User.class).bindFromRequest();
        if(userForm.hasErrors()) {
            return badRequest(views.html.createUser.render(userForm));
        }
        userForm.get().password = BCrypt.hashpw(userForm.get().password, BCrypt.gensalt());
        try{
            userForm.get().save();
        }catch(PersistenceException e){
            flash("failure", "Error creating user!");
            return ok(
                    views.html.createUser.render(formFactory.form(User.class))
            );
        }
        flash("success", "Computer " + userForm.get().firstName + " has been created");
        session("id",String.valueOf(userForm.get().id));
        return Results.redirect(
//        routes.HomeController.list(0, "name", "asc", "")
                routes.UserController.edit()
        );
    }
    public Result create() {
        Form<User> userForm = formFactory.form(User.class);
        return ok(
                views.html.createUser.render(userForm)
        );
    }
    public Result forgotPost() {
        Form<UpdateUser> userForm = formFactory.form(UpdateUser.class).bindFromRequest();
        User user= User.find.where().eq("email",userForm.get().email).findUnique();
        if(user!=null){
            Mail mail = new Mail();
            String passode = mail.send(user.email);
            globalObjects.getForgotPasscodes().put(passode,user);
            System.out.println(Arrays.toString(globalObjects.getForgotPasscodes().keySet().toArray()));
            return ok(
                    views.html.sentMail.render()
            );
        }
        return Results.redirect(
                routes.UserController.loginPage()
        );
    }
    public Result reset(String id) {
        Form<User> userForm = formFactory.form(User.class);
        return ok(
                views.html.reset.render(userForm,id)
        );
    }
    public Result resetPassword() {
        Form<Forgot> userForm = formFactory.form(Forgot.class).bindFromRequest();
        if(globalObjects.getForgotPasscodes().get(userForm.get().passcode) == null){
            flash("failure", "Unable to use link generate a new link");
            return Results.redirect(
                    routes.UserController.loginPage()
            );
        }
        User user = globalObjects.getForgotPasscodes().get(userForm.get().passcode);
        globalObjects.getForgotPasscodes().remove(userForm.get().passcode);
        user.password = BCrypt.hashpw(userForm.get().password, BCrypt.gensalt());
        user.update();
        session("id",String.valueOf(user.id));
        flash("success", "User " + user.firstName + " password has been updated");
        return Results.redirect(
                routes.UserController.edit()
        );
    }
    public Result forgot() {
        Form<User> userForm = formFactory.form(User.class);
        return ok(
                views.html.forgot.render(userForm)
        );
    }
    /**
     * Handle the 'edit form' submission
     *
     */
    public Result update() throws PersistenceException {
        Long id = Long.parseLong(session("id"));
        Form<UpdateUser> userForm = formFactory.form(UpdateUser.class).bindFromRequest();
        System.out.println(userForm.errorsAsJson().toString());
        if(userForm.hasErrors()) {
            return badRequest(views.html.editUser.render(id, userForm));
        }

        Transaction txn = Ebean.beginTransaction();
        try {
            User savedUser = User.find.byId(id);
            if (savedUser != null) {
                UpdateUser newUserData = userForm.get();
                savedUser.address = newUserData.address;
                savedUser.affiliation= newUserData.affiliation;
                savedUser.firstName= newUserData.firstName;
                savedUser.lastName= newUserData.lastName;
                savedUser.city= newUserData.city;
                savedUser.country= newUserData.country;
                savedUser.fax= newUserData.fax;
                savedUser.phone= newUserData.phone;
                savedUser.position= newUserData.position;
                savedUser.researchAreas= newUserData.researchAreas;
                savedUser.zip= newUserData.zip;
                savedUser.update();
                flash("success", "User " + userForm.get().firstName + " has been updated");
                txn.commit();
            }
        } finally {
            txn.end();
        }

        return ok(
                views.html.editUser.render(Long.parseLong(session("id")), userForm)
        );
    }
}
