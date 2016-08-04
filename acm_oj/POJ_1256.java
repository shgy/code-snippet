package onlinejudge.math;

import java.util.Scanner;

/**
 * @author shuaiguangying Anagram 全排列问题 :需要注意 这里的字符的大小重新定义过了 所以需要重新自定义排序
 */
public class POJ_1256 {
	private static void sort(char[] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a.length - i - 1; j++) {
				if (cmp(a[j], a[j + 1]) > 0) {
					swap(a, j, j + 1);
				}
			}
		}
	}

	private static int cmp(char a, char b) {
		if (Character.isUpperCase(a) && Character.isUpperCase(b)) {
			return a - b;
		} else if (Character.isLowerCase(a) && Character.isLowerCase(b)) {
			return a - b;
		} else {// 注意 'A'<'a'<'B'<'b'
			int na = Character.toLowerCase(a);
			int nb = Character.toLowerCase(b);
			if (na - nb != 0)
				return na - nb;
			if (Character.isUpperCase(a))
				return -1;
			return 1;
		}
	}

	private static void swap(char[] d, int i, int j) {
		char tmp = d[i];
		d[i] = d[j];
		d[j] = tmp;
	}

	private static void reverse(char[] d, int i) {
		int j = d.length - 1;
		while (i < j) {
			swap(d, i, j);
			i++;j--;
		}
	}

	private static String next(String data) {
		char[] a = data.toCharArray();
		int i = a.length - 2;
		if (cmp(a[i], a[i + 1]) < 0) { // a[i]<a[i+1]
			swap(a, i, i + 1);
		} else {
			while (i >= 0 && cmp(a[i], a[i + 1]) >= 0)
				i--; // a[i]>=a[i+1]
			if (i < 0)
				return null;// 已经全部是递减序列，没有更大的排列了
			int j = i + 1;
			while (j < a.length && cmp(a[i], a[j]) < 0)
				j++; // a[i]<a[j]
			j = j - 1;
			swap(a, i, j);
			reverse(a, i + 1);
		}
		return String.valueOf(a);
	}

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		String line = s.nextLine();
		int n = Integer.parseInt(line);
		while (n-- > 0) {
			line = s.nextLine();
			if (line.length() < 2) {
				System.out.println(line);
				continue;
			}
			char[] a = line.toCharArray();
			sort(a);
			line = String.valueOf(a);
			System.out.println(line);
			while ((line = next(line)) != null) {
				System.out.println(line);
			}
		}
		s.close();
	}
}
