package commoble.cram.api;

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
	/**
	 * Priority of registration. Plugins with lower priority numbers run first,
	 * e.g. a plugin with priority -1 runs before priority 0, which runs before priority 1.
	 * Plugins with the same priority are run in alphabetical order by fully-qualified class name.
	 * @return priority ordinal
	 */
	int priority() default 0;
}
