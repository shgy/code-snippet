package onlinejudge.greed;

import java.util.Scanner;

/**
 * @author shuaiguangying 
 *  Gone Fishing
 * 钓鱼问题：贪心策略
 *  每次选择 剩下鱼最多的湖
测试数据
2
1
0 0
1 1
1
答案
60 0 
0
 */
public class POJ_1042 {
	// 前n-1个湖中 上钩率最高的湖
	private static int maxFish(int[] left, int n) {
		int maxIdx = 0;
		int max = left[0];
		for(int i=1;i<n;i++){
			if(max<left[i]){
				maxIdx = i;
				max = left[i];
			}
		}
		return maxIdx;
	}

	// core
	private static int solve(int n, int h, int[] fi, int[] di, int[] ti,
			int[] rt) {
		int maxNum = -1; 
		for (int i = 0; i < n; i++) {
			int timeOnRoad = 0;
			for (int j = 0; j < i; j++) {
				timeOnRoad += ti[j];
			}
			int leftTime = h - timeOnRoad;
			// 每次挑选上钩率最高的湖
			int[] left = new int[i+1];
			System.arraycopy(fi, 0, left, 0, i+1);
			int[] timeEachLake = new int[n];
			int fishNum = 0;
			while (leftTime > 0 ) {
				int lakeNum = maxFish(left, i + 1);
				if(left[lakeNum]>0 ||timeEachLake[lakeNum]==0 ){
					int cur = left[lakeNum];
					fishNum += cur; // 钓到鱼的总数
					left[lakeNum]-= di[lakeNum];
					
					if(cur<=di[lakeNum])left[lakeNum]=0;
				}
				timeEachLake[lakeNum]++;
				leftTime--;
			}
			if (fishNum > maxNum ) {
				maxNum = fishNum;
				System.arraycopy(timeEachLake, 0, rt, 0, n);
			}
		}

		return maxNum;
	}

	// input
	private static int[] decompose(String line, int len) {
		int[] rt = new int[len];
		String[] ds = line.split("\\s+");
		for (int i = 0; i < ds.length; i++) {
			rt[i] = Integer.parseInt(ds[i]);
		}
		return rt;
	}

	private static void print(int[] rt, int total) {
		System.out.print(rt[0] * 5);
		for (int i = 1; i < rt.length; i++) {
			System.out.print(", " + (rt[i] * 5));
		}
		System.out.println("");
		System.out.println("Number of fish expected: " + total + "");
		System.out.println();
	}

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		while (true) {
			// input
			String line = s.nextLine();
			int n = Integer.parseInt(line);
			if (n == 0)
				break;
			line = s.nextLine();
			int h = Integer.parseInt(line) * 12;// 一个小时有12个时间段
			line = s.nextLine();
			int[] fi = decompose(line, n);
			line = s.nextLine();
			int[] di = decompose(line, n);
			line = s.nextLine();
			int[] ti = decompose(line, n - 1);
			int[] rt = new int[n];
			// solve
			int total = solve(n, h, fi, di, ti, rt);
			// print
			print(rt, total);
		}
		s.close();
	}
}
