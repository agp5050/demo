`自然演化式体系结构不能满足将来的需求`

###体系结构的转变（体系化的数仓环境）
####两种数据进行分类治理
* 原始数据：维持日常应用运行的细节性数据（或者叫操作型数据）
* 导出数据：经过汇总计算+各个粒度的数据

####体系层次结构
操作层-》原子/数据仓库层-》部门层（数据集市层）-》个体层（高层老板，启发式数据）
* 操作层：日常、细节、当前、频繁访问、面向应用
* 数仓层：大部分是粒度化数据、随时间变化、集成的（通过ETL）、面向主题(主题域)
* 部门层： 领域狭隘、一些原始数据。比如市场、财务两部门各自领域的数据
* 个体层：暂时、特定目的、启发式、非重复、基于PC和工作

####开发生命周期
* 传统操作型数据可以以瀑布模型：每个细节都是确定的，上一个完成，激活下一个
* 数仓开发：以螺旋式的开发。针对数据写程序-》分析结果-》系统需求明确-》系统设计修改。

#####创建数仓，可以从生产环境移走大量历史数据，减轻历史数据的包袱。
* 生产环境更容易纠错
* 更易于重构
* 更易于监控
* 更易于索引
* 此时生产环境 更小 更简单 更集中
####监控数据仓库
#####分两种一种是数据仓库的数据量监控、一个是数据的使用情况监控
* 通过数据增加确定什么增长，地方以及速率
* 哪些数据正在被用
* 用户使用响应时长
* who is use
* using how many data the user
* when is using
* how many data is in using
* data use rate，使用比率
#####建立概要文件帮忙监控
* 所有表目录
* 这些表内容概要
* 增长情况概要
* 访问表可用的索引目录
* 汇总表和汇总源目录

