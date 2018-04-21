1 下载https://github.com/Asquera/elasticsearch-http-basic/releases/download/v1.5.1/elasticsearch-http-basic-1.5.1.jar
2 将下载的文件放置到plugins/http-basic目录中
2 配置config/elasticsearch.yml文件如下：

#################################http-basic-auth############################

http.basic.log: true

http.basic.user: "some_user"

http.basic.password: "some_password"

http.basic.xforward: "X-Forwarded-For"

http.basic.ipwhitelist: []

http.basic.trusted_proxy_chains: []


