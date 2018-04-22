````

package com.shgy.hbase;



import java.security.MessageDigest;



public class Hashlib {

	public static char hexDigitsUpper[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       

	public static char hexDigitsLower[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

	

	  public static String getHash(String str, String hashType)    

	            throws Exception {    

	        MessageDigest md5 = MessageDigest.getInstance(hashType);    

	        md5.update(str.getBytes());

	        return toHexString(md5.digest());    

	    }    

	    

	    public static String toHexString(byte[] b) {    

	        StringBuilder sb = new StringBuilder(b.length * 2);    

	        for (int i = 0; i < b.length; i++) {    

	            sb.append(hexDigitsLower[(b[i] & 0xf0) >>> 4]);    

	            sb.append(hexDigitsLower[b[i] & 0x0f]);    

	        }    

	        return sb.toString();    

	    }     

	    public static void main(String[] args) throws Exception {

			System.out.println(Hashlib.getHash("123", "md5"));

		}

}

```




