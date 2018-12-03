package com.salvatorefiorilla.systemmonitor;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


//Class is extending AsyncTask because this class is going to perform a networking operation
public class SendMail extends AsyncTask<Void,Void,Void> {

    //Declaring Variables
    private Context context;
    private Session session;

    //Information to send email
    private String email;
    private String subject;
    private String message;
    private String filename;

    public SendMail(Context context, String email, String subject, String message,String filename){
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.filename = filename;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Toast.makeText(context,"Report Sent",Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        System.out.println("Creating properties..");
        Properties props = new Properties();
        System.out.println("Configuring properties for gmail..");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        System.out.println("Creating a new session..");
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Config.getEMAIL(), Config.getPASSWORD());
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(Config.getEMAIL()));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject(this.subject);
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(this.message);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart = new MimeBodyPart();
            //String filename = "/home/manisha/file.txt";
            DataSource source = new FileDataSource(filename);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            System.out.println("Sending email... ");
            Transport.send(message);
        } catch (MessagingException e) {
            System.out.println("Errore invio email: "+e.getMessage());
            e.printStackTrace();
        }   return null;
    }
}