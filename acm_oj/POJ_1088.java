package onlinejudge.dp;

import java.util.Scanner;

/**
 * @author shuaiguangying
 * POJ1088 滑雪
 */
public class Main {
	private static int solve(int[][] height,int r,int c){
		for(int i=0;i<r;i++){
			for(int j=0;j<c;j++){
				
			}
		}
		return 0;
	}
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		while(s.hasNext()){
			int r = s.nextInt();
			int c = s.nextInt();
			int[][] height = new int[r][c];
			for(int i=0;i<r;i++){
				for(int j=0;j<c;j++){
					height[i][j]=s.nextInt();
				}
			}
			int ans = solve(height,r,c);
			System.out.println(ans);
		}
	}
}
