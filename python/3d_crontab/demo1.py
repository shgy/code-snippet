# -*- coding: utf-8 -*-
"""
python-crontab 包用于 管理 crontab 任务

既然是管理, 就涉及到 增删查改



"""
import logging
log = logging.getLogger("crontab")

from crontab import CronTab
cron = CronTab(user='shgy', log=log)
# job = cron.new(command='/bin/echo "helslo" >> /home/shgy/test.dat', comment="aabb")
# job.minute.every(2)
# # cron.write("test.crontab")
# # cron.remove_all()
# #
# cron.write()
for result in cron.run_scheduler():
    print "This was printed to stdout by the process."
