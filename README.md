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