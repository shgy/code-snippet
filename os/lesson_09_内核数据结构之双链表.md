Linux内核在进程管理中用到了双链表. 而且双链表的实现方式与通常的实现方式不一样.
通常数据是内置在链表的节点中, 而Linux内核的实现, 数据却是外挂.
这样的实现方式会让代码逻辑清晰很多.
```
#include<stdio.h>
//#include"list.h"

void list_init(struct list *list){
/*
 初始化一个空双向链表:
 注意: 双向链接不是循环链表

       NN --> NN --> NN ---> END
END<-- NN <-- NN <-- NN

*/
 list->head.id=0;
 list->tail.id=-1;
 list->head.prev = NULL;
 list->head.next = &list->tail;
 list->tail.prev = &list->head;
 list->tail.next = NULL;
}


void list_append(struct list *list, struct node *node){
/*
  在链表的head和tail之间追加一个结点
 */
   struct node *insp;
   insp = list->tail.prev;

   node->prev = &list->head;
   node->next = &list->tail;
   insp->next = node;
   list->tail.prev = node;

}

struct pcb_data{
   int p_id;
   struct node mpoint;
};

int main(int argc, char *argv[])
{

    struct list mylist;
    struct pcb_data p1, p2;
    struct node *curr;

    list_init(&mylist);
     p1.mpoint.id=1;
     p1.p_id=10;
    list_append(&mylist, &p1.mpoint);

    for(curr= &mylist.head; curr!=NULL; curr=curr->next)
    {
      printf("node.id is %d\n",curr->id);
      if(curr->id==1){
         struct pcb_data *p;
         curr =(struct node*) ((long *)curr - 1);
         p = (struct pcb_data *)curr;
         printf("pcb.p_id is %d\n",p->p_id);
         curr =(struct node*) ((long *)curr + 1);
      }
    }
     for(curr= &mylist.tail; curr!=NULL; curr=curr->prev)
    {
      printf("node.id is %d\n",curr->id);
    }


    return 0;
}
```
例如上面的代码: pcb_data就是外挂在链表中, 如何从链表中拿到pcb_data呢?
通过位置的偏移,我们知道`mpoint`在结构体中偏移了4个字节
```
struct pcb_data{
   int p_id;
   struct node mpoint;
};
```
因此, 如下的代码即可定位到pcb_data的开始位置
```
struct pcb_data *p;
         p =(struct pcb_data *) ((long *)curr - 1);
         printf("pcb.p_id is %d\n",p->p_id);

```
