##数据仓库环境
###分区设计
好处：
* 数据易装载
* 数据访问快速
* 数据存档
* 数据删除
* 数据监控
* 数据存储
易于 重构、索引、顺序扫描、重组、恢复、监控。
#### 分区标准
* 时间
* 业务范围
* 地理位置
* 组织单位
* All above
#### 分区模式
* 物理层DBMS上面进行分区 （表里面）
* 应用层分区（物理上面不分） 更好 更容易迁移和管理


