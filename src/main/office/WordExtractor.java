package dxj.extractor.main.office;

import dxj.extractor.main.util.ExtractorUtil;
import dxj.extractor.main.util.Stream;
import dxj.extractor.main.util.FileUtil;


public class WordExtractor {
	
	public static String extract(byte[] ogiBytes, int[] err)
	{
		System.out.println("Total bytes: "+ ogiBytes.length);
		
		if(!ExtractorUtil.isOfficeFormat(ogiBytes))
		{
			System.out.println("Not the doc file!");
			err[0] = ExtractorUtil.NOT_WORD_FORMAT;
			return "";
		}
		
		StringBuilder content = new StringBuilder();
		
		Stream stream = new Stream(ogiBytes);
		int[] offset = new int[1];
		
		offset[0] = ExtractorUtil.DIR_SECT_POS;
		int dirSect1 = stream.getInteger(offset);
		
		int wordDocument = ExtractorUtil.getDirStreamOffset(ogiBytes, dirSect1, ExtractorUtil.STREAM_NAME_WORD);
		offset[0] = wordDocument + ExtractorUtil.START_SECT_OFFSET;
		int startSect = stream.getInteger(offset);
		int docStream = (startSect + 1) * ExtractorUtil.SECTOR_SIZE;
		
		offset[0] = docStream + ExtractorUtil.WORD_CLX_OFFSET;
		int fcClx = stream.getInteger(offset);
		int oneTable = ExtractorUtil.getDirStreamOffset(ogiBytes, dirSect1, ExtractorUtil.STREAM_NAME_ONETABLE);
		if(fcClx <= 0 || oneTable <= 0)
		{
			System.out.println("This version of doc can not be parsed!");
			err[0] = ExtractorUtil.CANNOT_PARSE_WORD;
			return "";
		}
		
		offset[0] = oneTable + ExtractorUtil.START_SECT_OFFSET;
		startSect = stream.getInteger(offset);
		int tableStream = (startSect + 1) * ExtractorUtil.SECTOR_SIZE;
		
	    int offsetClx = tableStream + fcClx;
	    offset[0] = offsetClx + 1;
	    int lcb = stream.getInteger(offset);
	   
	    int countPcd = (lcb - 4)/12;
	    if(countPcd > 1024)
	    {
	    	System.out.println("This version of doc can not be parsed!");
			err[0] = ExtractorUtil.CANNOT_PARSE_WORD;
			return "";
	    }
	    
	    int countCp = (lcb - countPcd*8)/4;
	    int offsetPlcpcd = offsetClx + 5;
	   
	    for(int i=0;i<countPcd;i++)
	    {
	    	int offsetPcd = offsetPlcpcd + countCp*4 + i*8;
	    	
	    	offset[0] = offsetPcd + 2;
	    	int start = stream.getInteger(offset);
	    	int fc = start >> 30;
	    	start = (start << 2) >> 2;
	   
			offset[0] = offsetPlcpcd + i*4;
			int cpPre = stream.getInteger(offset);
			int cpNext = stream.getInteger(offset);
			int length = cpNext - cpPre -1;
			if(fc == 0)
			{
				length *= 2;
			}
			else
			{
				start = start/2;
			}
			
			start += 512;
			ExtractorUtil.bytesToString(ogiBytes, content, start, length, fc);
	    
	    	System.out.println(start +", "+ length);
	    }
	    
	    err[0] = ExtractorUtil.EXTRACTING_DONE;
	    return content.toString();
	}
	
	public static String extract(String filePath, int[] err)
	{
		byte[] ogiBytes = null;
		try {
			ogiBytes = FileUtil.readBinFile(filePath);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return extract(ogiBytes, err);
	}
	
	public static void main(String[] args) throws Exception
	{
		String text = WordExtractor.extract("D:\\tools\\OfficeTest\\2.dot", new int[1]);
		
		FileUtil.writeAscFile("E:\\output.txt", text, false);
		
		System.out.println("Done!");
		
	}
}
