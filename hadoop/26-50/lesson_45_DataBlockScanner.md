看hdfs的TestDatanodeBlockScanner类, 感觉不甚理解.
```
localhost:50075/blockScannerReport?listblocks


Block report for block pool: BP-1754643252-127.0.1.1-1481693749184
blk_1073741834_1010        : status : ok     type : local  scan time : 88592617        1970-01-02 08:36:32,617
blk_1073741842_1018        : status : ok     type : local  scan time : 88592619        1970-01-02 08:36:32,619
blk_1073741849_1025        : status : ok     type : local  scan time : 88592619        1970-01-02 08:36:32,619
blk_1073741841_1017        : status : ok     type : local  scan time : 88592620        1970-01-02 08:36:32,620
blk_1073741852_1028        : status : ok     type : local  scan time : 88592620        1970-01-02 08:36:32,620
blk_1073741853_1029        : status : ok     type : local  scan time : 88592620        1970-01-02 08:36:32,620
blk_1073741827_1003        : status : ok     type : local  scan time : 88592621        1970-01-02 08:36:32,621
blk_1073741829_1005        : status : ok     type : local  scan time : 88592621        1970-01-02 08:36:32,621
blk_1073741839_1015        : status : ok     type : local  scan time : 88592621        1970-01-02 08:36:32,621
blk_1073741831_1007        : status : ok     type : local  scan time : 88592622        1970-01-02 08:36:32,622
blk_1073741836_1012        : status : ok     type : local  scan time : 88592622        1970-01-02 08:36:32,622
blk_1073741837_1013        : status : ok     type : local  scan time : 88592622        1970-01-02 08:36:32,622
blk_1073741844_1020        : status : ok     type : local  scan time : 88592623        1970-01-02 08:36:32,623
blk_1073741848_1024        : status : ok     type : local  scan time : 88592623        1970-01-02 08:36:32,623
blk_1073741838_1014        : status : ok     type : local  scan time : 88592624        1970-01-02 08:36:32,624
blk_1073741840_1016        : status : ok     type : local  scan time : 88592624        1970-01-02 08:36:32,624
blk_1073741846_1022        : status : ok     type : local  scan time : 88592624        1970-01-02 08:36:32,624
blk_1073741826_1002        : status : ok     type : local  scan time : 88592625        1970-01-02 08:36:32,625
blk_1073741845_1021        : status : ok     type : local  scan time : 88592625        1970-01-02 08:36:32,625
blk_1073741850_1026        : status : ok     type : local  scan time : 88592625        1970-01-02 08:36:32,625
blk_1073741830_1006        : status : ok     type : local  scan time : 88592626        1970-01-02 08:36:32,626
blk_1073741843_1019        : status : ok     type : local  scan time : 88592626        1970-01-02 08:36:32,626
blk_1073741828_1004        : status : ok     type : local  scan time : 88592627        1970-01-02 08:36:32,627
blk_1073741832_1008        : status : ok     type : local  scan time : 88592627        1970-01-02 08:36:32,627
blk_1073741835_1011        : status : ok     type : local  scan time : 88592627        1970-01-02 08:36:32,627
blk_1073741847_1023        : status : ok     type : local  scan time : 88592627        1970-01-02 08:36:32,627
blk_1073741825_1001        : status : ok     type : local  scan time : 88592628        1970-01-02 08:36:32,628
blk_1073741833_1009        : status : ok     type : local  scan time : 88592628        1970-01-02 08:36:32,628
blk_1073741851_1027        : status : ok     type : local  scan time : 88592629        1970-01-02 08:36:32,629
blk_1073741854_1030        : status : ok     type : local  scan time : 88592629        1970-01-02 08:36:32,629
blk_1073741863_1039        : status : ok     type : local  scan time : 88682826        1970-01-02 08:38:02,826
blk_1073741861_1037        : status : ok     type : local  scan time : 88682827        1970-01-02 08:38:02,827
blk_1073741862_1038        : status : ok     type : local  scan time : 88682830        1970-01-02 08:38:02,830
blk_1073741872_1048        : status : ok     type : local  scan time : 88702865        1970-01-02 08:38:22,865
blk_1073741869_1045        : status : ok     type : local  scan time : 88702866        1970-01-02 08:38:22,866
blk_1073741871_1047        : status : ok     type : local  scan time : 88702866        1970-01-02 08:38:22,866
blk_1073741870_1046        : status : ok     type : local  scan time : 88702867        1970-01-02 08:38:22,867
blk_1073741881_1057        : status : ok     type : local  scan time : 91373001        1970-01-02 09:22:53,001
blk_1073741879_1055        : status : ok     type : local  scan time : 91373007        1970-01-02 09:22:53,007
blk_1073741880_1056        : status : ok     type : local  scan time : 91373007        1970-01-02 09:22:53,007

Total Blocks                 :     40
Verified in last hour        :      0
Verified in last day         :      0
Verified in last week        :      0
Verified in last four weeks  :     40
Verified in SCAN_PERIOD      :      0
Not yet verified             :      0
Verified since restart       :     53
Scans since restart          :     53
Scan errors since restart    :      0
Transient scan errors        :      0
Current scan rate limit KBps :   1024
Progress this period         :    100%
Time left in cur period      :  66.76%

```

参考:
http://www.cnblogs.com/richard1023/p/4966878.html