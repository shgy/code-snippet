# -*- coding: utf-8
import Tkinter as tk

root = tk.Tk()
root.attributes('-alpha', 0.0) #For icon
#root.lower()
root.iconify()
window = tk.Toplevel(root)
window.geometry("200x200") #Whatever size
# window.overrideredirect(10) #Remove border
#window.attributes('-topmost', 1)
#Whatever buttons, etc
# close = tk.Button(window, text = "Close Window", command = lambda: root.destroy())
# close.pack(fill = tk.BOTH, expand = 1)
window.mainloop()