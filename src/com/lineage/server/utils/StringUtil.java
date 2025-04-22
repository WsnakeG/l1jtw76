package com.lineage.server.utils;

public class StringUtil {
	private static final String[] _decodeChars={"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
	public static String decode(byte[] b){
		StringBuffer resultSBuffer=new StringBuffer();
		for(int i=0;i<b.length;i++){
			resultSBuffer.append(decode(b[i]));
		}
		return resultSBuffer.toString();
	}
	private static String decode(byte b){
		int n=b;
		if(n<0){
			n+=256;
		}
		int d1=n/16;
		int d2=n%16;
		return _decodeChars[d1]+_decodeChars[d2];
	}
	public static byte[] encode(String password) {
		int size=password.length();
		byte[] ret=new byte[size/2];
		byte[] tmp=password.getBytes();
		for(int i=0;i<size/2;i++){
			ret[i]=encode(tmp[i*2],tmp[i*2+1]);
		}
		return ret;
	}
	private static byte encode(byte src0,byte src1){
		char _b0 = (char)Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
	    
	    _b0 = (char)(_b0 << '\004');
	    char _b1 = (char)Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
	    
	    byte ret = (byte)(_b0 ^ _b1);
	    return ret;
	}
}
