 需求: 对API访问有次数限制,一旦超过限制次数, 则不能再访问.在django中, 代码如下:

```

    @staticmethod

    def deduct_visit(id):

        # sql = 'update `api_dayvisit` set `dayused`=`dayused` + 1, `dayleft`=`dayleft`-1 where `dvid`=%d;' % dvid

        with transaction.atomic():

            apiorder = ApiOrder.objects.select_for_update().get(id=id)

            if apiorder.limit_total:

                if apiorder.left > 0:

                    apiorder.used += 1

                    apiorder.left -= 1

                else:

                    raise RequestTimesLimitException(": (total)")



            if apiorder.dayleft > 0:

                apiorder.dayused += 1

                apiorder.dayleft -= 1



                apiorder.save()

            else:

                raise RequestTimesLimitException(": (day)")

```



关键点在于 `select_for_update` . 这里 ` transaction.atomic()` 没有起到作用.
