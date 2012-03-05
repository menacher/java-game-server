package org.menacheri.jetclient.communication;

/**
 * The delivery guaranty for the underlying network transport protocol.
 * Implementations should be immutable.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IDeliveryGuaranty
{
	/**
	 * An enumeration which implements {@link IDeliveryGuaranty}. Used to
	 * abstract out the TCP and UDP transports.
	 * 
	 * @author Abraham Menacherry
	 * 
	 */
	public enum DeliveryGuaranty implements IDeliveryGuaranty
	{
		RELIABLE(1), FAST(2);
		final int guaranty;

		DeliveryGuaranty(int guaranty)
		{
			this.guaranty = guaranty;
		}

		public int getGuaranty()
		{
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
