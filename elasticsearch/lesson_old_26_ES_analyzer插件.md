分析器(analyzer)是搜索索引的重要组成部分，特别是对于中文分词。无论是用ES还是Solr做垂直搜索，配置特定的分词器是绕不过步骤。无论是Solr还是ES，都需要对分词器进行封装才能在系统中应用。下面简单对WhitespaceTokenizer进行ES封装。

	第一步，创建maven项目costomanalyzer。并配置好elasticsearch依赖包和maven-assembly-plugin插件。

	第二步，创建ES插件的入口类(AbstractPlugin)和注入绑定配置类(AbstractModule)，配置好es-plugin.properties文件。

	第三步，创建


