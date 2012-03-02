package org.menacheri.communication;


/**
 * The delivery guaranty for the underlying network transport protocol.
 * Implementations should be immutable.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IDeliveryGuaranty
{
	public enum DeliveryGuaranty implements IDeliveryGuaranty
	{
		RELIABLE(0),FAST(1);
		final int guaranty;
		
		DeliveryGuaranty(int guaranty)
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
