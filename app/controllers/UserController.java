package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import models.Computer;
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
    @Inject
    public UserController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result GO_HOME = Results.redirect(
//        routes.HomeController.list(0, "name", "asc", "")
            routes.HomeController.list(0,"name","asc","")
    );

    public Result edit() {
        Form<User> userForm = formFactory.form(User.class).fill(
                User.find.byId(Long.parseLong(session("id")))
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

        userForm.get().save();
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
    /**
     * Handle the 'edit form' submission
     *
     */
    public Result update() throws PersistenceException {
        Long id = Long.parseLong(session("id"));
        Form<User> userForm = formFactory.form(User.class).bindFromRequest();
        if(userForm.hasErrors()) {
            return badRequest(views.html.editUser.render(id, userForm));
        }

        Transaction txn = Ebean.beginTransaction();
        try {
            User savedUser = User.find.byId(id);
            if (savedUser != null) {
                User newUserData = userForm.get();
                savedUser.address = newUserData.address;
                savedUser.affiliation= newUserData.affiliation;
                savedUser.firstName= newUserData.firstName;
                savedUser.lastName= newUserData.lastName;
                savedUser.password= newUserData.password;
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

        return GO_HOME;
    }
}
