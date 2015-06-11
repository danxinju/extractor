package dxj.extractor.main.office;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import dxj.extractor.main.util.ExtractorUtil;
import dxj.extractor.main.util.FileUtil;
import dxj.extractor.main.util.GBKChars;

public class OfficeExtractor {
	
	public final static String WORD_TYPE = "doc";
	public final static String DOT_TYPE = "dot";
	public final static String PPT_TYPE = "ppt";
	public final static String EXCEL_TYPE = "xls";
	
	public static Map<String, Float> wordWeight = new HashMap<String, Float>();
	static {
		wordWeight.put(WORD_TYPE, 0.35F);
		wordWeight.put(DOT_TYPE, 0.35F);
		wordWeight.put(PPT_TYPE, 0.50F);
		wordWeight.put(EXCEL_TYPE, 0.50F);
	}
	
	private static String getFileType(String filePath)
	{
		if(filePath == null || "".equals(filePath))
		{
			return "";
		}
		
		String[] arr = filePath.split("\\.");
		
		return arr[arr.length-1].toLowerCase();
	}
	
	public static String extract(byte[] ogiBytes, int[] err, String fileType)
	{
		System.out.println("Total bytes: "+ ogiBytes.length);
		
		StringBuilder content = new StringBuilder();
		int index = 0;
		float weight = wordWeight.get(fileType);
		while(index < ogiBytes.length)
		{
			StringBuilder ch = new StringBuilder();
			StringBuilder en = new StringBuilder();
			
			for(int i=0;i<ExtractorUtil.SECTOR_SIZE;i++)
			{
				if(i < ExtractorUtil.SECTOR_SIZE-1)
				{
					String a = Integer.toHexString(ogiBytes[index+i+1] & 0xFF);
					String b = Integer.toHexString(ogiBytes[index+i]   & 0xFF);
					if(a.length() == 1)
					{
						a = "0"+ a;
					}
					
					if(b.length() == 1)
					{
						b = "0"+ b;
					}
					
					int val = Integer.valueOf(a+b, 16);
					if(val > 0 && GBKChars.chars.contains(val))
					{
						ch.append( (char)val );
					}
					
					for(short j=0;j<2;j++)
					{
						val = ogiBytes[index+i+j] & 0xFF;
						if(val >= 32 && val <= 126)
						{
							en.append( (char)val );
						}
						
					}
				}
				
				i++;
			}
			
			int chLength = ch.length()*2;
			if(chLength > en.length())
			{
				if((float)chLength/(float)ExtractorUtil.SECTOR_SIZE > weight);
				{
					content.append(ch);
				}
			}
			else
			{
				if((float)en.length()/(float)ExtractorUtil.SECTOR_SIZE > weight && en.toString().split(" ").length > 4)
				{	
					content.append(en);
				}
			}
			
			index += ExtractorUtil.SECTOR_SIZE;
		}
		
		return content.toString();
	}
	
	public static String extract(String filePath, int[] err)
	{
		System.out.println("Current File: "+ filePath);
		byte[] ogiBytes = null;
		try {
			ogiBytes = FileUtil.readBinFile(filePath);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(ogiBytes == null || ogiBytes.length == 0)
		{
			err[0] = ExtractorUtil.EMPTY_FILE_STREAM;
			return "";
		}
		
		if(ExtractorUtil.isRTFFormat(ogiBytes))
		{
			return RTFExtractor.extract(new String(ogiBytes), err, 1);
		}
		
		String fileType = getFileType(filePath);
		String content = "";
		if(WORD_TYPE.indexOf(fileType) > -1 || DOT_TYPE.indexOf(fileType) > -1)
		{
			content = WordExtractor.extract(ogiBytes, err);
			if(err[0] == ExtractorUtil.CANNOT_PARSE_WORD)
			{
				return extract(ogiBytes, err, fileType);
			}
			else
			{
				return content;
			}
		}
		
		if(PPT_TYPE.indexOf(fileType) > -1)
		{
			content = PPTExtractor.extract(ogiBytes, err);
			if(err[0] == ExtractorUtil.CANNOT_PARSE_PPT)
			{
				return extract(ogiBytes, err, fileType);
			}
			else
			{
				return content;
			}
		}
		
		if(EXCEL_TYPE.indexOf(fileType) > -1)
		{
			content = ExcelExtractor.extract(ogiBytes, err);
			if(err[0] == ExtractorUtil.CANNOT_PARSE_EXCEL)
			{
				return extract(ogiBytes, err, fileType);
			}
			else
			{
				return content;
			}
		}
		
		err[0] = -1;
		return "";
	}
	
	public static void main(String[] args)
	{
		String folder = "D:\\tools\\OfficeTest";
		File root = new File(folder);
		if(!root.isDirectory())
		{
			return;
		}
		
		File[] children = root.listFiles();
		int[] err = new int[1];
		int totalFiles = 0;
		int exactCount = 0;
		int hardCount = 0;
		for(int i=0;i<children.length;i++)
		{
			File file = children[i];
			String content = extract(file.getAbsolutePath(), err);
			if(err[0] == -1)
			{
				continue;
			}
			
			totalFiles++;
			
			if(err[0] == ExtractorUtil.EXTRACTING_DONE)
			{
				exactCount++;
			}
			
			if
			(
				err[0] == ExtractorUtil.CANNOT_PARSE_EXCEL || 
				err[0] == ExtractorUtil.CANNOT_PARSE_PPT 	||
				err[0] == ExtractorUtil.CANNOT_PARSE_WORD
			)
			{
				hardCount++;
			}
			
			try {
				FileUtil.writeAscFile("D:\\tools\\OfficeResult\\"+ file.getName() +".txt", content, false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		int failedCount = totalFiles - exactCount - hardCount;
		System.out.println("||==================================||");
		System.out.println("totalFiles: "+ totalFiles);
		System.out.println("exactCount: "+ exactCount);
		System.out.println("hardCount: "+ hardCount);
		System.out.println("failedCount: "+ failedCount);
	}
	
	public static void main2(String[] args) throws Exception
	{
		String text = OfficeExtractor.extract("E:\\cn.rtf", new int[1]);
		
		FileUtil.writeAscFile("E:\\output.txt", text, false);
		
		System.out.println("Done!");
	}
	
}
