# -*- coding: utf-8 -*-
"""

最基础的日志配置

"""
import logging, traceback
logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s %(message)s',
                    datefmt='%a, %d %b %Y %H:%M:%S',
                    filename='errors.log',
                    filemode='a')

logging.info("hello")
try:
    raise Exception("Test Exception")
except Exception as e:
    logging.exception(traceback.format_exc())