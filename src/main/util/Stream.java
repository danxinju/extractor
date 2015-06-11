package dxj.extractor.main.util;

import java.nio.ByteBuffer;

/**
 * �����ķ�װ
 * @author Administrator
 * @date 2010-02-15
 */
public class Stream
{
    static int CRC_TABLE[] =
    {
        0x00000000, 0x77073096, 0xee0e612c, 0x990951ba, 0x076dc419,
        0x706af48f, 0xe963a535, 0x9e6495a3, 0x0edb8832, 0x79dcb8a4,
        0xe0d5e91e, 0x97d2d988, 0x09b64c2b, 0x7eb17cbd, 0xe7b82d07,
        0x90bf1d91, 0x1db71064, 0x6ab020f2, 0xf3b97148, 0x84be41de,
        0x1adad47d, 0x6ddde4eb, 0xf4d4b551, 0x83d385c7, 0x136c9856,
        0x646ba8c0, 0xfd62f97a, 0x8a65c9ec, 0x14015c4f, 0x63066cd9,
        0xfa0f3d63, 0x8d080df5, 0x3b6e20c8, 0x4c69105e, 0xd56041e4,
        0xa2677172, 0x3c03e4d1, 0x4b04d447, 0xd20d85fd, 0xa50ab56b,
        0x35b5a8fa, 0x42b2986c, 0xdbbbc9d6, 0xacbcf940, 0x32d86ce3,
        0x45df5c75, 0xdcd60dcf, 0xabd13d59, 0x26d930ac, 0x51de003a,
        0xc8d75180, 0xbfd06116, 0x21b4f4b5, 0x56b3c423, 0xcfba9599,
        0xb8bda50f, 0x2802b89e, 0x5f058808, 0xc60cd9b2, 0xb10be924,
        0x2f6f7c87, 0x58684c11, 0xc1611dab, 0xb6662d3d, 0x76dc4190,
        0x01db7106, 0x98d220bc, 0xefd5102a, 0x71b18589, 0x06b6b51f,
        0x9fbfe4a5, 0xe8b8d433, 0x7807c9a2, 0x0f00f934, 0x9609a88e,
        0xe10e9818, 0x7f6a0dbb, 0x086d3d2d, 0x91646c97, 0xe6635c01,
        0x6b6b51f4, 0x1c6c6162, 0x856530d8, 0xf262004e, 0x6c0695ed,
        0x1b01a57b, 0x8208f4c1, 0xf50fc457, 0x65b0d9c6, 0x12b7e950,
        0x8bbeb8ea, 0xfcb9887c, 0x62dd1ddf, 0x15da2d49, 0x8cd37cf3,
        0xfbd44c65, 0x4db26158, 0x3ab551ce, 0xa3bc0074, 0xd4bb30e2,
        0x4adfa541, 0x3dd895d7, 0xa4d1c46d, 0xd3d6f4fb, 0x4369e96a,
        0x346ed9fc, 0xad678846, 0xda60b8d0, 0x44042d73, 0x33031de5,
        0xaa0a4c5f, 0xdd0d7cc9, 0x5005713c, 0x270241aa, 0xbe0b1010,
        0xc90c2086, 0x5768b525, 0x206f85b3, 0xb966d409, 0xce61e49f,
        0x5edef90e, 0x29d9c998, 0xb0d09822, 0xc7d7a8b4, 0x59b33d17,
        0x2eb40d81, 0xb7bd5c3b, 0xc0ba6cad, 0xedb88320, 0x9abfb3b6,
        0x03b6e20c, 0x74b1d29a, 0xead54739, 0x9dd277af, 0x04db2615,
        0x73dc1683, 0xe3630b12, 0x94643b84, 0x0d6d6a3e, 0x7a6a5aa8,
        0xe40ecf0b, 0x9309ff9d, 0x0a00ae27, 0x7d079eb1, 0xf00f9344,
        0x8708a3d2, 0x1e01f268, 0x6906c2fe, 0xf762575d, 0x806567cb,
        0x196c3671, 0x6e6b06e7, 0xfed41b76, 0x89d32be0, 0x10da7a5a,
        0x67dd4acc, 0xf9b9df6f, 0x8ebeeff9, 0x17b7be43, 0x60b08ed5,
        0xd6d6a3e8, 0xa1d1937e, 0x38d8c2c4, 0x4fdff252, 0xd1bb67f1,
        0xa6bc5767, 0x3fb506dd, 0x48b2364b, 0xd80d2bda, 0xaf0a1b4c,
        0x36034af6, 0x41047a60, 0xdf60efc3, 0xa867df55, 0x316e8eef,
        0x4669be79, 0xcb61b38c, 0xbc66831a, 0x256fd2a0, 0x5268e236,
        0xcc0c7795, 0xbb0b4703, 0x220216b9, 0x5505262f, 0xc5ba3bbe,
        0xb2bd0b28, 0x2bb45a92, 0x5cb36a04, 0xc2d7ffa7, 0xb5d0cf31,
        0x2cd99e8b, 0x5bdeae1d, 0x9b64c2b0, 0xec63f226, 0x756aa39c,
        0x026d930a, 0x9c0906a9, 0xeb0e363f, 0x72076785, 0x05005713,
        0x95bf4a82, 0xe2b87a14, 0x7bb12bae, 0x0cb61b38, 0x92d28e9b,
        0xe5d5be0d, 0x7cdcefb7, 0x0bdbdf21, 0x86d3d2d4, 0xf1d4e242,
        0x68ddb3f8, 0x1fda836e, 0x81be16cd, 0xf6b9265b, 0x6fb077e1,
        0x18b74777, 0x88085ae6, 0xff0f6a70, 0x66063bca, 0x11010b5c,
        0x8f659eff, 0xf862ae69, 0x616bffd3, 0x166ccf45, 0xa00ae278,
        0xd70dd2ee, 0x4e048354, 0x3903b3c2, 0xa7672661, 0xd06016f7,
        0x4969474d, 0x3e6e77db, 0xaed16a4a, 0xd9d65adc, 0x40df0b66,
        0x37d83bf0, 0xa9bcae53, 0xdebb9ec5, 0x47b2cf7f, 0x30b5ffe9,
        0xbdbdf21c, 0xcabac28a, 0x53b39330, 0x24b4a3a6, 0xbad03605,
        0xcdd70693, 0x54de5729, 0x23d967bf, 0xb3667a2e, 0xc4614ab8,
        0x5d681b02, 0x2a6f2b94, 0xb40bbe37, 0xc30c8ea1, 0x5a05df1b,
        0x2d02ef8d
    };

    private byte[] bytes;

    /**
     * ����������
     */
    public Stream()
    {
        bytes = new byte[0];
    }

    /**
     * ���������������
     * @param size ���Ĵ�С
     */
    public Stream(int size)
    {
        bytes = new byte[size];
    }

    /**
     * ���������������
     * @param bytes �ֽ�����
     */
    public Stream(byte[] bytes)
    {
        this.bytes = bytes;
    }

    public boolean equals(Object stream)
    {
        return bytes.equals(((Stream)stream).getBytes());
    }

    public int hashCode()
    {
        int hkey = 0;

        for(int i=0; i<bytes.length ;i++)
        {
            if(hkey >= 0)
            {
                hkey = Stream.CRC_TABLE[(hkey ^ bytes[i]) & 0xFF] ^ (hkey >> 8);
            }
            else
            {
                hkey = Stream.CRC_TABLE[(int)((0x100000000L+hkey) ^ bytes[i]) & 0xFF] ^ (int)((0x100000000L+hkey) >> 8);
            }
        }

        return hkey;
    }

    /**
     * ��ȡ�ֽ�
     * @return �ֽ�����
     */
    public byte[] getBytes()
    {
        return bytes;
    }

    /**
     * ��ȡ�ֽ����鳤��
     * @return �ֽ����鳤��
     */
    public int length()
    {
        if(bytes == null)
        {
            return 0;
        }

        return bytes.length;
    }

    /**
     * ��ȡutf8���������ַ���ֽ����鳤��
     * @param string �ַ�
     * @return �ֽ����鳤��
     */
    public static int length(String string)
    {
        try
        {
            return string.getBytes("utf8").length;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * ��ȡutf8���������ַ���ֽ�����
     * @param string �ַ�
     * @return �ֽ�����
     */
    public static byte[] getBytes(String string)
    {
        try
        {
            return string.getBytes("utf8");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * ���ֽ�����ת��Ϊutf8������ַ�
     * @param bytes �ֽ�����
     * @return �µ��ַ�
     */
    public static String setBytes(byte[] bytes)
    {
        try
        {
            return new String(bytes, "utf8");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean bitGet(byte bt, int pt)
    {
        switch(pt)
        {
        case 0:
            return (bt & 0X80) != 0;
        case 1:
            return (bt & 0X40) != 0;
        case 2:
            return (bt & 0X20) != 0;
        case 3:
            return (bt & 0X10) != 0;
        case 4:
            return (bt & 0X08) != 0;
        case 5:
            return (bt & 0X04) != 0;
        case 6:
            return (bt & 0X02) != 0;
        case 7:
            return (bt & 0X01) != 0;
        default:
            return false;
        }
    }

    public static byte bitSet(byte bt, int pt, boolean flag)
    {
        if(flag)
        {
            switch(pt)
            {
            case 0:
                return (byte)(bt | 0X80);
            case 1:
                return (byte)(bt | 0X40);
            case 2:
                return (byte)(bt | 0X20);
            case 3:
                return (byte)(bt | 0X10);
            case 4:
                return (byte)(bt | 0X08);
            case 5:
                return (byte)(bt | 0X04);
            case 6:
                return (byte)(bt | 0X02);
            case 7:
                return (byte)(bt | 0X01);
            default:
                return 0;
            }
        }
        else
        {
            switch(pt)
            {
            case 0:
                return (byte)(bt & 0X7F);
            case 1:
                return (byte)(bt & 0XBF);
            case 2:
                return (byte)(bt & 0XDF);
            case 3:
                return (byte)(bt & 0XEF);
            case 4:
                return (byte)(bt & 0XF7);
            case 5:
                return (byte)(bt & 0XFB);
            case 6:
                return (byte)(bt & 0XFD);
            case 7:
                return (byte)(bt & 0XFE);
            default:
                return 0;
            }
        }
    }

    public static int bitSet(boolean[] bits)
    {
        byte[] bytes = new byte[4];
        int size = Math.min(32, bits.length);

        for(int i=0; i<4; i++)
        {
            for(int j=0; j<8; j++)
            {
                int k = i*8 + j;
                if(k < size && bits[k])
                {
                    bytes[i] = Stream.bitSet(bytes[i], j, true);
                }
            }
        }

        return Integer.reverseBytes(ByteBuffer.wrap(bytes).getInt());
    }

    public static boolean[] bitGet(int bit)
    {
        boolean[] bits = new boolean[32];
        byte[] bytes = ByteBuffer.allocate(4).putInt(Integer.reverseBytes(bit)).array();

        for(int i=0; i<4; i++)
        {
            for(int j=0; j<8; j++)
            {
                bits[i*8+j] = Stream.bitGet(bytes[i], j);
            }
        }

        return bits;
    }

    /**
     * ���ֽ����ɳ�����ֵ
     * @param low ��4λintֵ
     * @param high ��4λintֵ
     * @return ������ֵ
     */
    public static long makeLong(int low, int high)
    {
        ByteBuffer buf = ByteBuffer.allocate(8);

        buf.putInt(0, high);
        buf.putInt(4, low);

        return buf.getLong();
    }

    /**
     * ��ȡ������ֵ�ĵ�4λ����ֵ
     * @param value ������ֵ
     * @return ����ֵ
     */
    public static int lowInteger(long value)
    {
        ByteBuffer buf = ByteBuffer.allocate(8);

        buf.putLong(value);

        return buf.getInt(4);
    }

    /**
     * ��ȡ������ֵ�ĸ�4λ����ֵ
     * @param value ������ֵ
     * @return ����ֵ
     */
    public static int highInteger(long value)
    {
        ByteBuffer buf = ByteBuffer.allocate(8);

        buf.putLong(value);

        return buf.getInt(0);
    }

    /**
     * CRCУ��
     */
    public int crc32()
    {
        int i, tab, crc32 = 0;

        if(length() == 0)
        {
            return crc32;
        }

        crc32 = (int)(crc32 ^ 0xFFFFFFFFL);

        for(i=0; i<length(); i++)
        {
            if(crc32 >= 0)
            {
                tab = CRC_TABLE[(crc32 ^ bytes[i]) & 0xFF];
                if(tab >= 0)
                {
                    crc32 = tab ^ (crc32 >> 8);
                }
                else
                {
                    crc32 = (int)((0x100000000L+tab) ^ (crc32 >> 8));
                }
            }
            else
            {
                tab = CRC_TABLE[(int)((0x100000000L+crc32) ^ bytes[i]) & 0xFF];
                if(tab >= 0)
                {
                    crc32 = tab ^ (int)((0x100000000L+crc32) >> 8);
                }
                else
                {
                    crc32 = (int)((0x100000000L+tab) ^ (int)((0x100000000L+crc32) >> 8));
                }
            }
        }

        if(crc32 >= 0)
        {
            return (int)(crc32 ^ 0xFFFFFFFFL);
        }
        else
        {
            return (int)((0x100000000L+crc32) ^ 0xFFFFFFFFL);
        }
    }

    /**
     * ��ԭʼ��׷���±�����
     * @param bytes ������
     * @return �µı�����
     */
    public Stream append(byte[] bytes)
    {
        byte[] rbytes = new byte[length()+bytes.length];
        if(length() > 0)
        {
            System.arraycopy(this.bytes, 0, rbytes, 0, length());
        }
        System.arraycopy(bytes, 0, rbytes, length(), bytes.length);

        return new Stream(rbytes);
    }

    /**
     * ��ԭʼ��׷���±�����
     * @param bytes ������
     * @param length ����
     * @return �µı�����
     */
    public Stream append(byte[] bytes, int length)
    {
        if(length > bytes.length)
        {
            length = bytes.length;
        }

        byte[] rbytes = new byte[length()+length];
        if(length() > 0)
        {
            System.arraycopy(this.bytes, 0, rbytes, 0, length());
        }
        System.arraycopy(bytes, 0, rbytes, length(), length);

        return new Stream(rbytes);
    }

    /**
     * ��ԭʼ���в����±�����
     * @param offset �����±�������λ��
     * @param bytes ������
     * @return �µı�����
     */
    public Stream insert(int offset, byte[] bytes)
    {
        if(offset > length())
        {
            offset = length();
        }

        byte[] rbytes = new byte[length()+bytes.length];
        if(offset > 0)
        {
            System.arraycopy(this.bytes, 0, rbytes, 0, offset);
        }
        System.arraycopy(bytes, 0, rbytes, offset, bytes.length);
        if(offset < length())
        {
            System.arraycopy(this.bytes, offset, rbytes, offset+bytes.length, length()-offset);
        }

        return new Stream(rbytes);
    }

    /**
     * ��ԭʼ���в����±�����
     * @param offset �����±�������λ��
     * @param bytes ������
     * @param length ����
     * @return �µı�����
     */
    public Stream insert(int offset, byte[] bytes, int length)
    {
        if(length > bytes.length)
        {
            length = bytes.length;
        }
        if(offset > length())
        {
            offset = length();
        }

        byte[] rbytes = new byte[length()+length];
        if(offset > 0)
        {
            System.arraycopy(this.bytes, 0, rbytes, 0, offset);
        }
        System.arraycopy(bytes, 0, rbytes, offset, length);
        if(offset < length())
        {
            System.arraycopy(this.bytes, offset, rbytes, offset+length, length()-offset);
        }

        return new Stream(rbytes);
    }

    /**
     * �����е�ĳһ�ν����滻
     * @param start �滻��ʼλ��
     * @param end �滻����λ��
     * @param bytes ������
     * @return �µı�����
     */
    public Stream replace(int start, int end, byte[] bytes)
    {
        if(end >length())
        {
            end = length();
        }
        if(start > end)
        {
            start = end;
        }

        byte[] rbytes = new byte[length()+start-end+bytes.length];
        if(start > 0)
        {
            System.arraycopy(this.bytes, 0, rbytes, 0, start);
        }
        System.arraycopy(bytes, 0, rbytes, start, bytes.length);
        if(end < length())
        {
            System.arraycopy(this.bytes, end, rbytes, start+bytes.length, length()-end);
        }

        return new Stream(rbytes);
    }

    /**
     * �����е�ĳһ�ν����滻
     * @param start �滻��ʼλ��
     * @param end �滻����λ��
     * @param bytes ������
     * @param length ����
     * @return �µı�����
     */
    public Stream replace(int start, int end, byte[] bytes, int length)
    {
        if(length > bytes.length)
        {
            length = bytes.length;
        }
        if(end >length())
        {
            end = length();
        }
        if(start > end)
        {
            start = end;
        }

        byte[] rbytes = new byte[length()+start-end+length];
        if(start > 0)
        {
            System.arraycopy(this.bytes, 0, rbytes, 0, start);
        }
        System.arraycopy(bytes, 0, rbytes, start, length);
        if(end < length())
        {
            System.arraycopy(this.bytes, end, rbytes, start+length, length()-end);
        }

        return new Stream(rbytes);
    }

    /**
     * ɾ�����е�ĳһ��
     * @param start ɾ����ʼλ��
     * @param end ɾ�����λ��
     * @return �µı�����
     */
    public Stream delete(int start, int end)
    {
        if(end > length())
        {
            end = length();
        }
        if(start > end)
        {
            start = end;
        }

        byte[] rbytes = new byte[length()+start-end];
        if(start > 0)
        {
            System.arraycopy(this.bytes, 0, rbytes, 0, start);
        }
        if(end < length())
        {
            System.arraycopy(this.bytes, end, rbytes, start, length()-end);
        }

        return new Stream(rbytes);
    }

    /**
     * ��ĳһλ�����ȡ��
     * @param beginIndex ������ʼλ��
     * @return �µı�����
     */
    public Stream substream(int beginIndex)
    {
        if(beginIndex > length())
        {
            beginIndex = length();
        }
        byte[] buf = new byte[length()-beginIndex];
        if(beginIndex < length())
        {
            System.arraycopy(buf, 0, bytes, beginIndex, length()-beginIndex);
        }

        return new Stream(buf);
    }

    /**
     * ȡ����ĳ����
     * @param beginIndex ������ʼλ��
     * @param endIndex   �������Ϊֹ
     * @return �µı�����
     */
    public Stream substream(int beginIndex, int endIndex)
    {
        if(endIndex > length())
        {
            endIndex = length();
        }
        if(beginIndex > endIndex)
        {
            beginIndex = endIndex;
        }
        byte[] buf = new byte[endIndex-beginIndex];
        if(endIndex > beginIndex)
        {
            System.arraycopy(buf, 0, bytes, beginIndex, endIndex-beginIndex);
        }

        return new Stream(buf);
    }

    /**
     * ��ȡ�ֽ�
     * @param offset λ��ֵ
     * @return ��λ��ֵ�������鳤��ʱ����0����֮���ظ�λ�õ��ֽ�
     */
    public byte getByte(int[] offset)
    {
        if(offset[0] >= length())
        {
            offset[0] += 1;
            return 0;
        }

        byte val = bytes[offset[0]];

        offset[0] += 1;
        return val;
    }

    /**
     * ���ֽڼ���ԭ�ֽ�������
     * @param offset λ��ֵ
     * @param val ����ֵ
     */
    public void setByte(int[] offset, byte val)
    {
        if(offset[0] >= length())
        {
            offset[0] += 1;
            return;
        }

        bytes[offset[0]] = val;
        offset[0] += 1;
    }

    /**
     * ��ȡ������ֵ
     * @param offset λ��ֵ
     * @return  ��λ��ֵ��1�����ֽ����鳤�ȣ�����0����֮����ת����Ķ�����ֵ
     */
    public short getShort(int[] offset)
    {
        if(offset[0]+1 >= length())
        {
            offset[0] += 2;
            return 0;
        }

        short val = Short.reverseBytes(ByteBuffer.wrap(bytes).getShort(offset[0]));

        offset[0] += 2;
        return val;
    }

    /**
     * ��������ֵ�����ֽ�������
     * @param offset λ��ֵ
     * @param val ����ֵ
     */
    public void setShort(int[] offset, short val)
    {
        if(offset[0]+1 >= length())
        {
            offset[0] += 2;
            return;
        }

        ByteBuffer.wrap(bytes).putShort(offset[0], Short.reverseBytes(val));
        offset[0] += 2;
    }

    /**
     * ��ȡ����ֵ
     * @param offset λ��ֵ
     * @return  ��λ��ֵ��3�����ֽ����鳤�ȣ�����0����֮����ת���������ֵ
     */
    public int getInteger(int[] offset)
    {
        if(offset[0]+3 >= length())
        {
            offset[0] += 4;
            return 0;
        }

        int val = Integer.reverseBytes(ByteBuffer.wrap(bytes).getInt(offset[0]));

        offset[0] += 4;
        return val;
    }

    /**
     * ������ֵ�����ֽ�������
     * @param offset λ��ֵ
     * @param val ����ֵ
     */
    public void setInteger(int[] offset, int val)
    {
        if(offset[0]+3 >= length())
        {
            offset[0] += 4;
            return;
        }

        ByteBuffer.wrap(bytes).putInt(offset[0], Integer.reverseBytes(val));
        offset[0] += 4;
    }

    /**
     * ��ȡ������ֵ
     * @param offset λ��ֵ
     * @return  ��λ��ֵ��7�����ֽ����鳤�ȣ�����0����֮����ת����ĳ�����ֵ
     */
    public long getLong(int[] offset)
    {
        if(offset[0]+7 >= length())
        {
            offset[0] += 8;
            return 0;
        }

        long val = Long.reverseBytes(ByteBuffer.wrap(bytes).getLong(offset[0]));

        offset[0] += 8;
        return val;
    }

    /**
     * ��������ֵ�����ֽ�������
     * @param offset λ��ֵ
     * @param val ����ֵ
     */
    public void setLong(int[] offset, long val)
    {
        if(offset[0]+7 >= length())
        {
            offset[0] += 8;
            return;
        }

        ByteBuffer.wrap(bytes).putLong(offset[0], Long.reverseBytes(val));
        offset[0] += 8;
    }

    /**
     * ��ȡ������ֵ
     * @param offset λ��ֵ
     * @return  ��λ��ֵ��3�����ֽ����鳤�ȣ�����0����֮����ת����ĸ�����ֵ
     */
    public float getFloat(int[] offset)
    {
        if(offset[0]+3 >= length())
        {
            offset[0] += 4;
            return 0;
        }

        float val = Float.intBitsToFloat(Integer.reverseBytes(ByteBuffer.wrap(bytes).getInt(offset[0])));

        offset[0] += 4;
        return val;
    }

    /**
     * ��������ֵ�����ֽ�������
     * @param offset λ��ֵ
     * @param val ����ֵ
     */
    public void setFloat(int[] offset, float val)
    {
        if(offset[0]+3 >= length())
        {
            offset[0] += 4;
            return;
        }

        ByteBuffer.wrap(bytes).putInt(offset[0], Integer.reverseBytes(Float.floatToIntBits(val)));
        offset[0] += 4;
    }

    /**
     * ��ȡ˫������ֵ
     * @param offset λ��ֵ
     * @return  ��λ��ֵ��7�����ֽ����鳤�ȣ�����0����֮����ת����ĸ�����ֵ
     */
    public double getDouble(int[] offset)
    {
        if(offset[0]+7 >= length())
        {
            offset[0] += 8;
            return 0;
        }

        double val = Double.longBitsToDouble(Long.reverseBytes(ByteBuffer.wrap(bytes).getLong(offset[0])));

        offset[0] += 8;
        return val;
    }

    /**
     * ��˫������ֵ�����ֽ�������
     * @param offset λ��ֵ
     * @param val ����ֵ
     */
    public void setDouble(int[] offset, double val)
    {
        if(offset[0]+7 >= length())
        {
            offset[0] += 8;
            return;
        }

        ByteBuffer.wrap(bytes).putLong(offset[0], Long.reverseBytes(Double.doubleToLongBits(val)));
        offset[0] += 8;
    }

    /**
     * ��ȡ�ַ�
     * @param offset λ��ֵ
     * @return  �ַ�
     */
    public String getString(int[] offset)
    {
        int length = getShort(offset) & 0x7FFF;
        byte[] buf = new byte[length];

        if(offset[0]+length < length())
        {
            System.arraycopy(bytes, offset[0], buf, 0, length);
        }
        else if(offset[0] < length())
        {
            System.arraycopy(bytes, offset[0], buf, 0, length()-offset[0]);
        }

        offset[0] += length;
        return Stream.setBytes(buf);
    }

    /**
     * ���ַ�����ֽ�������
     * @param offset λ��ֵ
     * @param val ����ֵ
     */
    public void setString(int[] offset, String val)
    {
        byte[] buf = Stream.getBytes(val);
        setShort(offset, (short)buf.length);

        if(offset[0]+buf.length < length())
        {
            System.arraycopy(buf, 0, bytes, offset[0], buf.length);
        }
        else if(offset[0] < length())
        {
            System.arraycopy(buf, 0, bytes, offset[0], length()-offset[0]);
        }

        offset[0] += buf.length;
    }

    /**
     * ��ȡ��
     * @param offset λ��ֵ
     * @return �����µ���
     */
    public Stream getStream(int[] offset)
    {
        int length = getInteger(offset);
        byte[] buf = new byte[length];

        if(offset[0]+length < length())
        {
            System.arraycopy(bytes, offset[0], buf, 0, length);
        }
        else if(offset[0] < length())
        {
            System.arraycopy(bytes, offset[0], buf, 0, length()-offset[0]);
        }

        offset[0] += length;
        return new Stream(buf);
    }

    /**
     * ���������ֽ�������
     * @param offset λ��ֵ
     * @param val ����ֵ
     */
    public void setStream(int[] offset, Stream val)
    {
        byte[] buf = val.getBytes();
        setInteger(offset, val.length());

        if(offset[0]+val.length() < length())
        {
            System.arraycopy(buf, 0, bytes, offset[0], val.length());
        }
        else if(offset[0] < length())
        {
            System.arraycopy(buf, 0, bytes, offset[0], length()-offset[0]);
        }

        offset[0] += buf.length;
    }

    /**
     * ��ȡ�ַ�
     * @param offset λ��ֵ
     * @return  �ַ�
     */
    public String getBuffer(int[] offset, int length)
    {
        byte[] buf = new byte[length];

        if(offset[0]+length < length())
        {
            System.arraycopy(bytes, offset[0], buf, 0, length);
        }
        else if(offset[0] < length())
        {
            System.arraycopy(bytes, offset[0], buf, 0, length()-offset[0]);
        }

        offset[0] += length;
        return Stream.setBytes(buf);
    }

    /**
     * ���ַ�����ֽ�������
     * @param offset λ��ֵ
     * @param val ����ֵ
     */
    public void setBuffer(int[] offset, String val)
    {
        byte[] buf = Stream.getBytes(val);

        if(offset[0]+buf.length < length())
        {
            System.arraycopy(buf, 0, bytes, offset[0], buf.length);
        }
        else if(offset[0] < length())
        {
            System.arraycopy(buf, 0, bytes, offset[0], length()-offset[0]);
        }

        offset[0] += buf.length;
    }
}
