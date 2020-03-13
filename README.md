# 导出Excel的服务

### 注意点：
#### 1. 如果使用NGINX代理部署这个导出Excel服务框架的话，为了获取服务端真实的地址的话，在NGINX中添加如下一句话
`proxy_set_header Host $host;`

NGINX的配置文件最终结果为：
```
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
    }
```

#### 2. 如果使用了NGINX代理部署了这个导出Excel服务框架的话，为了获取客户端真实的地址的话，在NGINX中添加下面代码
```
proxy_set_header X-Real-IP $remote_addr;
proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
```

## 升级版本：
#### Excel导出服务0.3版本
##### 添加功能：
    1.使用Guava-retry框架，进行客户端通知失败的重试禁止
    2.使用服务重启，自动删除过期的文件内容，从而造成文件的推挤
    3.添加Mongo的NOSQL的支持，并且可以配置多个Mongo连接
    4.优化日期的处理
#### Excel导出服务0.1版本
    1.添加MYSQL数据库的支持，并且可以配置多个数据源