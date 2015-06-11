package dxj.extractor.main.util;

public class TextFilter {
	
	public final static short CODE_FlATE = 1;
	public final static short CODE_LZW = 2;
	public final static short CODE_ASC85 = 3;
	public final static short CODE_ASCHEX = 4;
	
	public int start = 0;
	public int length = 0;
	public String raw = "";
	
	public short getCodeMethod()
	{
		if(raw.indexOf("/Filter/FlateDecode") > -1 || raw.indexOf("/Filter/Fl") > -1)
		{
			return CODE_FlATE;
		}
		
		if(raw.indexOf("/Filter/LZWDecode") > -1)
		{
			return CODE_LZW;
		}
		
		if(raw.indexOf("/Filter/ASCII85Decode") > -1 || raw.indexOf("/Filter/A85") > -1)
		{
			return CODE_ASC85;
		}
		
		if(raw.indexOf("/Filter/ASCIIHexDecode") > -1 || raw.indexOf("/Filter/AHx") > -1)
		{
			return CODE_ASCHEX;
		}
		
		return 0;
	}
}