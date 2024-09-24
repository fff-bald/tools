package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * 邮件工具类
 */
public class EmailUtil {

    /**
     * 邮件发送类型枚举
     */
    public enum EmailSendType {
        QQ {
            @Override
            public Properties getProperties() {
                Properties properties = new Properties();
                properties.put("mail.smtp.host", "smtp.qq.com");
                properties.put("mail.smtp.port", "587");
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mailFrom", "your-email@qq.com");
                properties.put("password", "your-email-password");
                return properties;
            }
        },
        GMAIL {
            @Override
            public Properties getProperties() {
                Properties properties = new Properties();
                properties.put("mail.smtp.host", "smtp.gmail.com");
                properties.put("mail.smtp.port", "587");
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mailFrom", "your-email@gmail.com");
                properties.put("password", "your-email-password");
                return properties;
            }
        },
        ONE_SIX_THREE {
            @Override
            public Properties getProperties() {
                Properties properties = new Properties();
                properties.put("mail.smtp.host", "smtp.163.com");
                properties.put("mail.smtp.port", "25");
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mailFrom", ConfigUtil.getProperties().getProperty("email.163.address", ""));
                properties.put("password", ConfigUtil.getProperties().getProperty("email.163.password", ""));
                return properties;
            }
        };

        public abstract Properties getProperties();
    }

    /**
     * 发送电子邮件
     *
     * @param type        邮件发送类型
     * @param toAddress   收件人的电子邮件地址
     * @param subject     邮件主题
     * @param message     邮件内容
     * @param attachFiles 附件文件路径数组（可选）
     * @throws MessagingException 如果发送邮件失败
     */
    public static void sendEmail(EmailSendType type, String toAddress, String subject, String message, String... attachFiles) throws MessagingException {
        Properties properties = type.getProperties();

        String userName = properties.getProperty("mailFrom");
        String password = properties.getProperty("password");

        // 创建一个新的会话，使用认证信息
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(properties, auth);

        // 创建一个新的电子邮件消息
        Message msg = new MimeMessage(session);

        // 设置发件人地址
        msg.setFrom(new InternetAddress(userName));
        // 设置收件人地址
        InternetAddress[] toAddresses = {new InternetAddress(toAddress)};
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        // 设置邮件主题
        msg.setSubject(subject);
        // 设置发送日期
        msg.setSentDate(new java.util.Date());

        // 创建消息部分
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html");

        // 创建多部分
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // 添加附件
        if (attachFiles != null) {
            for (String filePath : attachFiles) {
                MimeBodyPart attachPart = new MimeBodyPart();
                try {
                    attachPart.attachFile(filePath);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                multipart.addBodyPart(attachPart);
            }
        }

        // 设置多部分对象到消息对象
        msg.setContent(multipart);

        // 发送邮件
        Transport.send(msg);
    }

    public static void main(String[] args) {
        // 收件人信息
        String mailTo = ConfigUtil.getProperties().getProperty("email.common.receiver.address");
        String subject = "FundConstant";
        String message = "This is a test email with attachments.";

        // 附件文件路径
        String[] attachFiles = {".\\src\\main\\java\\process\\fund\\constant\\FundConstant.java"};

        try {
            sendEmail(EmailSendType.ONE_SIX_THREE, mailTo, subject, message, attachFiles);
            System.out.println("Email sent successfully.");
        } catch (MessagingException ex) {
            System.out.println("Could not send email.");
            ex.printStackTrace();
        }
    }
}
