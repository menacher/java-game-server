package io.nadron.util;

public class BinaryUtils
{

	private static final String HEXES = "0123456789ABCDEF";

	public static String getHexString(byte[] raw)
	{
		return getHexString(raw, null);
	}

	public static String getHexString(byte[] raw, String separator)
	{
		boolean sep = (null != separator) && !("".equals(separator));

		if (raw == null)
		{
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw)
		{
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
			if (sep)
			{
				hex.append(separator);
			}
		}
		return hex.toString();
	}
}
