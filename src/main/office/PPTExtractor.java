
package dxj.extractor.main.office;

import dxj.extractor.main.util.ExtractorUtil;
import dxj.extractor.main.util.Stream;
import dxj.extractor.main.util.FileUtil;


public class PPTExtractor {

	private static int findTextRecords(Stream stream, byte[] bytes, int start, StringBuilder content, int[] offset) 
	{
		if(start < 0)
		{
			return -1;
		}
		
		byte opt = bytes[start];
		int container = opt & 0x0f;
		if(container == 0x0f) 
		{
			return start+8;
		}
		
		offset[0] = start + 2;
		int type = stream.getShort(offset);
		
		offset[0] = start + 4;
		int len = stream.getInteger(offset);

		if(type == 0x0FA8) 
		{
			ExtractorUtil.bytesToString(bytes, content, start+8, len, 1);
			System.out.println("Text Bytes Atom found!");
		}
	
		if(type == 0x0FA0)
		{
			ExtractorUtil.bytesToString(bytes, content, start+8, len, 0);
			System.out.println("Text Chars Atom found!");
		}

		int newStart = start + 8 + len;
		if(newStart > bytes.length - 8) 
		{
			newStart = -1;
		}
		
		return newStart;
	}
	
	public static String extract(byte[] ogiBytes, int[] err)
	{
		System.out.println("Total bytes: "+ ogiBytes.length);
		
		if(!ExtractorUtil.isOfficeFormat(ogiBytes))
		{
			System.out.println("Not the ppt file!");
			err[0] = ExtractorUtil.NOT_PPT_FORMAT;
			return "";
		}
		
		Stream stream = new Stream(ogiBytes);
		int[] offset = new int[1];
		
		offset[0] = ExtractorUtil.DIR_SECT_POS;
		int dirSect1 = stream.getInteger(offset);
		int pptDocument = ExtractorUtil.getDirStreamOffset(ogiBytes, dirSect1, ExtractorUtil.STREAM_NAME_PPT);
		if(pptDocument <= 0)
		{
			System.out.println("This version of ppt can not be parsed!");
			err[0] = ExtractorUtil.CANNOT_PARSE_PPT;
			return "";
		}
		
		offset[0] = pptDocument + ExtractorUtil.START_SECT_OFFSET;
		int startSect = stream.getInteger(offset);
		int docStart = (startSect + 1) * ExtractorUtil.SECTOR_SIZE;
		int docLength = stream.getInteger(offset);
		
		byte[] bytes = new byte[docLength];
		System.arraycopy(ogiBytes, docStart, bytes, 0, docLength);
		
		stream = new Stream(bytes);
		
		StringBuilder content = new StringBuilder();
		int start = 0;
		while(start != -1) 
		{
			start = findTextRecords(stream, bytes, start, content, offset);
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
		String text = PPTExtractor.extract("D:\\tools\\OfficeTest\\cn-t.ppt", new int[1]);
		
		FileUtil.writeAscFile("E:\\output.txt", text, false);
		
		System.out.println("Done!");
	}
}
