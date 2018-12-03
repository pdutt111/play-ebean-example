package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.mashape.unirest.http.exceptions.UnirestException;
import models.BackendCalls;
import models.Submission;
import models.UpdateUser;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SubmissionsController extends Controller {
    private FormFactory formFactory;
    GlobalObjects globalObjects = new GlobalObjects();
    BackendCalls calls = new BackendCalls();

    @Inject
    public SubmissionsController(FormFactory formFactory) {
        this.formFactory = formFactory;

    }

    public Result createSubmission() {
        try {
            UpdateUser user = new UpdateUser(calls.getUser(session("id")));
            Form<Submission> userForm = formFactory.form(Submission.class);
            return ok(
                    views.html.createSubmission.render(userForm, user.authority)
            );
        } catch (UnirestException e) {
            return Results.redirect(
                    routes.UserController.loginPage()
            );
        }
    }

    public Result fetchSubmissions() {
        try {
            User user = calls.getUser(session("id"));
            List<Submission> submissions = calls.fetchSubmissions(user);
            return ok(
                    views.html.submissions.render(submissions, user.authority)
            );
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Results.redirect(
                routes.UserController.loginPage()
        );
    }

    public Result fetchSubmission(Long id) {
        try {
            User user = calls.getUser(session("id"));
            Submission submission = calls.fetchSubmission(user, id);
            Form<Submission> submissionForm = formFactory.form(Submission.class)
                    .fill(submission);
            return ok(
                    views.html.changeSubmission.render(submissionForm, user.authority, id)
            );
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return Results.redirect(
                routes.UserController.loginPage()
        );
    }

    public Result save() {
        Form<Submission> submissionForm = formFactory.form(Submission.class).bindFromRequest();
        User user = null;
        try {
            user = calls.getUser(session("id"));
        } catch (UnirestException e) {
            return Results.redirect(
                    routes.UserController.loginPage()
            );
        }
        try {
            if (submissionForm.hasErrors()) {
                flash("failure", "Problems creating submission");
                return badRequest(views.html.createSubmission.render(submissionForm, user.authority));
            }
            submissionForm.get().status = "0";
            submissionForm.get().email = user.email;
            calls.saveSubmission(user, submissionForm.get());
            flash("success", "Submission has been created");
            return Results.redirect(
                    routes.SubmissionsController.fetchSubmissions()
            );
        } catch (UnirestException e) {
            e.printStackTrace();
            flash("failure", "Error creating Submission!");
            return ok(
                    views.html.changeSubmission.render(submissionForm, user.authority, submissionForm.get().id)
            );
        }
    }

    public Result update(Long submissionId) throws PersistenceException {
        User user = null;
        try {
            user = calls.getUser(session("id"));
        } catch (UnirestException e) {
            return Results.redirect(
                    routes.UserController.loginPage()
            );
        }
        Form<Submission> submissionForm = formFactory.form(Submission.class).bindFromRequest();
        if (submissionForm.hasErrors()) {
            return badRequest(views.html.createSubmission.render(submissionForm, user.authority));
        }
        try {
            calls.updateSubmission(user, submissionForm.get(), submissionId);
            return Results.redirect(
                    routes.SubmissionsController.fetchSubmission(submissionId)
            );
        } catch (UnirestException e) {
            e.printStackTrace();
            flash("failure", "Error updating Submission!");
            return ok(
                    views.html.changeSubmission.render(submissionForm, user.authority, submissionForm.get().id)
            );
        }
    }

    public Result approve(Long submissionId) throws PersistenceException {
        User user = null;
        try {
            user = calls.getUser(session("id"));
        } catch (UnirestException e) {
            return Results.redirect(
                    routes.UserController.loginPage()
            );
        }
        Form<Submission> submissionForm = formFactory.form(Submission.class).bindFromRequest();
        if (submissionForm.hasErrors()) {
            return badRequest(views.html.createSubmission.render(submissionForm, user.authority));
        }
        try {
            calls.approveSubmission(user.email,submissionId);
            flash("success", "Submission has been approved");
            return Results.redirect(
                    routes.SubmissionsController.fetchSubmission(submissionId)
            );
        } catch (UnirestException e) {
            e.printStackTrace();
            flash("failure", "Error updating Submission!");
            return ok(
                    views.html.changeSubmission.render(submissionForm, user.authority, submissionForm.get().id)
            );
        }
    }
}
