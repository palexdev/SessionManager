package io.github.palexdev.sessionmanager.utils.gson;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation used by {@link ExcludeAnnotationStrategy} to exclude
 * fields from serialization only.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Exclude {
}
