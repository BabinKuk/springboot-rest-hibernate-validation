package org.babinkuk.common;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@Retention(RUNTIME)
@Target({TYPE, METHOD})
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public @interface ProducesJson {

}
