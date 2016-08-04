package onlinejudge.math;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @author xh
 * Dirichlet's Theorem on Arithmetic Progressions
 * 打印素数表，或者简单的求素数
 */
public class POJ_3006 {
	static int max = 1000000;
	static boolean[] prime = new boolean[max];
	private static void printPrime(){
		Arrays.fill(prime, true);
		prime[0]=prime[1] = false;//1 不是素数
		for(int i=2;i<max;i++){
			if(prime[i]){
				for(int j=2*i;j<max;j=j+i){
					prime[j]= false;
				}
			}		
		}
	}
	
	public static void main(String[] args) {
		printPrime();
		Scanner s = new Scanner(System.in);
		while(true){
			String line = s.nextLine();
			String[] data = line.split("\\s+");
			int a = Integer.parseInt(data[0]);
			int d = Integer.parseInt(data[1]);
			int n = Integer.parseInt(data[2]);
			
			if(a==0&&d==0&&n==0)break;
			int count = 0;
			int val = a;
			while(true){
				if( prime[val]==true)count++;
				if(count == n)break;
				val=val + d;
			}
			System.out.println(val);
		}
	}
}
