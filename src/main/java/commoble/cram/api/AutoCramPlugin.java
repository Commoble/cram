package com.github.commoble.cram.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating a class with this annotation will cause an instance of that
 * class to be instantiated during cram registration.
 * 
 * Classes annotated with this annotation must either have no explicit constructors,
 * or have one constructor with no arguments.
 * 
 * Classes annotated with this annotation must also implement CramPlugin.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoCramPlugin
{

}
