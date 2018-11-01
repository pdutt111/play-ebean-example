package controllers;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletionStage;
import org.apache.commons.mail.*;

public class Mail {
    public String send(String mailAddress) {
        String devStr = (int)(Math.random()*100000000)+"";
        System.out.println(devStr);
        Email email = new SimpleEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(465);
        email.setSSLOnConnect(true);
        email.setAuthenticator(new DefaultAuthenticator("jp2013213323@gmail.com", "ly5364983"));
        email.setSSLOnConnect(true);
        try{
            System.out.println("enter into mail 1......");
            email.setFrom("jp2013213323@gmail.com");
            System.out.println("enter into mail 2");
            email.setSubject("TestMail");
            System.out.println("enter into mail 3");
            email.setMsg("Your verify code is : "+devStr);
            System.out.println("enter into mail 4...");
            email.addTo(mailAddress);
            System.out.println("enter into mail 5...");
            email.send();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("error!!!!");
        }
        return devStr;
    }

}
