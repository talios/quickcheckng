package com.theoryinpractise.quickcheckng;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation a class named *Generators to generate a class containing TestNG @DataProviders
 * for each 'public static' Generator method.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface DataProviders {
}
