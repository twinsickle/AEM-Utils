package com.twinsickle.aem.utils.email;

import java.util.List;

public interface Message {
    List<String> getRecipients();
    String getSubject();
    String getMessage();
}
