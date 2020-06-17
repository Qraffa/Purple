# Purple
judger

## 部署

### 构建docker镜像

#### 构建judger镜像

1. 将application.yml配置文件，libjudger.so文件，purple-judger.jar文件，Dockerfile文件，放在同一路径下
2. 执行以下命令

```bash
docker build -t pjudger:2.0 .
docker run --rm --name pjudger -p 9500:9500 -d pjudger:2.0
```

#### 构建backend镜像

1. 将application.yml配置文件，purple-judger.jar文件，Dockerfile文件，放在同一路径下
2. 执行以下命令

```bash
docker build -t pback:2.0 .
docker run --rm --name pback -p 9501:9501 -d pback:2.0
```

#### 部署zookeeper

```bash
docker pull zookeeper
docker run --rm --name zookeeper -p 2181:2181 -d zookeeper
```

#### 部署redis

```bash
docker pull redis
docker run --rm --name redis -p 6379:6379 -d redis
```

## 参考

判题沙盒：[https://github.com/QingdaoU/Judger](https://github.com/QingdaoU/Judger)