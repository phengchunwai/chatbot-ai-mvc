# 模型
领域层的模型数据就是对整个业务需要参与的数据对象进行一个抽象然后封装

部署流程 就是把代码提交到仓库上， 然后在服务器上拉去
执行 mvn install all
接着执行 ./build.sh 构建docker镜像
然后进入到doc里面执行 docker-compose -f docker-compose-app.yml up -d 启动镜像