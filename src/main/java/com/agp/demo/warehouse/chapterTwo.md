##数据仓库环境
###数据仓库是一个面向主题的、集成的、非易失的、随时间变化的用来支持管理人员决策的数据集合
* 各个部门面向主题不同（数据集市主题不同）
* 集成的（通过ETL从各个数据源集成的，比如完成1/0,f/m，男/女统一转化
度量统一、描述统一、关键字统一等从各个数据源ETL统一。
* 非易失，一般以批量式导入，一般不更新。
* 随时间变化 每个记录都有时间，在某个时间是准确的，不断增加，一般跨度是年级别
###数据仓库结构
* 早期细节级
* 当前细节级
* 轻度综合级
* 高度综合级
###面向主题
企业主题，典型有：
顾客、产品、交易、活动、政策、索赔、账目
###数据仓库从1-》n天演变（理想模式）
* day1 观察现有操作型事物处理(OLTP)系统
* day2 数据仓库建立了单个主题域里面有几张相关表
* day3 更多的主题域和数据
* day4 各种各样的DSS应用,应用太多和太多请求，一些用户感觉到服务排队时间过长
* day5 部门数据库（数据集市）兴起，各个部门把数据从数据仓库调入自己的部门处理环境，降低成本，
* day6 得到部门数据比数仓更快更低成本、大量转去各个部门处理
* dayn 体系结构充分发展，个别在数仓使用，大部分用各自从数仓同步的数据进行开发应用。

###数据粒度
* 高粒度低细节
* 低粒度高细节
###活样本数据库
随机从数据库重抽取一定量样本。节省大量时间。但是会损失精度。还要筛选样本
###双重粒度级 可以处理绝大数请求
* 轻度综合， 可以满足95%的DSS的请求
* 真实档案， 可以5%的请求，或者更少。