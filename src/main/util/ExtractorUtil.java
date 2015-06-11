package dxj.extractor.main.util;

import java.io.ByteArrayOutputStream;
import java.util.zip.Inflater;


public class ExtractorUtil {

	public final static String STREAM_NAME_EXCEL = "Workbook";
	public final static String STREAM_NAME_PPT = "PowerPoint Document";
	public final static String STREAM_NAME_WORD = "WordDocument";
	public final static String STREAM_NAME_ONETABLE = "1Table";
	
	public final static int DIR_SECT_POS = 48;
	public final static int SECTOR_SIZE = 512;
	public final static int START_SECT_OFFSET = 116;
	public final static int WORD_CLX_OFFSET = 418;
	
	public final static int NOT_WORD_FORMAT = -1000;
	public final static int NOT_RTF_FORMAT = -1001;
	public final static int NOT_PPT_FORMAT = -1002;
	public final static int NOT_EXCEL_FORMAT = -1003;
	public final static int NOT_PDF_FORMAT = -1004;
	
	public final static int CANNOT_PARSE_WORD = -2000;
	public final static int CANNOT_PARSE_RTF = -2001;
	public final static int CANNOT_PARSE_PPT = -2002;
	public final static int CANNOT_PARSE_EXCEL = -2003;
	
	public final static int EXTRACTING_DONE = 0;
	public final static int EMPTY_FILE_STREAM = -999;
	
	public static String bytesToString(byte[] ogiBytes, int start, int length, int fc, int[] offset, int fExtSt)
	{
		StringBuilder content = new StringBuilder();
		int streamLength = start+length;
		if(start < 0 || length < 0 || streamLength >= ogiBytes.length || start >= ogiBytes.length)
		{
			return content.toString();
		}
		
		if(fc == 0)
		{
			for(int i=start;i<streamLength;i++)
			{
				if(i == streamLength - 1)
				{
					return content.toString();
				}
				
				String a = Integer.toHexString(ogiBytes[i+1] & 0xFF);
				String b = Integer.toHexString(ogiBytes[i]   & 0xFF);
				if(a.length() == 1)
				{
					a = "0"+ a;
				}
				
				if(b.length() == 1)
				{
					b = "0"+ b;
				}
				
				int ch = Integer.valueOf(a+b, 16);
				content.append( (char)ch );
				i++;
			}
		}
		else
		{
			for(int i=start;i<streamLength;i++)
			{
				if(i == ogiBytes.length - 1)
				{
					return content.toString();
				}
				
				int ch = ogiBytes[i] & 0xFF;
				if(fExtSt == 0)
				{
					if(ch < 32 || ch > 126 || (ch > 57 && ch < 64))
					{
						streamLength++;
						if(offset != null && offset.length > 0)
						{
							offset[0]++;
						}
						continue;
					}
				}
				content.append( (char)ch );
			}
		}
		
		return content.toString();
	}
	
	public static void bytesToString(byte[] ogiBytes, StringBuilder content, int start, int length, int fc)
	{
		content.append( bytesToString(ogiBytes, start, length, fc, null, 1) );
	}
	
	public static int getDirStreamOffset(byte[] ogiBytes, int dirSect1, String streamName)
	{
		for(int i=0;i<8;i++)
		{
			int offsetEntry = (dirSect1 + 1)*512 + i*128;
			String content = bytesToString(ogiBytes, offsetEntry, 64, 0, null, 1);
			if(content.toString().indexOf(streamName) > -1)
			{
				return offsetEntry;
			}
		}
		
		return 0;
	}
	
	public static boolean isOfficeFormat(byte[] ogiBytes)
	{
		if(ogiBytes == null || ogiBytes.length < 8)
		{
			return false;
		}
		
		if(
			(ogiBytes[0] & 0xFF) != 208 ||
			(ogiBytes[1] & 0xFF) != 207 ||
			(ogiBytes[2] & 0xFF) != 17 	||
			(ogiBytes[3] & 0xFF) != 224 ||
			(ogiBytes[4] & 0xFF) != 161 ||
			(ogiBytes[5] & 0xFF) != 177 ||
			(ogiBytes[6] & 0xFF) != 26 	||
			(ogiBytes[7] & 0xFF) != 225
		){
			return false;
		}
		
		return true;
	}
	
	public static boolean isRTFFormat(byte[] ogiBytes)
	{
		if(ogiBytes == null || ogiBytes.length < 8)
		{
			return false;
		}
		
		if(
			(ogiBytes[0] & 0xFF) == 0x7B ||
			(ogiBytes[1] & 0xFF) == 0x5C ||
			(ogiBytes[2] & 0xFF) == 0x72 ||
			(ogiBytes[3] & 0xFF) == 0x74 ||
			(ogiBytes[4] & 0xFF) == 0x66 ||
			(ogiBytes[5] & 0xFF) == 0x31 ||
			(ogiBytes[6] & 0xFF) == 0x5C ||
			(ogiBytes[7] & 0xFF) == 0x61
		){
			return true;
		}
		
		short lines = 0;
		for(int i = 0; i < ogiBytes.length; i++)
		{
			if(ogiBytes[i] != 0x0A && ogiBytes[i] != 0x0D)
			{
				continue;
			}
			
			if(lines > 2)
			{
				break;
			}
			
			if(i+1+8 >= ogiBytes.length)
			{
				break;
			}
			
			if(
				(ogiBytes[i+1] & 0xFF) == 0x7B ||
				(ogiBytes[i+2] & 0xFF) == 0x5C ||
				(ogiBytes[i+3] & 0xFF) == 0x72 ||
				(ogiBytes[i+4] & 0xFF) == 0x74 ||
				(ogiBytes[i+5] & 0xFF) == 0x66 ||
				(ogiBytes[i+6] & 0xFF) == 0x31 ||
				(ogiBytes[i+7] & 0xFF) == 0x5C ||
				(ogiBytes[i+8] & 0xFF) == 0x61
			){
				return true;
			}
			
			lines++;
		}
		
		return false;
	}
	
	public static boolean isPdfFormat(byte[] ogiBytes)
	{
		if(ogiBytes == null || ogiBytes.length < 4)
		{
			return false;
		}
		
		if(
			(ogiBytes[0] & 0xFF) == 0x25 ||
			(ogiBytes[1] & 0xFF) == 0x50 ||
			(ogiBytes[2] & 0xFF) == 0x44 ||
			(ogiBytes[3] & 0xFF) == 0x46
		){
			return true;
		}
		
		return false;
	}
	
	public static byte[] decodeLZW(byte[] in) 
	{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        LZWDecoder lzw = new LZWDecoder();
        lzw.decode(in, out);
        return out.toByteArray();
    }
	
	public static boolean isWhitespace(int ch) 
	{
	    return (ch == 0 || ch == 9 || ch == 10 || ch == 12 || ch == 13 || ch == 32);
	}
	 
	public static byte[] decodeASCII85(byte[] in) 
	{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int state = 0;
        int chn[] = new int[5];
        for (int k = 0; k < in.length; ++k) 
        {
            int ch = in[k] & 0xff;
            if (ch == '~')
            {
                break;
            }
            if (isWhitespace(ch))
            {
                continue;
            }
            if (ch == 'z' && state == 0) 
            {
                out.write(0);
                out.write(0);
                out.write(0);
                out.write(0);
                continue;
            }
            if (ch < '!' || ch > 'u')
            {
                throw new RuntimeException("Illegal character in ASCII85Decode.");
            }
            
            chn[state] = ch - '!';
            ++state;
            if (state == 5) 
            {
                state = 0;
                int r = 0;
                for (int j = 0; j < 5; ++j)
                {
                    r = r * 85 + chn[j];
	                out.write((byte)(r >> 24));
	                out.write((byte)(r >> 16));
	                out.write((byte)(r >> 8));
	                out.write((byte)r);
                }
            }
        }
        
        int r = 0;
        if (state == 1)
        {
            throw new RuntimeException("Illegal length in ASCII85Decode.");
        }
        
        if (state == 2) 
        {
            r = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85 + 85 * 85 * 85  + 85 * 85 + 85;
            out.write((byte)(r >> 24));
        }
        else if (state == 3) 
        {
            r = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85  + chn[2] * 85 * 85 + 85 * 85 + 85;
            out.write((byte)(r >> 24));
            out.write((byte)(r >> 16));
        }
        else if (state == 4) 
        {
            r = chn[0] * 85 * 85 * 85 * 85 + chn[1] * 85 * 85 * 85  + chn[2] * 85 * 85  + chn[3] * 85 + 85;
            out.write((byte)(r >> 24));
            out.write((byte)(r >> 16));
            out.write((byte)(r >> 8));
        }
        return out.toByteArray();
    }
	
	public static int getHexString(int v) 
	{
	    if (v >= '0' && v <= '9')
	        return v - '0';
	    if (v >= 'A' && v <= 'F')
	        return v - 'A' + 10;
	    if (v >= 'a' && v <= 'f')
	        return v - 'a' + 10;
	    return -1;
	}
	 
	public static byte[] decodeASCIIHex(byte[] in) 
	{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean first = true;
        int n1 = 0;
        for (int k = 0; k < in.length; ++k) {
            int ch = in[k] & 0xff;
            if (ch == '>')
            {
                break;
            }
            if (isWhitespace(ch))
            {
                continue;
            }
            int n = getHexString(ch);
            if (n == -1)
            {
                throw new RuntimeException("Illegal character in ASCIIHexDecode.");
            }
            if (first)
            {
                n1 = n;
            }
            else
            {
                out.write((byte)((n1 << 4) + n));
            }
            first = !first;
        }
        if (!first)
        {
            out.write((byte)(n1 << 4));
        }
        return out.toByteArray();
    }
	
	public static byte[] decodeFlate(byte[] in)
	{
		Inflater decompresser = new Inflater();
		decompresser.setInput(in);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try
		{
			byte[] buffer = new byte[4096];
			while(true)
			{
				int count = decompresser.inflate(buffer);
				if(count == 0 || decompresser.finished())
				{
					break;
				}
				
				out.write(buffer, 0, count);
			}
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return out.toByteArray();
	}
}
