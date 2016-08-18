/*
 功能: 设定对信息屏蔽集内信号的处理方式(阻塞或不阻塞)
 场景: 延迟响应信号.
 同一类信号发送多次只会响应一次.
 SIGKILL和SIGSTOP是不会被阻塞；也不会被修改默认的信号处理器.
 */

#include<stdio.h>
#include<string.h>
#include<errno.h>
#include<signal.h>

void sigHandler(int sig)
{
	printf("%d:%s\n",sig, strsignal(sig));
}

int main(int argc, char *argv[])
{
 sigset_t set;
 struct sigaction act;

 sigemptyset(&act.sa_mask);
 act.sa_flags = 0;
 act.sa_handler=sigHandler;

 sigaction(SIGINT, &act, NULL);

 sigemptyset(&set);
 sigaddset(&set, SIGINT);

 // 阻塞SIGINT信号
 sigprocmask(SIG_BLOCK,&set, NULL);
 printf("begin sleep for 10 seconds\n");
 sleep(10);
 printf("wake up\n");
 //解除对SIGINT的阻塞
 sigprocmask(SIG_UNBLOCK, &set, NULL);

 pause();

 return 0;
}
