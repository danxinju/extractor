package dxj.extractor.main.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtil {
	
	public static byte[] readBinFile(String path) throws Exception
	{
		FileInputStream stream = new FileInputStream(path);
		
		int len = stream.available();
		byte[] buffer = new byte[len];
		stream.read(buffer);
		stream.close();
		
		return buffer;
	}
	
	public static String readAscFile(String path) throws Exception
	{
		InputStreamReader reader = new InputStreamReader(new FileInputStream(path), "UTF-8");
		StringBuilder sb = new StringBuilder();

		int ch = 0;
		while((ch = reader.read()) != -1)
		{
			sb.append( (char)ch );
		}
		reader.close();
		
		return sb.toString();
	}
	
	public synchronized static void writeBinFile(String path, byte[] buffer) throws Exception
	{
		FileOutputStream output = new FileOutputStream(path, true);		
		output.write(buffer);
		output.flush();
		output.close();
	}
	
	public synchronized static void writeAscFile(String path, String content) throws Exception
	{
		writeAscFile(path, content, true);
	}
	
	public synchronized static void writeAscFile(String path, String content, boolean append) throws Exception
	{
		FileOutputStream output = new FileOutputStream(path, append);
		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		
		writer.append(content);
		writer.flush();
		writer.close();
	}
}
