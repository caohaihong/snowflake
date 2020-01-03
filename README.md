# 警告
本分支基于cas，高并发下争抢严重，请不要使用

# snowflake
此项目是一个基于`zookeeper`、`spring boot`、`snowflake算法`开发的，独立部署的项目，此项目并未在生产环境使用过，想要使用的开发者请自行考虑其风险性

这不是一个组件

# config
请查看 `application.properties`

* `zk.host` 是zookeeper地址
* `work.conf.filepath` 是本地缓存的work节点信息
* `work.conf.znode` 是zookeeper节点配置

# How to build
* Latest stable [Oracle JDK 8](http://www.oracle.com/technetwork/java/)
* Latest stable [gradle](https://gradle.org/)