package dxj.extractor.main.office;

import dxj.extractor.main.util.ExtractorUtil;
import dxj.extractor.main.util.Stream;
import dxj.extractor.main.util.FileUtil;

public class ExcelExtractor {
	
	private static void processBoundSheet(Stream stream, int lbPlyPos, int docStart, int docLength, StringBuilder content, String[] sst)
	{
		if(sst == null || sst.length == 0)
		{
			return;
		}
		
		int[] offset = new int[1];
		
		/**
		 * skip the BOF record
		 * */
		offset[0] = lbPlyPos + 20;
		short type = stream.getShort(offset);
		if(type != 523)
		{
			System.out.println("Index record not found!");
			return ;
		}
		
		int length = stream.getShort(offset);
		offset[0] += length;
		
		int streamLength = docStart+docLength;
		while(offset[0] < streamLength)
		{
			type = stream.getShort(offset);
			length = stream.getShort(offset);
			if(type == 253)
			{
				offset[0] += 6;
				int sstIndex = stream.getInteger(offset);
				if(sstIndex < sst.length)
				{
					content.append( sst[sstIndex] );
				}
			}
			else
			{
				offset[0] += length;
			}
		}
	}
	
	private static String[] getSST(Stream stream, int docStart, int docLength)
	{
		int[] offset = new int[1];
		offset[0] = docStart;
		int streamLength = docStart+docLength;
		
		String[] sst = null;
		while(offset[0] < streamLength)
		{
			short type = stream.getShort(offset);
			short length = stream.getShort(offset);
			
			if(type == 252)
			{
				offset[0] += 4;
				int sstLength = stream.getInteger(offset);
				sst = new String[sstLength];
				for(int i=0;i<sstLength;i++)
				{
					if(offset[0] <= 0)
					{
						break;
					}
					
					int cch = stream.getShort(offset);
					byte flags = stream.getByte(offset);
					int fHightByte = flags & 1;
					int fExtSt = flags >> 2;
					int fRichSt = flags >> 3;
					int rgRun = 0;
					
					int fc = fHightByte != 0 ? 0 : 1;
					cch = fc == 0 ? cch*2 : cch;
					
					if(fRichSt != 0)
					{
						short cRun = stream.getShort(offset);
						rgRun = cRun*4;
					}
					
					if(fExtSt != 0)
					{
						offset[0] += 4;
					}
					sst[i] = ExtractorUtil.bytesToString(stream.getBytes(), offset[0], cch, fc, offset, fExtSt);
					if(fExtSt != 0)
					{
						offset[0] += 14 + 2;
					}
					
					offset[0] += (cch + rgRun);
				}
				
				break;
			}
			else
			{
				offset[0] += length;
			}
			
		}
		
		return sst;
	}
	
	public static String extract(byte[] ogiBytes, int[] err) 
	{
		System.out.println("Total bytes: "+ ogiBytes.length);
		
		if(!ExtractorUtil.isOfficeFormat(ogiBytes))
		{
			System.out.println("Not the doc file!");
			err[0] = ExtractorUtil.NOT_EXCEL_FORMAT;
			return "";
		}
		
		Stream stream = new Stream(ogiBytes);
		int[] offset = new int[1];
		
		offset[0] = ExtractorUtil.DIR_SECT_POS;
		int dirSect1 = stream.getInteger(offset);
		int workbook = ExtractorUtil.getDirStreamOffset(ogiBytes, dirSect1, ExtractorUtil.STREAM_NAME_EXCEL);
		if(workbook <= 0)
		{
			System.out.println("This version of xls can not be parsed!");
			err[0] = ExtractorUtil.CANNOT_PARSE_EXCEL;
			return "";
		}
		
		offset[0] = workbook + ExtractorUtil.START_SECT_OFFSET;
		int startSect = stream.getInteger(offset);
		int docStart = (startSect + 1) * ExtractorUtil.SECTOR_SIZE;
		int docLength = stream.getInteger(offset);
		
		StringBuilder content = new StringBuilder();
		
		offset[0] = docStart;
		int streamLength = docStart+docLength;
		String[] sst = getSST(stream, docStart, docLength);
		while(offset[0] < streamLength)
		{
			short type = stream.getShort(offset);
			short length = stream.getShort(offset);
			
			if(type == 133)
			{
				int lbPlyPos = stream.getInteger(offset) + docStart;
				System.out.println("BoundSheet found! "+ lbPlyPos);
				processBoundSheet(stream, lbPlyPos, docStart, docLength, content, sst);
				
				offset[0] += (length - 4);
				break;
			}
			else
			{
				offset[0] += length;
			}
			
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
		String text = ExcelExtractor.extract("D:\\tools\\OfficeTest\\QOKX200812111338024464306973742910328.xls", new int[1]);
		
		FileUtil.writeAscFile("E:\\output.txt", text, false);
		
		System.out.println("Done!");
		
	}
}
