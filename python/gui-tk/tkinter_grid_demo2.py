# -*- coding: utf-8 -*-
from Tkinter import *

master = Tk()
var = IntVar()

Label(master, text="First").grid(sticky=E)
Label(master, text="Second").grid(sticky=E)

e1 = Entry(master)
e2 = Entry(master)

e1.grid(row=0, column=1)
e2.grid(row=1, column=1)

checkbutton = Checkbutton(master, text='Preserve aspect', variable=var)
checkbutton.grid(columnspan=2, sticky=W)

photo = PhotoImage(file='qq.png')
label = Label(image=photo)
label.image = photo
label.grid(row=0, column=2, columnspan=2, rowspan=2, sticky=W+E+N+S, padx=5, pady=5)

button1 = Button(master, text='Zoom in')
button1.grid(row=2, column=2)

button2 = Button(master, text='Zoom out')
button2.grid(row=2, column=3)

mainloop()