package dxj.extractor.main;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dxj.extractor.main.util.ExtractorUtil;
import dxj.extractor.main.util.FileUtil;
import dxj.extractor.main.util.TextFilter;

public class PdfExtractor {
	
	private static byte[] nomalizeAsciiBytes(byte[] ogiBytes)
	{
		byte[] retBytes = new byte[ogiBytes.length];
		for(int i=0;i<ogiBytes.length;i++)
		{
			byte b = ogiBytes[i];
			int ch = b & 0xFF;
			
			if((ch >= 32 && ch < 127) || ch == 10 || ch == 13)
			{
				retBytes[i] = b;
			}
			else
			{
				retBytes[i] = (byte) 'x';
			}
		}
		
		return retBytes;
	}
	
	private static List<TextFilter> getTextFilters(String ascii)
	{
		List<TextFilter> filters = new ArrayList<TextFilter>();
		
		Matcher matcher = Pattern.compile("<</(.*)>>").matcher(ascii);
		while(matcher.find())
		{
			String item = matcher.group();
			if(item.indexOf("/Length") == -1)
			{
				continue;
			}
			
			if
			(
					item.indexOf("/Filter/FlateDecode") == -1 		&&
					item.indexOf("/Filter/Fl") == -1 				&&
					item.indexOf("/Filter/LZWDecode") == -1 		&&
					item.indexOf("/Filter/ASCII85Decode") == -1 	&&
					item.indexOf("/Filter/A85") == -1 				&&
					item.indexOf("/Filter/ASCIIHexDecode") == -1 	&&
					item.indexOf("/Filter/AHx") == -1
					
			)
			{
				continue;
			}
			
			if
			(
				item.indexOf("/N ") > -1 		|| 
				item.indexOf("/V ") > -1 		||
				item.indexOf("/F ") > -1 		||
				item.indexOf("/H ") > -1 		||
				item.indexOf("/Metadata ") > -1 ||
				item.indexOf("/Type /XObject") > -1
			)
			{
				continue;
			}
			
			TextFilter filter = new TextFilter();
			String[] arr = item.split("/");
			for(String a : arr)
			{
				if(a.indexOf("Length ") > -1)
				{
					String[] b = a.split(" ");
					if(b.length > 1 && b[1] != null && !"".equals(b[1]))
					{
						filter.length = Integer.parseInt(b[1].replace(">>", ""));
					}
				}
			}
			
			if(filter.length == 0)
			{
				continue;
			}
			
			filter.raw = item;
			filter.start = matcher.start() + item.length();
			int nextLine = ascii.codePointAt(filter.start);
			if(nextLine == 10 || nextLine == 13)
			{
				filter.start +=2;
			}
			filter.start += 6;
			filter.start += 2;
			
			filters.add(filter);
		}
		
		return filters;
	}
	
	public static String extract(byte[] ogiBytes, int[] err) throws Exception
	{
		System.out.println("Total bytes: "+ ogiBytes.length);
		if(!ExtractorUtil.isPdfFormat(ogiBytes))
		{
			System.out.println("Not the pdf file!");
			err[0] = ExtractorUtil.NOT_PDF_FORMAT;
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		String ascii = new String( nomalizeAsciiBytes(ogiBytes) );
		
		List<TextFilter> filters = getTextFilters(ascii);

		for(TextFilter filter : filters)
		{
			System.out.println(filter.start +","+ filter.length);
			byte[] bytes = new byte[filter.length];
			System.arraycopy(ogiBytes, filter.start, bytes, 0, filter.length);
			
			switch(filter.getCodeMethod())
			{
			case TextFilter.CODE_FlATE:
				bytes = ExtractorUtil.decodeFlate(bytes);
				break;
			case TextFilter.CODE_LZW:
				bytes = ExtractorUtil.decodeLZW(bytes);
				break;
			case TextFilter.CODE_ASC85:
				bytes = ExtractorUtil.decodeASCII85(bytes);
				break;
			case TextFilter.CODE_ASCHEX:
				bytes = ExtractorUtil.decodeASCIIHex(bytes);
				break;
			default:
				break;
			}
			
			if(bytes == null || bytes.length == 0)
			{
				continue;
			}
			
			String section = new String(bytes);
			builder.append(section);
			/**
			Matcher matcher = Pattern.compile("\\([^\\)]+\\)").matcher(section);
			while(matcher.find())
			{
				builder.append( matcher.group().replaceAll("\\(|\\)", "") );
			}*/
			
		}
		
		err[0] = ExtractorUtil.EXTRACTING_DONE;
		return builder.toString();
	}
	
	public static String extract(String filePath, int[] err)
	{
		byte[] ogiBytes = null;
		try 
		{
			ogiBytes = FileUtil.readBinFile(filePath);
			return extract(ogiBytes, err);
		} 
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return "";
	}
	
	public static void main(String[] args) throws Exception
	{
		String text = PdfExtractor.extract("D:\\tools\\010Editor\\PDF+Reference+1.6+���İ�.pdf", new int[1]);
		
		FileUtil.writeAscFile("E:\\output.txt", text, false);
		
		System.out.println("Done!");
		
	}
}
