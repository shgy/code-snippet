#include<stdio.h>
#include"list.h"

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
         p = (struct pcb_data *)((long *)curr -1);
         printf("pcb.p_id is %d\n",p->p_id);
      }
    }
     for(curr= &mylist.tail; curr!=NULL; curr=curr->prev)
    {
      printf("node.id is %d\n",curr->id);
    }


    return 0;
}
