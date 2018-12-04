package models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import play.libs.Json;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class BackendCalls {
    private static final String BASE_URL = "http://localhost:9001";

    public static User getUser(String email) throws UnirestException {
        HttpResponse<String> resp = Unirest.get(BASE_URL + "/users/" + email)
                .asString();
        if(resp.getStatus() == 400){
            throw new UnirestException(new Exception());
        }
        return Json.fromJson(Json.parse(resp.getBody()), User.class);
    }

    public static User saveUser(User user) throws UnirestException {
        HttpResponse<String> resp = Unirest.post(BASE_URL + "/users")
                .header("content-type", "application/json")
                .body(Json.toJson(user).toString())
                .asString();
        if(resp.getStatus() == 400){
            throw new UnirestException(new Exception());
        }
        return Json.fromJson(Json.parse(resp.getBody()), User.class);
    }

    public static void updatePassword(User user) throws UnirestException {
        HttpResponse<String> resp = Unirest.post(BASE_URL + "/users/reset/" + user.email)
                .header("content-type", "application/json")
                .body(Json.toJson(user).toString())
                .asString();
        if(resp.getStatus() == 400){
            throw new UnirestException(new Exception());
        }
    }

    public static void updateUser(String email,UpdateUser user) throws UnirestException {
        HttpResponse<String> resp = Unirest.post(BASE_URL + "/users/edit/" + email)
                .header("content-type", "application/json")
                .body(Json.toJson(user).toString())
                .asString();
        if(resp.getStatus() == 400){
            throw new UnirestException(new Exception());
        }
    }
    public static List<Submission> fetchSubmissions(User user) throws UnirestException, IOException {
        HttpResponse<String> resp =Unirest.get(BASE_URL + "/users/"+user.email+"/submission/all")
                .header("content-type", "application/json")
                .asString();
        if(resp.getStatus() == 400){
            throw new UnirestException(new Exception());
        }
        ObjectMapper mapper = new ObjectMapper();
        return Arrays.asList(mapper.readValue(resp.getBody(),Submission[].class));
    }
    public static Submission fetchSubmission(User user,Long id) throws UnirestException {
        HttpResponse<String> resp =Unirest.get(BASE_URL + "/users/"+user.email+"/submission/"+id)
                .header("content-type", "application/json")
                .asString();
        if(resp.getStatus() == 400){
            throw new UnirestException(new Exception());
        }
        return Json.fromJson(Json.parse(resp.getBody()),Submission.class);
    }
    public static void saveSubmission(User user,Submission submission) throws UnirestException {
        HttpResponse<String> resp = Unirest.post(BASE_URL + "/users/"+user.email+"/submission/new")
                .header("content-type", "application/json")
                .body(Json.toJson(submission).toString())
                .asString();
        if(resp.getStatus() == 400){
            throw new UnirestException(new Exception());
        }
    }
    public static void updateSubmission(User user,Submission submission,Long id) throws UnirestException {
        HttpResponse<String> resp = Unirest.post(BASE_URL + "/users/"+user.email+"/submission/"+id)
                .header("content-type", "application/json")
                .body(Json.toJson(submission).toString())
                .asString();
        if(resp.getStatus() == 400){
            throw new UnirestException(new Exception());
        }
    }
    public static void approveSubmission(String email,Long id) throws UnirestException {
        System.out.println("approving submission"+id+email);
        HttpResponse<String> resp = Unirest.post(BASE_URL + "/users/"+email+"/submission/approve/"+id)
                .asString();
        if(resp.getStatus() == 400){
            throw new UnirestException(new Exception());
        }
    }
    public static void rejectSubmission(String email,Long id) throws UnirestException {
        System.out.println("approving submission"+id+email);
        HttpResponse<String> resp = Unirest.post(BASE_URL + "/users/"+email+"/submission/reject/"+id)
                .asString();
        if(resp.getStatus() == 400){
            throw new UnirestException(new Exception());
        }
    }
}
