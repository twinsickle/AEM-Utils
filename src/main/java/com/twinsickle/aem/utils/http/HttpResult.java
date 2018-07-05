package com.twinsickle.aem.utils.http;

import java.util.Optional;

public interface HttpResult<T> {

    boolean success();
    Optional<T> getEntity();
}
