package io.nadron.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SmallFileReader
{
	public static String readSmallFile(String filePath)
			throws IOException
	{
		if(filePath == null)
		{
			return null;
		}
		File smallFile = new File(filePath);
		return readSmallFile(smallFile);
	}
	
	public static String readSmallFile(File smallFile)
			throws IOException
	{
		FileReader reader = new FileReader(smallFile);
		BufferedReader bufferedReader = new BufferedReader(reader);
		StringBuffer buf = new StringBuffer();
		String line = null;
		while ((line = bufferedReader.readLine()) != null)
		{
			buf.append(line);
		}
		bufferedReader.close();
		reader.close();
		return buf.toString();
	}
}
