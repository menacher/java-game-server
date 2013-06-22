package io.nadron.client.communication;

/**
 * The delivery guaranty for the underlying network transport protocol.
 * Implementations should be immutable.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface DeliveryGuaranty
{
	/**
	 * An enumeration which implements {@link DeliveryGuaranty}. Used to
	 * abstract out the TCP and UDP transports.
	 * 
	 * @author Abraham Menacherry
	 * 
	 */
	public enum DeliveryGuarantyOptions implements DeliveryGuaranty
	{
		RELIABLE(1), FAST(2);
		final int guaranty;

		DeliveryGuarantyOptions(int guaranty)
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
