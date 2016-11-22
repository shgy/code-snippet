struct node{
   struct node *prev;
   struct node *next;
   int id;
};


struct list{
   struct node head;
   struct node tail;
};


void list_init(struct list*);

void list_append(struct list*, struct node*);

void list_remove(struct list*, struct node*);



