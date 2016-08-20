#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>
#include<limits.h>

#include<sys/inotify.h>
/*
 注意 inotify只能监控当前目录, 而无法监控子目录；如果需要监控子目录.
 则需要使用nftw()函数将所有的目录添加到inotify的监控项中；需要注意监控的目录数不要超出max_user_watches上限.
 */
#define BUF_LEN (10 * (sizeof(struct inotify_event) + NAME_MAX + 1 ))

int main(int argc, char *argv[])
{
 
 int fd;
 
 if( (fd= inotify_init()) == -1)
 {
 	perror("inotify_init");
	return 1;
 }
 if( inotify_add_watch(fd,"test", IN_ALL_EVENTS) == -1)
 {
   perror("inotify_add_watch");
   return 1;
 }
 
 int numRead;
 char *p;
 char buf[BUF_LEN];
 struct inotify_event *event;

 for(;;)
 {
   numRead = read(fd, buf, BUF_LEN); 
   if(numRead == 0)
   {
     printf("read() from inotify returned 0!");
	 return 1;
   }

   if(numRead == -1)
   {
     perror("read");
	 return 1;
   }

   for(p = buf; p< buf + numRead;)
   {
     event = (struct inotify_event *) p;
	 switch (event->mask) {
	  case IN_ACCESS:
	   printf("access\n");
	   break;
	  case IN_ATTRIB:
	   printf("attrib\n");
	   break;
	  case IN_CLOSE_WRITE:
	   printf("close write\n");
	   break;
	  case IN_CLOSE_NOWRITE:
	   printf("close nowrite\n");
	   break;
	  case IN_CREATE:
	   printf("create\n");
	   break;
	  case IN_DELETE:
	     printf("delete\n");
	   break;
	  default:
	    printf("event mask is %d\n",event->mask);
	   
	 }
	 p += sizeof(struct inotify_event) + event->len;
   }
 }


 return 0;
}
