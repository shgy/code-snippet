1. Sigar 移除
es不再使用sigar统计操作系统信息, 而是依赖JVM.
`network.*` 被移除
`fs.*.dev` 和 `fs.*.disk*` 被移除
`os.*` 被移除
`os.mem.total` 和 `os.swap.total` 被移除
`process.mem.resident` 和 `process.mem.share` 被移除。

这些移除，感觉移除了底层的系统依赖， 更依赖于JVM了。


