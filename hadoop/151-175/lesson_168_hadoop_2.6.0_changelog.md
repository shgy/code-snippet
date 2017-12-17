http://hadoop.apache.org/docs/r2.6.0/hadoop-project-dist/hadoop-common/releasenotes.html

YARN-2853
testKillFinishingApp

1. 一个正在运行的app 接收到kill事件后


YARN-2841  EOFException
  抛出异常时: 给出更清晰的异常类型. EOFException 是Java IOException的子类.
  

YARN-2834: RM重启后失败
   如果 app 更新token失败, app会转移到 FAILED 状态, 并且将app的最终状态保存. 但是RMAppAttempt
依然正常运行. 因此,在RM重启后, app由于处于失败状态,不会添加到调度器中, 但是 attempt会进行调度,所以
attempt会找不到app, 从而抛出NPE (Null Pointer Exception)
   
   如何修复呢? 这个涉及到RM的个各个组件的功能.  在
ApplicationMasterService 和 ApplicationMasterLauncher 两个组件中找找.

ApplicationMasterService用户 RM-AM交互, AM向RM证明自己活着, 领取生活费.
