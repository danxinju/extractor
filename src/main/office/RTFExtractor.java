package dxj.extractor.main.office;

import java.util.HashMap;
import java.util.Map;

import dxj.extractor.main.util.ExtractorUtil;
import dxj.extractor.main.util.FileUtil;

public class RTFExtractor
{
	
	private static Map<String, String> include = new HashMap<String, String>();
	static
	{
		include.put("par", 	  	  "\n");
		include.put("bullet",     "\u2022");
		include.put("emdash",     "\u2014");
		include.put("emspace",    "\u2003");
		include.put("endash",     "\u2013");
		include.put("enspace",    "\u2002");
		include.put("ldblquote",  "\u201C");
		include.put("lquote",     "\u2018");
		include.put("ltrmark",    "\u200E");
		include.put("rdblquote",  "\u201D");
		include.put("rquote",     "\u2019");
		include.put("rtlmark",    "\u200F");
		include.put("tab",        "\u0009");
		include.put("zwj",        "\u200D");
		include.put("zwnj",       "\u200C");
	}
	
	public static String extract(String file, int[] err, int read)
	{
		String content = file;
		if(read == 0)
		{
			try {
				content = FileUtil.readAscFile(file);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Total length: "+ content.length());
		
		if(!ExtractorUtil.isRTFFormat(content.getBytes()))
		{
			System.out.println("Not the rtf file!");
			err[0] = ExtractorUtil.NOT_RTF_FORMAT;
			return "";
		}
		
		int line1 = content.indexOf("}}");
		if(line1 > -1)
		{
			int line2 = content.indexOf("}}", line1+2);
			if(line2 > -1)
			{
				content = content.substring(line2+2);
			}
			else
			{
				content = content.substring(line1+2);
			}
		}

		content = content.replaceAll("\\{\\\\\\*[^\\}]*?\\}", "");
		content = content.replaceAll("\n|\r", "");
		
		StringBuilder builder = new StringBuilder();
		String[] buffers = content.split("\\\\");
		for(int i=0;i<buffers.length;i++)
		{
			String buffer = buffers[i].trim();
			String value = include.get(buffer);
			if(value != null)
			{
				builder.append(value);
				continue;
			}
			
			if(buffer.startsWith("'"))
			{
				if(i == buffers.length - 1)
				{
					break;
				}
				
				String a = buffer.replaceAll("\\}\\{", "");
				String b = buffers[i+1].replaceAll("\\}\\{", "");
				if(!b.startsWith("'"))
				{
					continue;
				}
				
				if(a.length() < 3 || b.length() < 3)
				{
					continue;
				}
				
				if(a.length() > 3)
				{
					builder.append( a.substring(3) );
					a = a.substring(0, 3);
				}
				
				if(b.length() > 3)
				{
					builder.append( b.substring(3) );
					b = b.substring(0, 3);
				}
				
				a = a.replace("'", "");
				b = b.replace("'", "");
				if(a.length() != 2 || a.replaceAll("[A-Fa-f0-9]", "").length() != 0)
				{
					continue;
				}
				
				if(b.length() != 2 || b.replaceAll("[A-Fa-f0-9]", "").length() != 0)
				{
					continue;
				}
				
				int ch = Integer.valueOf(b+a, 16);
				byte[] temp = new byte[2];
				temp[0] = (byte) ch;
				temp[1] = (byte) (ch >> 8);
				builder.append( new String(temp) );

				i++;
				continue;
			}
			
			int spaceOffset = buffer.indexOf(" ");
			if(spaceOffset > -1)
			{
				String rest = buffer.substring(spaceOffset+1);
				if(buffer.startsWith("f0 "))
				{
					builder.append( rest.replaceAll("\\}\\{", "") );
					continue;
				}
				
				if("{".equals(rest) || "}".equals(rest) || rest.indexOf("}{") > -1 || rest.indexOf("}}") > -1)
				{
					continue;
				}
				
				if(rest.startsWith("{"))
				{
					builder.append( rest.substring(1) );
					continue;
				}
				
				if(rest.endsWith("}"))
				{
					builder.append( rest.substring(0, rest.length()-1) );
					continue;
				}
				
				builder.append( rest );
				continue;
			}
			
		}
		
		err[0] = ExtractorUtil.EXTRACTING_DONE;
		return builder.toString();
	}
	
	public static void main(String[] args) throws Exception
	{
		String text = RTFExtractor.extract("E:\\cn.rtf", new int[1], 0);
		FileUtil.writeAscFile("E:\\output.txt", text, false);
		
		System.out.println("Done!");
	}
}

