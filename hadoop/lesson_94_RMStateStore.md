RMStateStore记录了运行中应用程序的运行日志。 当集群故障重启后， ResourceManager可通过这些日志回复程序的运行状态， 从而避免全部重新运行。
这属于ResourceManager容错机制的范畴。

ResourceManager提供了4种 RMStateStore

NullRMStateStore

MemoryRMStateStore

FileSystemRMStateStore

ZKRMStateStore -- 真正的高可用