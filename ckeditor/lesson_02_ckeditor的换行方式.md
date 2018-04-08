
在ckeditor中编辑时, 换行的默认行为是加p标签. 但是那不是我们需要的.

```
CKEDITOR.config.enterMode = CKEDITOR.ENTER_BR;
```
修改enterMode即可.


There is a file contents.css in the ckeditor library with the class .cke_editable with the line-height property set to 1.6. Update it with the value required. This will update the line-height in all the places its used in your application.

This is what i did.

.cke_editable
{
    font-size: 13px;
    line-height: 1.0;
}

不得不说, stackoverflow太赞了.
