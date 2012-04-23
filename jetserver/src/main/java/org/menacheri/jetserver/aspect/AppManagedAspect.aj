package org.menacheri.jetserver.aspect;

import org.menacheri.jetserver.concurrent.Agent;
import org.menacheri.jetserver.concurrent.DataFlowVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This aspect is responsible for weaving in behavior to {@link AppManaged}
 * annotated objects. Based on the pointcut matching and
 * {@link MethodAdviceFilter} based filter criteria, it will convert normal
 * method calls to <a href="http://code.google.com/p/jetlang/">jetlang</a> calls by
 * wrapping them around a {@link Runnable} object. Using this aspect
 * users can transparently weave in concurrent behavior into their java
 * applications.
 * 
 * @author Abraham Menacherry
 * 
 */
@SuppressWarnings({"unchecked","serial"})
public aspect AppManagedAspect
{
	private static final Logger LOG = LoggerFactory.getLogger(AppManagedAspect.class);
	
	/**
	 * A marker interface on to which methods and variables are added as below.
	 * @author Abraham Menacherry
	 *
	 */
	public interface AgentWrapper{};
	
	/**
	 * Declare (introduce) a private Jetlang {@link Agent} variable on the interface
	 * @return
	 */
	private Agent AgentWrapper.agent;
	
	/**
	 * @return returns the associated {@link Agent} instance or null.
	 */
	public Agent AgentWrapper.getAgent()
	{
		return agent;
	}

	/**
	 * @param newAgent sets the {@link Agent} instance on the {@link AgentWrapper}.
	 */
	public void AgentWrapper.setAgent(Agent newAgent)
	{
		agent = newAgent;
	}
	
	/**
	 * Introduce the above interface implementation to @AppManaged annotated classes.
	 */
	declare parents : @AppManaged * implements AgentWrapper;
	
	/**
	 * For debugging purposes only.
	 * @param s
	 */
	static final void println(String s){ System.out.println(s); }
	
	// POINT CUTS are declared below
	/**
	 * Point cut that matches any private method
	 */
	pointcut anyPrivate() : execution(private * *.*(..));
	
	/**
	 * Point cut that matches all methods which return a void and are not private.
	 */
	pointcut allNonPrivateVoid() : execution(void *.*(..)) && !anyPrivate();
	
	// Point cut that matches all methods that do not return a void and are not private.
	pointcut allNonVoidNonPrivate() : execution(* *.*(..)) && !execution(void *.*(..)) && !anyPrivate();
	
	// Point cut that matches the first public,protected or default method
	// execution on an AppManaged annotated class from a non-annotated class.
	pointcut firstVoidCall(AppManaged annotation) :
		@within(AppManaged) && !cflowbelow(@within(AppManaged)) && 
		allNonPrivateVoid() && @this(annotation) && 
		// Method filter allows to to turn off or on the advice on a case by case basis.
		// Do not advice in case filter is set to "NO_METHODS".
		if ((annotation.methodFilterType() == MethodAdviceFilter.ALL_VOID_METHODS) || 
				(annotation.methodFilterType() == MethodAdviceFilter.ALL_METHODS));
	
	// Point cut that matches the first public,protected or default non void method
	// execution on an AppManaged annotated class.
	pointcut firstCall(AppManaged annotation) :
		@within(AppManaged) && !cflowbelow(@within(AppManaged)) && allNonVoidNonPrivate() && 
		@this(annotation) &&
		// Method filter allows to to turn off or on the advice on a case by case basis.
		// Do not advice in case filter is set to "NO_METHODS" or ALL_VOID_METHODS
		if (annotation.methodFilterType() == MethodAdviceFilter.ALL_METHODS);
	
	// Point cut that matches the method execution of any @AppManaged annotated object.
	pointcut appManagedVoidExecution(Object appManaged) : execution(void (@AppManaged *).*(..)) && this(appManaged);
	
	// Point cut that matches all non void method execution of any @AppManaged annotated object.
	pointcut appManagedExecution(Object appManaged) : execution(* (@AppManaged *).*(..)) && this(appManaged);
	
	// Point cut that matches any void call to any @AppManaged annotated object.
	pointcut appManagedVoidCall(Object called, AppManaged annotation) : 
		call(void (@AppManaged *).*(..)) && target(called) && @target(annotation) &&
		if ((annotation.methodFilterType() == MethodAdviceFilter.ALL_VOID_METHODS) || 
				(annotation.methodFilterType() == MethodAdviceFilter.ALL_METHODS));
	
	// Point cut that matches all non void calls to any @AppManaged annotated object.
	pointcut appManagedCall(Object called, AppManaged annotation) : 
		call(* (@AppManaged *).*(..)) && target(called) && @target(annotation) &&
		if (annotation.methodFilterType() == MethodAdviceFilter.ALL_METHODS);
	
	// Point cut that matches method calls from one @AppManaged annotated object to another. i.e. routed calles.
	pointcut routedVoid(Object appManaged, Object called, AppManaged annotation) : 
		appManagedVoidCall(called, annotation) && cflowbelow(appManagedVoidExecution(appManaged)) && if(called != appManaged);

	// Point cut that matches method calls from one @AppManaged annotated object to another. i.e. routed calles.
	pointcut routed(Object appManaged, Object called, AppManaged annotation) : 
		!execution(void *.*(..)) && !call(void *.*(..)) && appManagedCall(called,annotation) && cflowbelow(appManagedExecution(appManaged)) && if(called != appManaged);
	
	// Point cut to match the creation of any app managed object.
	// Using this point cut we will inject an Agent object.
	pointcut appManagedObjectCreation(Object newInstance, AppManaged annotation): 
		initialization ((@AppManaged *).new(..)) && this(newInstance) && @this(annotation);
	
	
	// Advises are declared below

	/**
	 * After advice that will inject a <a href="http://code.google.com/p/jetlang">jetlang</a>
	 * agent.
	 * 
	 * @param newInstance
	 * @param annotation
	 */
	after(Object newInstance,AppManaged annotation) returning: 
		appManagedObjectCreation(newInstance, annotation){
		if(annotation.concurrencyType() == ConcurrencyType.AGENT){
			// Create a new agent instance for the corresponding appManaged
			// object instance.
			Agent agentForAppManaged = new Agent(); 
			// The agent instance is now set on the wrapper.
			((AgentWrapper)newInstance).setAgent(agentForAppManaged);
		}
	}
	
	/**
	 * This advice will convert a normal void method call (for e.g. public void
	 * someMethod(){}) to a Jetlang agent call
	 * 
	 * @param appManaged
	 *            The object instance from which the call is being made, or the
	 *            "this"
	 * @param called
	 *            The object instance which represents the target of the method
	 *            call i.e, the actual instance on which the method is called.
	 * @param annotation
	 *            The annotation instance for this class. Used to check the
	 *            MethodAdviceFilter enum's value.
	 * @param wrapper
	 *            Each AppManaged object has an AgentWrapper injected post
	 *            initialization. This parameter is an instance of the wrapper.
	 */
	void around(final Object appManaged, final Object called, final AppManaged annotation, final AgentWrapper wrapper) : 
		routedVoid (appManaged, called, annotation) && this(wrapper){
//		LOG.trace("Intercepted message: {}",
//				thisJoinPointStaticPart.getSignature().getName());
//		LOG.trace("in class: {}",
//				thisJoinPointStaticPart.getSignature().getDeclaringType().getName());
		Agent agentOfAppManaged = wrapper.getAgent();
		// If agent is null, then this app managed object is not really an agent.
		if(null != agentOfAppManaged){
		// proceed the call inside
			agentOfAppManaged.send(new Runnable() {
			@Override
			public void run()
			{
				proceed(appManaged,called,annotation,wrapper);
			}
		});
		}else{
			proceed(appManaged,called,annotation,wrapper);
		}
	}
	
	/**
	 * This advice will convert a normal void method call (for e.g. protected
	 * void someMethod(){}) to a GPars agent call
	 * 
	 * @param wrapper
	 *            Each AppManaged object has an AgentWrapper injected post
	 *            initialization. This parameter is an instance of the wrapper.
	 * @param annotation
	 *            The annotation instance for this class. Used to check the
	 *            MethodAdviceFilter enum's value.
	 */
	void around(final AgentWrapper wrapper, final AppManaged annotation) : 
		firstVoidCall(annotation) && this(wrapper){
//		LOG.trace("Intercepted message: {}",
//				thisJoinPointStaticPart.getSignature().getName());
//		LOG.trace("in class: {}",
//				thisJoinPointStaticPart.getSignature().getDeclaringType().getName());
		Agent agentOfAppManaged = wrapper.getAgent();
		// If agent is null, then this app managed object is not really an agent.
		if(null != agentOfAppManaged){
		// proceed the call inside
			agentOfAppManaged.send(new Runnable() {
			@Override
			public void run()
			{
				proceed(wrapper,annotation);
			}
		});
		}else{
			proceed(wrapper,annotation);
		}
	}
	
	/**
	 * This advice will convert a normal non void method call( for e.g. public
	 * int someMethod(){}) to a GPars agent call. The implementation is actually
	 * a blocking call which will wait till the original method that was invoked
	 * returns a value.
	 * 
	 * @param appManaged
	 *            The object instance from which the call is being made, or the
	 *            "this"
	 * @param called
	 *            The object instance which represents the target of the method
	 *            call i.e, the actual instance on which the method is called.
	 * @param annotation
	 *            The annotation instance for this class. Used to check the
	 *            MethodAdviceFilter enum's value.
	 * @param wrapper
	 *            Each AppManaged object has an AgentWrapper injected post
	 *            initialization. This parameter is an instance of the wrapper.
	 * @return Returns the result of the actual object call. It does this either
	 *         by using a DataFlowVariable and blocking on the call or by
	 *         directly proceeding with the call outside of GPars.
	 */
	Object around(final Object appManaged, final Object called,  final AppManaged annotation, final AgentWrapper wrapper) : 
		routed(appManaged, called, annotation) && this(wrapper){
		Agent agentOfAppManaged = wrapper.getAgent();
//		LOG.trace("Intercepted message: {}",
//				thisJoinPointStaticPart.getSignature().getName());
//		LOG.trace("in class: {}",
//				thisJoinPointStaticPart.getSignature().getDeclaringType().getName());
		// If agent is null, then this app managed object is not really an agent.
		if(null != agentOfAppManaged){
		final DataFlowVariable var = new DataFlowVariable();
		// proceed the call inside
		agentOfAppManaged.send(new Runnable() {
			@Override
			public void run()
			{
				var.bind(proceed(appManaged,called,annotation,wrapper));
			}
		});
		try
		{
			return var.getVal();
		}
		catch (InterruptedException e)
		{
			LOG.error("AppManagedAspect thread interrupted",e);
		}
		return null;
		}else{
			return proceed(appManaged,called,annotation,wrapper);
		}
	}
	
	// TODO the return type for int is returning 0 instead of correct value why?
	/**
	 * This advice will convert a normal non void method call( for e.g. public
	 * int someMethod(){}) to a GPars agent call. The implementation is actually
	 * a blocking call which will wait till the original method that was invoked
	 * returns a value.
	 * 
	 * @param Each
	 *            AppManaged object has an AgentWrapper injected post
	 *            initialization. This parameter is an instance of the wrapper.
	 * @param annotation
	 *            The annotation instance for this class. Used to check the
	 *            MethodAdviceFilter enum's value.
	 * @return Returns the result of the actual object call. It does this either
	 *         by using a DataFlowVariable and blocking on the call or by
	 *         directly proceeding with the call outside of GPars.
	 */
	Object around(final AgentWrapper wrapper,final AppManaged annotation) : 
		firstCall(annotation) && this(wrapper){
		Agent agentOfAppManaged = wrapper.getAgent();
//		LOG.trace("Intercepted message: {}",
//				thisJoinPointStaticPart.getSignature().getName());
//		LOG.trace("in class: {}",
//				thisJoinPointStaticPart.getSignature().getDeclaringType().getName());
		// If agent is null, then this app managed object is not really an agent.
		if(null != agentOfAppManaged){
			final DataFlowVariable var = new DataFlowVariable();
			// proceed the call inside messaging runnable.
			agentOfAppManaged.send(new Runnable() {
				@Override
				public void run()
				{
					var.bind(proceed(wrapper,annotation));
				}
			});
			try
			{
				return var.getVal();
			}
			catch (InterruptedException e)
			{
				LOG.error("AppManagedAspect thread interrupted",e);
			}
			return null;
		}else{
			return proceed(wrapper,annotation);
		}
	}
	
}
