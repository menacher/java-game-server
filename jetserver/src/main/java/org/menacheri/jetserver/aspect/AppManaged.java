package org.menacheri.jetserver.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is an easy way to make a class <a
 * href="http://gpars.codehaus.org/">Gpars</a> enabled. This aspect helps the <a
 * href="www.eclipse.org/aspectj/">aspectj</a> to weave in behavior like
 * concurrency and persistence to the annotated classes. Using the different
 * configuration options (Enums), the user can turn on or off such behavior for
 * each class.
 * 
 * @author Abraham Menacherry
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AppManaged {
	
	/**
	 * Use ConcurrencyType.NONE if required to turn off.
	 * 
	 * @return The type of concurrency for the adviced object.
	 */
	ConcurrencyType concurrencyType() default ConcurrencyType.AGENT;

	/**
	 * Defines which filter will be applied to classes that have the concurrency
	 * feature above turned on to AGENT.
	 * 
	 * @return The advice can be meant for different types of methods. Default
	 *         is methods with return type void.
	 */
	MethodAdviceFilter methodFilterType() default MethodAdviceFilter.ALL_VOID_METHODS;

	/**
	 * A true means the objects would be serialized and persisted on disk
	 * transparently. TODO this feature is yet to be implemented.
	 * 
	 * @return Default is true, meaning that the app managed objects will be
	 *         persisted.
	 */
	boolean persistance() default true;
}
