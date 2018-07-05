package com.twinsickle.aem.utils.email;

import java.util.List;

public interface MessageService {

    void send(List<String> recipients, String subject, String body);
    void send(Message message);
}
