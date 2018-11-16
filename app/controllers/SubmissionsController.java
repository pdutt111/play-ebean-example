package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
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
import java.util.ArrayList;
import java.util.List;

public class SubmissionsController  extends Controller {
    private FormFactory formFactory;
    GlobalObjects globalObjects = new GlobalObjects();
    @Inject
    public SubmissionsController(FormFactory formFactory) {
        this.formFactory = formFactory;

    }
    public Result createSubmission() {
        UpdateUser user = new UpdateUser(User.find.byId(Long.parseLong(session("id"))));
        Form<Submission> userForm = formFactory.form(Submission.class);
        return ok(
                views.html.createSubmission.render(Long.parseLong(session("id")), userForm,user.authority)
        );
    }
    public Result fetchSubmissions() {
        User user =User.find.byId(Long.parseLong(session("id")));
        List<Submission> submissions = Submission.find.where().eq("email",user.email).findList();
        if(user.authority.equals("admin")){
            submissions = Submission.find.all();
        }
        return ok(
                views.html.submissions.render(Long.parseLong(session("id")), submissions,user.authority)
        );
    }
    public Result fetchSubmission(Long id) {
        User user = User.find.byId(Long.parseLong(session("id")));
        Submission submission = Submission.find.where().eq("id",id).findUnique();
        if(!user.authority.equals("admin")){
            submission = Submission.find.where().eq("email",user.email).eq("id",id).findUnique();
        }
        Form<Submission> submissionForm = formFactory.form(Submission.class)
                .fill(submission);
        return ok(
                views.html.changeSubmission.render(Long.parseLong(session("id")), submissionForm,user.authority,id)
        );
    }
    public Result save() {
        User user = User.find.byId(Long.parseLong(session("id")));
        Form<Submission> submissionForm = formFactory.form(Submission.class).bindFromRequest();
        if(submissionForm.hasErrors()) {
            flash("failure", "Problems creating submission");
            return badRequest(views.html.createSubmission.render(Long.parseLong(session("id")), submissionForm,user.authority));
        }
        try{
            submissionForm.get().status = "0";
            submissionForm.get().email = user.email;
            submissionForm.get().save();
        }catch(PersistenceException e){
            e.printStackTrace();
            flash("failure", "Error creating user!");
            return ok(
                    views.html.changeSubmission.render(Long.parseLong(session("id")), submissionForm,user.authority,submissionForm.get().id)
            );
        }
        flash("success", "Submission has been created");
        return Results.redirect(
                routes.SubmissionsController.fetchSubmissions()
        );
    }
    public Result update(Long submissionId) throws PersistenceException {
        User user = User.find.byId(Long.parseLong(session("id")));
        Form<Submission> submissionForm = formFactory.form(Submission.class).bindFromRequest();
        Submission savedSubmission = Submission.find.byId(submissionId);
        if(submissionForm.hasErrors()) {
            return badRequest(views.html.createSubmission.render(Long.parseLong(session("id")), submissionForm,user.authority));
        }

        Transaction txn = Ebean.beginTransaction();
        try {
            if (savedSubmission != null) {
                savedSubmission = submissionForm.get();
                savedSubmission.email = user.email;
                savedSubmission.id = submissionId;
                savedSubmission.status = "0";
                savedSubmission.update();
                flash("success", "Submission has been updated");
                txn.commit();
            }
        } finally {
            txn.end();
        }

        return Results.redirect(
                routes.SubmissionsController.fetchSubmission(submissionId)
        );
    }
    public Result approve(Long submissionId) throws PersistenceException {
        User user = User.find.byId(Long.parseLong(session("id")));
        System.out.println(user.authority);
        if(user.authority.equals("admin")){
            System.out.println("changing submission status");
            Transaction txn = Ebean.beginTransaction();
            System.out.println(submissionId);
            Submission submission = Submission.find.byId(submissionId);
            submission.status = "1";
            submission.update();
            txn.commit();
        }
        flash("success", "Submission has been approved");
        return Results.redirect(
                routes.SubmissionsController.fetchSubmission(submissionId)
        );
    }
}
