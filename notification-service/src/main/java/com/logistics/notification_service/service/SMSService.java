package com.logistics.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SMSService {

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.phone-number:}")
    private String fromPhoneNumber;

    /**
     * Envoie un SMS
     * Note: Pour utiliser Twilio, ajoutez la dépendance twilio-java dans pom.xml
     * et décommentez le code ci-dessous
     */
    public boolean sendSMS(String phoneNumber, String message) {
        try {
            log.info("Sending SMS to: {}", phoneNumber);

            // Vérifier la configuration
            if (accountSid == null || accountSid.isEmpty() || accountSid.startsWith("your-")) {
                log.warn("Twilio not configured. SMS sending skipped (mock mode)");
                log.info("Mock SMS sent to {} with message: {}", phoneNumber, message);
                return true; // Mode mock pour le développement
            }

            // Code Twilio (décommenter après avoir ajouté la dépendance)
            /*
            Twilio.init(accountSid, authToken);
            Message twilioMessage = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(fromPhoneNumber),
                    message
            ).create();

            log.info("SMS sent successfully. SID: {}", twilioMessage.getSid());
            return true;
            */

            // Pour l'instant, mode mock
            log.info("Mock SMS sent successfully");
            return true;

        } catch (Exception e) {
            log.error("Error sending SMS", e);
            return false;
        }
    }
}