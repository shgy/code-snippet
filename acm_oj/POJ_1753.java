package onlinejudge.easy;
import java.util.Scanner;
 //这道题也可以用高斯消元法来解决
 
/**
 * Flip Game
 * 这个题目在枚举上也有一点小的技巧。如果要是暴力枚举的话，
 * 一旦棋盘到了16*16的话显然就吃不消了（一个东欧区域赛的题目）
 * ，实际上我们完全可以只枚举第一行的操作，之后，如果我们想把棋子
 * 全部翻成一种颜色的话，那么第二行的操作就是固定的了（因为第一行的
 * 棋子的状态对第二行棋子的翻转进行了约束，如果想把第一行的棋子变成
 * 白色，那么第二行中位于第一行黑色棋子下方的位置必须翻转，反之亦然），
 * 那么第三行、第四行的操作显然也是固定的了。
*/
public class POJ_1753 {
    static int steps=0x7fffffff;
    //(x,y)坐标合起来就是中心点及上下左右坐标啦！
    static int[] dx={0,0,0,1,-1};
    static int[] dy={0,1,-1,0,0};
     
    /*
     * 把st以2进制表示，每四个的排，排四列，从右下为(0,0)左上为（3,3）
     * @param x竖坐标点
     * @param y横坐标点
     * @param st需要处理的数
     * @return 改变确定位置的状态，如1变成0或者0变成1
     * */
    public static int flip(int x, int y, int source){
        if(x >= 0 && x < 4 && y >= 0 && y < 4)
            source ^= 1 << (x * 4 + y);
        return source;
    }
    /*
     * @param current当前行
     * @param num 回合数
     * @param source 原数据
     * @param flag 标志 如果数据源当前位的状态不为flag，则翻动
     * */
    public static void dfs(int current,int num,int source,int flag){
        //如果最后一行已经翻完
        if(current==4){
            if(source==0xffff||source==0){
                //已经完成了任务
                steps=num<steps?num:steps;
            }
            return;
        }
        //把当前行都翻成同种颜色
        int x,y;
        for (int i = current-1,j=0; j < 4; j++) {//每行有四个，所以需要翻四次
            if( (((source& (1<< (i*4+j) ))>>(i*4+j)) ^ flag)==1 ){
                /*source& (1<< (i*4+j) )>>(i*4+j) :把source中的(i,j)的状态取出来*/
                for (int k = 0; k <5; k++) {//当前，上下左右都得翻动
                    x=current+dx[k];
                    y=j+dy[k];
                    source=flip(x, y, source);
                }
                num++;
            }
        }
        //翻下一行
        dfs(current+1, num, source, flag);
         
    }
    /*
     * */
    public static int solve(int source){
        for (int i = 0; i < 16; i++) {
            int num=0,temp=source,x,y;
            for (int j = 0; j < 4; j++) {
                if((i&(1<<j))>0){
                    for (int k = 0; k <5; k++) {//当前，上下左右都得翻动
                        x=0+dx[k];
                        y=j+dy[k];
                        temp=flip(x, y, temp);
                    }
                    num++;
                }
            }
            dfs(1, num, temp, 0);
             
            dfs(1, num, temp, 1);
        }
        return steps==0x7fffffff?-1:steps;
    }
    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);
        int source=0;
        String string="";
        for (int i = 0; i < 4; i++) {
            string+=scanner.nextLine().trim();
        }
        for (int i = 0; i < string.length(); i++) {
                source=(source<<1)+(string.substring(i, i+1).equals("b")?1:0);
        }
 
        if(solve(source)!=-1){
            System.out.println(steps);
        }else {
            System.out.println("Impossible");
 
        }
        scanner.close();
    }
}  