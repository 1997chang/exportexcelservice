# 导出Excel的服务

### 注意点：
#### 1. 如果使用NGINX代理部署这个导出Excel服务框架的话，在NGINX中添加如下一句话
`proxy_set_header Host $host;`

NGINX的配置文件最终结果为：
```
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
    }
```