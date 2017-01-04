# -*- coding: utf-8 -*-


if __name__ == '__main__':
    import os, commands
    print os.getenv("HIVE_OPTS")

    (status, output) = commands.getstatusoutput('hive -e "show databases;"')
    print status, output