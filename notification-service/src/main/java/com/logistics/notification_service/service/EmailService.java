package com.logistics.notification_service.service;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String fromEmail;


    /**
     * Envoie un email simple (texte brut)
     */
    public boolean sendSimpleEmail(String to, String subject, String text) {
        try {
            log.info("Sending simple email to: {}", to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Simple email sent successfully");
            return true;
        } catch (Exception e) {
            log.error("Error sending simple email", e);
            return false;
        }
    }



    /**
     * Envoie un email HTML avec template Thymeleaf
     */
    public boolean sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> templateData) {
        try {
            log.info("Sending template email to: {} using template: {}", to, templateName);

            // Créer le contexte Thymeleaf
            Context context = new Context();
            context.setVariables(templateData);

            // Générer le HTML depuis le template
            String htmlContent = templateEngine.process(templateName, context);

            // Créer le message MIME
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Template email sent successfully");
            return true;
        } catch (Exception e) {
            log.error("Error sending template email", e);
            return false;
        }
    }

    /**
     * Envoie un email HTML personnalisé
     */
    public boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            log.info("Sending HTML email to: {}", to);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("HTML email sent successfully");
            return true;
        } catch (Exception e) {
            log.error("Error sending HTML email", e);
            return false;
        }
    }
}