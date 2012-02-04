package org.menacheri.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines constants which decide the communication protocol while sending
 * messages. RELIABLE guaranty will use a protocol like TCP which can reliably
 * send messages between server and client. FAST guaranty will use a network
 * protocol like UDP, which will send messages quite fast but not reliably.
 * 
 * @author Abraham Menacherry
 * 
 */
public class DeliveryGuaranty implements IDeliveryGuaranty
{
	private static final Logger LOG = LoggerFactory
			.getLogger(DeliveryGuaranty.class);
	public static final int RELIABLE = 0;// TCP, reliable but slow
	public static final int FAST = 1;// UDP, fast but unreliable
	// Higher level implementations which can use both tcp and upd will have
	// this value.
	public static final int MIXED_MODE = 2;
	/**
	 * This value holds the delivery guaranty.
	 */
	private final int guaranty;
	
	public DeliveryGuaranty(int guaranty)
	{
		if (guaranty < 0)
		{
			this.guaranty = 0;
			LOG.warn("Negative guranty {} passed into "
					+ "constructor, will set as 0", guaranty);
		}
		else
		{
			this.guaranty = guaranty;
		}
	}
	
	@Override
	public int getGuaranty()
	{
		return guaranty;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + guaranty;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeliveryGuaranty other = (DeliveryGuaranty) obj;
		if (guaranty != other.guaranty)
			return false;
		return true;
	}
}
