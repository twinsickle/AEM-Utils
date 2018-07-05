package com.twinsickle.aem.utils.email;

import java.util.Optional;

public interface MessageGenerator {
    Optional<Message> createMessage();
}
