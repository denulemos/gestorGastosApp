package com.example.gestorgastos;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.widget.Toast;



import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//ES NECESARIO INSTALAR LA LIBRERIA JAVAMAIL PARA QUE ESTO FUNCIONE.

public class SendMail extends AsyncTask{
    private Context context;
    private Session session;
    private String email;
    private String subject;
    private String message;
    private ProgressDialog progressDialog;
    public SendMail(Context context, String email, String subject, String message){
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context,"Enviando Email","Espere un momento ...",false,false);
    }



    //COMPLETAR CON DATOS, RECOMENDABLE HACERLO EN UN ARCHIVO .ENV POR SEGURIDAD

    protected Object doInBackground(Object[] objects) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("EMAIL REMITENTE", "CONTRASEÑA REMITENTE");
            }
        });
        try {
            MimeMessage mm = new MimeMessage(session);
            mm.setFrom(new InternetAddress("EMAIL REMITENTE"));
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            mm.setSubject(subject);
            mm.setText(message);
            Transport.send(mm);
            progressDialog.dismiss();
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}