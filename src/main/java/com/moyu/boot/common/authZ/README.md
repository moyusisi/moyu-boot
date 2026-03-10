## 鉴权组件

通过Sa-Token实现认证鉴权
依赖redis

### 功能
* 提供了全局认证过滤器(可配置需要认证访问的地址、白名单地址)
* Jwt、Token等令牌处理Service
* 默认的sm4加密器
* 登陆用户信息对象LoginUser及上下文工具类LoginUserUtils
* 提供了数据权限与处理能力