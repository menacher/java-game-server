package io.nadron.communication;


/**
 * The delivery guaranty for the underlying network transport protocol.
 * Implementations should be immutable.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface DeliveryGuaranty
{
	public enum DeliveryGuarantyOptions implements DeliveryGuaranty
	{
		RELIABLE(0),FAST(1);
		final int guaranty;
		
		DeliveryGuarantyOptions(int guaranty)
		{
			this.guaranty = guaranty;
		}
		
		public int getGuaranty(){
			return guaranty;
		}
	}
	/**
	 * Return the associated integer guaranty constant.
	 * 
	 * @return returns the integer guaranty.
	 */
	public int getGuaranty();
}
