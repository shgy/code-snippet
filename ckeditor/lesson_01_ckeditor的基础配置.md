ckeditor的配置不复杂， 到了4.7以后， 可以直接订制下载。 如下： https://ckeditor.com/cke4/builder。

这里主要贴一下配置：
```
function init_ckeditor() {

    CKEDITOR.config.height = 550;
    CKEDITOR.config.width = 'auto';

    CKEDITOR.config.toolbar = 'Full';
    CKEDITOR.config.toolbar_Full = [
{ name: 'document', items : [ 'Source','-','Save','NewPage','DocProps','Preview','Print','-','Templates' ] },
{ name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
{ name: 'editing', items : [ 'Find','Replace','-','SelectAll','-', 'Scayt' ] }, //'SpellChecker',
{ name: 'forms', items : [ 'Form', 'Checkbox', 'Radio', 'TextField', 'Textarea', 'Select', 'Button', 'ImageButton',
'HiddenField' ] },
'/',
{ name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ] },
{ name: 'paragraph', items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','CreateDiv',
'-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ] },
{ name: 'links', items : [ 'Link','Unlink','Anchor' ] },
{ name: 'insert', items : ['Table','HorizontalRule','Smiley','SpecialChar','PageBreak','Iframe' ] }, // 'Image','Flash',
'/',
{ name: 'styles', items : [ 'Styles','Format','Font','FontSize' ] },
{ name: 'colors', items : [ 'TextColor','BGColor' ] },
{ name: 'tools', items : [ 'Maximize', 'ShowBlocks','pbckcode','-','About' ] }
];


  // PBCKCODE CUSTOMIZATION
  CKEDITOR.config.pbckcode = {
    // An optional class to your pre tag.
    cls: '',

    // The syntax highlighter you will use in the output view
    highlighter: 'PRETTIFY',

    // An array of the available modes for you plugin.
    // The key corresponds to the string shown in the select tag.
    // The value correspond to the loaded file for ACE Editor.
    modes: [['SQL'          , 'sql'],['Python'       , 'python']
            ],

    // The theme of the ACE Editor of the plugin.
    theme: 'textmate',

    // Tab indentation (in spaces)
    tab_size: '4'
  };
    CKEDITOR.replace('tab_desc');
}

```
