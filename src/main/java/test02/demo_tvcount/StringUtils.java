package test02.demo_tvcount;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {

	/**
	 * 字符串加密算法
	 * @param strSrc 	被加密字符串
	 * @param algorithm	加密算法 MD5、SHA-1、SHA-256、SHA-384
	 * @return
	 */
	public static String encrypt(String strSrc, String algorithm) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance(algorithm);
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            System.out.println("签名失败！");
            return null;
        }
        return strDes;
    }
	
	
	/**
	 * bytes2Hex
	 * @param bts
	 * @return
	 * 
	 */
	public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }
	
	
	/**
	 * isEmpty
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str==null || str.length()==0;
	}
}
