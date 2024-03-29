看《黑客与画家》讲到"防止垃圾邮件的一种方法"，觉得很适合用来表述数学公式与机器学习之间的关系。涉及到机器学习的数学公式比较简单，概率论基础教程都会讲到。解决的问题也很典型： 垃圾邮件的识别。


防止垃圾邮件有很多种方法，最直观的一种就是“规则”,  各种if-else的条件。这种方法能够解决一个问题，但是解决不了一类问题。而且，这个规则的制定需要非常熟悉业务，好在通常我们面临的业务问题是很垂直的， 通过规则， 也能够解决问题。毕竟解决问题才是业务的核心诉求。


接下来， 业务随着业务的发展， 规则越来越复杂， 我们维护起来也越来越吃力。 而且使用规则，是被动式的解决问题，用户体验也不好。 这个时候，新的方法该上场了，这个方法就是 “统计学方法”。 因为接触的规则越多， 我们会慢慢发现邮件中出现某个关键词， 只能表示邮件有可能是垃圾邮件。 这个可能性如何度量呢？  用贝叶斯方法。


贝叶斯方法的思路属于逆向思维。 通常概率论解决的问题是“已知邮件是垃圾邮件，问各个单词出现在垃圾邮件中的概率”， 贝叶斯方法解决的问题是“已知邮件内容， 问当前邮件属于垃圾邮件的概率".

理解贝叶斯公式不难，其基础点有"条件概率", "联合概率"。 贝叶斯公式的推导也很简单:

P(AB) = P(B)\*P(A|B)  
P(AB) = P(A)\*P(B|A)

有:

P(B)\*P(A|B)=P(A)\*P(B|A)

所以

P(A|B) = P(A)\*P(B|A) / P(B)


虽然机器学习最忌讳的就是套公式，但是为了方便理解， 我们先套个公式:

P(垃圾邮件|邮件内容) 表示 ”在已知邮件内容，邮件属于垃圾邮件的概率“

P(垃圾邮件|邮件内容) = P(垃圾邮件) * P(邮件内容|垃圾邮件) / P(邮件内容)

等式右边的概率是可以通过样本计算出来的。


现在解决问题的方法有了，数学公式也有了， 是不是问题就解决了呢？ 显然不是。我们只是完成了模型选择而已。通过《黑客与画家》看这个模型是如何落地的。


1. 选择样本： 作者选取了4000封正常邮件和4000封垃圾邮件。

2. 选择特征：字母、阿拉伯数字、破折号、撇号、美元符号作为“实义标识”

3. 统计次数: 计算了每个实义标识在两个邮件组出现的次数

4. 确定计算公式。 这里其实就是整篇文章的精华了。a. 作者没有完完全全套用贝叶斯公式; b. 作者分别在token和邮件两个维度用了贝叶斯思想。这才是难能可贵的。

5. 特征选择: 作者选取了top15的特征， 而非邮件全部的token.

6. 结果选取: 通常我们选取结果是以0.5为界，而作者以0.9为界。

如果说通常意义上的编程是一维的，那么机器学习的编程就是二维的。通常的工程问题是非黑即白，要么可用，要么是有Bug不可用。而机器学习在工程上的落地，更核心的关注点在于算法效果好不好和算法效果能不能更好。算法效果好不好，核心点在于数学模型， 其次在于怎么用好数学模型。 《黑客与画家》用简明的例子说明他是怎么用数学模型解决业务问题的。 

引申一下：这个问题属于典型的二分类问题。像垃圾邮件，垃圾评论， 评论的情感判断， 是否目标用户，是否推荐用户... 很多问题都可以归类到二分类问题。如果把"垃圾邮件的识别"抽象到分类问题，整个解决问题的思路就又开阔了很多。




  







































