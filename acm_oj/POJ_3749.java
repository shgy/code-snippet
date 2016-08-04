package onlinejudge.easy;

import java.util.Scanner;

/**
 * @author xh
 * 破译密码
 * 简单的字符串处理
 */
public class POJ_3749 {
	private static boolean isArcii(char ch){
		if((ch>='a'&&ch<='z')||(ch>='A'&&ch<='Z')){
			return true;
		}
		return false;
	}
	
	private static char back5(char ch){
		char upper = Character.toUpperCase(ch);
		if(upper<'F'){
			int d = 'F'-ch;
			return (char) ('Z'-d+1);
		}
		return (char) (upper-5);
	}
	public static void main(String[] args) {
		Scanner  s = new Scanner(System.in);
		while(true){
			String line =s.nextLine();
			if(line.equals("ENDOFINPUT"))break;
			else if(line.equals("START")){
				String crypt = s.nextLine();
				char[] ch = new char[crypt.length()];
				for(int i=0,len = crypt.length();i<len;i++){
					char cur = crypt.charAt(i);
					if(isArcii(cur)){
						ch[i] = back5(cur);
					}else{
						ch[i] = cur;
					}
				}
				System.out.println(String.valueOf(ch));
				s.nextLine();//END
			}
		}
	}
}
