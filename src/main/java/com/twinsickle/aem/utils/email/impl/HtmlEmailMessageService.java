package com.twinsickle.aem.utils.email.impl;

import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.twinsickle.aem.utils.email.Message;
import com.twinsickle.aem.utils.email.MessageService;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component(service = MessageService.class)
public class HtmlEmailMessageService implements MessageService {
    private static final Logger LOG = LoggerFactory.getLogger(HtmlEmailMessageService.class);

    @Reference
    private MessageGatewayService messageGatewayService;

    @Override
    public void send(List<String> recipients, String subject, String body) {
        try {
            HtmlEmail email = new HtmlEmail();

            email.setTo(convertRecipients(recipients));
            email.setSubject(subject);
            email.setMsg(body);

            MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
            messageGateway.send(email);

        } catch (EmailException ee){
            LOG.error("HtmlEmailMessageService#send - Failed to send email", ee);
        }
    }

    @Override
    public void send(Message message) {
        send(message.getRecipients(), message.getSubject(), message.getMessage());
    }

    private Collection<InternetAddress> convertRecipients(List<String> recipients){
        return recipients.stream()
                .map(this::convertToInternetAddress)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private Optional<InternetAddress> convertToInternetAddress(String recipient){
        try {
            return Optional.of(new InternetAddress(recipient));
        } catch (AddressException ae){
            LOG.warn("HtmlEmailMessageService - {} is an invalid recipient", recipient);
        }
        return Optional.empty();
    }

}
