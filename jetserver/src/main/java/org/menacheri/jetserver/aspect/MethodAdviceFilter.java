package org.menacheri.jetserver.aspect;

/**
 * This enumeration describes some filtering capabilities while applying the
 * {@link AppManaged} aspect to classes. User can then for example use the
 * {@link AppManaged} aspect only for persistence by selecting NO_METHODS filter
 * while applying the aspect to a class. The NO_METHODS will turn off converting
 * method calls to jetlang call altogether.
 * 
 * @author Abraham Menacherry
 * 
 */
public enum MethodAdviceFilter
{
	/**
	 * Advice all non private methods. Hence, methods like these below will get
	 * advised.
	 * 
	 * <pre>
	 * 	protected void method1(){
	 *  }
	 *  or
	 *  public int method2(){
	 *  }
	 * </pre>
	 */
	ALL_METHODS,
	/**
	 * No methods of the class will get advised if set to this value.
	 */
	NO_METHODS,
	/**
	 * Advice all non private methods which return a void. Hence methods like
	 * the following will get advised. This is also the default used in
	 * {@link AppManaged} aspect.
	 * 
	 * <pre>
	 * 	protected void method1(){
	 *  }
	 *  or
	 *  public void method2(){
	 *  }
	 * </pre>
	 * 
	 * But methods like these below will not get advised.
	 * 
	 * <pre>
	 * 	private void method1(){
	 *  }
	 *  or
	 *  public int method2(){
	 *  }
	 * </pre>
	 */
	ALL_VOID_METHODS;

}
