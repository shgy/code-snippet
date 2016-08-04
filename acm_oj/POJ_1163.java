package onlinejudge.dp;

import java.util.Scanner;

/**
 * @author shuaiguangying
 * 
 * @Title The Triangle
 * @Content
 *   简单的dp
 */
public class POJ_1163 {
	private static int solve(int[][] triangle){
		int[][] max=new int[triangle.length][triangle[0].length];
		for(int i=max.length-1;i>=0;i--){
			for(int j=0;j<=i;j++){
				if(i==max.length-1){
					max[i][j] = triangle[i][j];
				}else{
					max[i][j] = Math.max(max[i+1][j], max[i+1][j+1]) + triangle[i][j];
				}
			}
		}
		return max[0][0];
	}
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		while(s.hasNext()){
			int n = Integer.parseInt(s.nextLine());
			int[][] triangle = new int[n][n]; 
			for(int i=0;i<n;i++){
				String line = s.nextLine();
				String[] d = line.split("\\s+");
				for (int j=0;j<d.length;j++) {
					triangle[i][j] = Integer.parseInt(d[j]);
				}
			}
			int rt = solve(triangle);
			System.out.println(rt);
		}
		s.close();
	}
}
