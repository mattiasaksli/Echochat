import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class NotificationSender implements Runnable {

    private String email;
    private String username;
    private List<Chatroom> chatrooms;
    private volatile boolean exit = false;

    void setEmail(String email) {
        this.email = email;
    }

    void setChatrooms(List<Chatroom> chatrooms) {
        this.chatrooms = chatrooms;
    }

    void setUsername(String username) {
        this.username = username;
    }

    void stop() {
        exit = true;
    }

    @Override
    public void run() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Map<Chatroom, List<Message>> chatroomMessages = new HashMap<>();

        for (Chatroom chatroom : chatrooms) {
            chatroomMessages.put(chatroom, chatroom.getUserAndMessages().get(username));
        }

        while (!exit) {

            try {
                long seconds = 30;
                long minutes = 0;
                long hours = 0;
                long days = 0;
                long time = 1000 * seconds + 1000 * 60 * minutes + 1000 * 60 * 60 * hours + 1000 * 60 * 60 * 24 * days;
                Thread.sleep(time);

                StringBuilder emailMessage = new StringBuilder();
                for (Map.Entry<Chatroom, List<Message>> entry : chatroomMessages.entrySet()) {
                    if (entry.getValue().isEmpty()) {
                        continue;
                    }

                    String chatroomName = entry.getKey().getName();
                    emailMessage.append("<h3><b>").append(chatroomName).append("</b></h3>");
                    emailMessage.append("########################################################################")
                            .append("########################################################################<br>");

                    List<Message> newMessages = entry.getValue();
                    for (Message message : newMessages) {
                        long timestamp = message.getTimestamp();
                        Date resultDate = new Date(timestamp);
                        String author = message.getAuthor();
                        String messageContent = message.getMessage();
                        String newMessage = "[" + sdf.format(resultDate) + "] " + author + " >>> " + messageContent + "<br>";
                        emailMessage.append(newMessage);
                    }

                    emailMessage.append("########################################################################")
                            .append("########################################################################<br><br>");
                    entry.getValue().clear();
                }

                if (emailMessage.length() != 0) {
                    emailMessage.insert(0, "<h3>Conversations in the chatrooms you are associated with:</h3>");

                    try {
                        sendEmail(emailMessage.toString());
                    } catch (Exception e) {
                        System.out.println("Failed to send email to user " + username + "! Reason: " + e.getMessage());
                    }
                }

            } catch (InterruptedException e) {
                System.out.println("Notification thread for user " + username + " stopped working!");
                throw new RuntimeException();
            }
        }

        System.out.println("notification thread closed successfully for user " + username);
    }

    // From https://www.journaldev.com/2532/javamail-example-send-mail-in-java-smtp
    private void sendEmail(String body) throws Exception {
        final String fromEmail;
        final String password;

        try (InputStream is = NotificationSender.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();

            if (is != null) {
                prop.load(is);
            } else {
                throw new FileNotFoundException("config.properites not found!");
            }

            fromEmail = prop.getProperty("fromEmail");
            password = prop.getProperty("password");
        }

        System.out.println("sending email to " + username + "...");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        createMailStuff(session, body);
    }

    private void createMailStuff(Session session, String body) throws Exception {

        MimeMessage msg = new MimeMessage(session);

        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");

        msg.setFrom(new InternetAddress("echo.notifications@gmail.com", "NoReply-EchoBot"));

        msg.setReplyTo(InternetAddress.parse("echo.notifications@gmail.com", false));

        msg.setSubject("Echoboys Chat Notification", "UTF-8");

        msg.setText(body, "UTF-8");

        msg.setContent(body, "text/html");

        msg.setSentDate(new Date());

        msg.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(email, false));
        Transport.send(msg);

        System.out.println("email sent to " + username);

    }
}
