package onlinejudge.bst;

import java.util.Random;
import java.util.Scanner;

/**
 * @author shuaiguangying
 * @Title Tunnel Warfare
 * @Content
 * 平衡树 基础题
 * 有技巧的地方在于：把删除操作 插入到树中，并且插入0和n+1,避免空节点出现的情况
 * 然后就是求前驱和后继的差值
 *
 * 平衡树求前驱和后继 的贪心算法需要了解
 */
public class POJ_2892 {
 static class Treap{
  static final Random r = new Random();
  public static final int Left = 0;
  public static final int Right = 1;
  Node root;
  //左旋
  private Node rotate_left(Node y){
   Node x = y.c[Right];
   y.c[Right] = x.c[Left];
   x.c[Left] = y;
   return x;
  }
  //右旋
  private Node rotate_right(Node x){
   Node y = x.c[Left];
   x.c[Left] = y.c[Right];
   y.c[Right] = x;
   return y;
  }
 
  public void insert(int key){
   root = insert(root,key);
  }private Node insert(Node node,int key){
   if(node == null){
    node = new Node(key);
    node.prio = r.nextInt();
   }else if(key > node.key){
    node.c[Right] = insert(node.c[Right],key);
    if(node.c[Right].prio < node.prio){
     node = rotate_left(node);
    }
   }else if(key<node.key){
    node.c[Left] = insert(node.c[Left],key);
    if(node.c[Left].prio < node.prio){
     node = rotate_right(node);
    }
   }else{
    node.cnt++;
   }
   return node;
  }
 
  public void delete(int key){
   delete(root,null,0,key);
  }private void delete(Node node,Node p,int direct,int key){
   if(node == null) return ;
   
   if(key > node.key){
    delete(node.c[Right],node,Right,key);
   
   }else if(key < node.key){
    delete(node.c[Left],node,Left,key);
   
   }else if(--node.cnt <= 0){
    if(node.c[Left]==null && node.c[Right]==null){//叶子节点
     if(p == null){
      root = null; //空树
     }else{
      p.c[direct]=null;
     }
    }else if(node.c[Left]!=null && node.c[Right]!=null){//有两个非空子节点
     if(node.c[Left].prio < node.c[Right].prio){
      node = rotate_right(node);
      if(p==null){
       root = node;
      }else{
       p.c[direct] = node;
      }
      delete(node.c[Right],node,Right,key);
     }else{
      node = rotate_left(node);
      if(p==null){
       root = node;
      }else{
       p.c[direct] = node;
      }
      delete(node.c[Left],node,Left,key);
     }
    }else{ //只有一个节点为空
     p.c[direct] = (node.c[Left]==null) ? node.c[Right]:node.c[Left];
    }
   }
  }
 
  //查找该数的前驱
  private Node pred(Node node,Node best,int q){
   if(node==null) return best;
   if(q<node.key ){
    return pred(node.c[Left], best , q);
   }else{
    return pred(node.c[Right], node , q);
   }
  }
  //查找该数的后继
  private Node succ(Node node,Node best,int q){
   if(node==null) return best;
   if(q <= node.key){
    return succ(node.c[Left], node,q);
   }else{
    return succ(node.c[Right],best,q);
   }
  }
 
  public int count(int q){
   Node pred = pred(root,null,q);
   Node succ = succ(root,null,q);
   return (succ.key - pred.key - 1) < 0 ? 0 : (succ.key - pred.key - 1);
  }
 
  class Node{
   int key;
   int prio;
   int cnt = 1;
   Node[] c = new Node[2];	//0表示左孩子，1表示右孩子
   public Node(int key){
    this.key = key;
   }
  }
 }
 static int[] stack = new int[ 50005];
 static int top =0;
 public static void main(String[] args) {
  Scanner s = new Scanner(System.in);
 //	while(s.hasNext()){
   Treap t = new Treap();
   t.insert(0);
   
   String line = s.nextLine();
   String[] ds = line.split("\\s+");
   int n = Integer.parseInt(ds[0]);
   int m = Integer.parseInt(ds[1]);
   t.insert(n+1);
   while(m-->0){
    line = s.nextLine();
    if(line.startsWith("D")){
     ds = line.split("\\s+");
     stack[top++] = Integer.parseInt(ds[1]);
     t.insert(stack[top-1]);
    }else if(line.startsWith("R")){
     t.delete(stack[--top]);
    }else if(line.startsWith("Q")){
     ds = line.split("\\s+");
     int q = Integer.parseInt(ds[1]);
     System.out.println(t.count(q));
    }
   }
  //}
  s.close();
//	 Treap t = new Treap();
//	 t.insert(0);
//	 t.insert(8);
//	 t.insert(3);
//	 t.insert(6);
//	 t.insert(5);
//	 System.out.println(t.count(4));
//	 System.out.println(t.count(5));
//	 t.delete(5);
//	 System.out.println(t.count(4));
//	 t.delete(6);
//	 System.out.println(t.count(4));
 }
}
