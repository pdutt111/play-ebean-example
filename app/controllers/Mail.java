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
        email.setAuthenticator(new DefaultAuthenticator("account@gmail.com", "password"));
        email.setSSLOnConnect(true);
        try{
            email.setFrom("jp2013213323@gmail.com");
            email.setSubject("erification");
            email.setMsg("Your verify code is : "+devStr);
            email.addTo(mailAddress);
            email.send();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("error!!!!");
        }
        return devStr;
    }

}
