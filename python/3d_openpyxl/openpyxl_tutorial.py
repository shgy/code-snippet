# -*- coding: utf-8 -*-
"""
通常会使用xlrd和xlwt进行excel的读写, 但是这两个库貌似没法修改excel文件, 因此使用了openpyxl库.
常见的功能无外乎是: 打开, 读取, 写入
"""
import openpyxl  # 2.4.0
from copy import copy

# 打开一个excel文件
filename = 'demo.xlsx'
workbook = openpyxl.load_workbook(filename)  # 打开xls文件

# 遍历excel中的sheet
for s_name in workbook.get_sheet_names():
    worksheet = workbook.get_sheet_by_name(s_name)

# 遍历sheet中的cell
    for row in worksheet.iter_rows(min_row=4, min_col=2, max_col=2, max_row=4):
        for cell in row:
            print cell.value

# 为cell赋值
    worksheet['A10'] = 'Hello'

# 复制样式: 注意 复制样式的操作和赋值的操作, 顺序必须是先 copy_style, 然后再赋值


def copy_style(w1, w2):
    w1.font = copy(w2.font)
    w1.fill = copy(w2.fill)
    w1.border = copy(w2.border)
    w1.alignment = copy(w2.alignment)
    w1.number_format = copy(w2.number_format)
    w1.protection = copy(w2.protection)

# 另存文件
workbook.save(filename="%s.bak" % filename)