###TEST
use test;
create table if not exists student(
id int,
name varchar(100),
level int,
constraint primary key (id));

insert into student values(1,"a",1),(2,"b",2),(3,"c",3);

insert into student values(1,"a",1),(2,"b",2),(3,"c",3);
select * from student;

show create table student;

explain select * from student where id = 3 and name like "c%" ;

alter table student add index index_name (`name`(30));

show index  from student;
alter table student drop index index_name;
alter table student drop primary key;

alter table student add primary key (id)

###procedure
delimiter ;; \
create procedure tb_insert() \
begin \
declare i int default 1;\
while i< 8000\
do\
insert into tbl1 select rand()*80000,rand()*80000;\
set i=i+1;\
end while ;\
commit;\
end;;\
delimiter ;

call tb_insert();

###视图View
use test;
explain select * from tbl1 order by a, b desc limit 5;

select * from sys.sys_config;

create view v_tbl1(x,y) as select a,b from tbl1 limit 100;

select * from v_tbl1;
show create table  v_tbl1;

select * from tbl1 where a < 30;
select * from student;

show create table student;

insert into student values(16,"dd",3),(21,"ee",2);

explain select * from student,tbl1 where student.id = tbl1.a;

explain select * from student join tbl1 on student.id = tbl1.a;

select student.id,student.name,student.level from student,tbl1 where student.id =tbl1.a;

create or replace view v_tbl1(x,y,z) as select student.id,student.name,student.level from student,tbl1 where student.id =tbl1.a;

alter view v_tbl1(x,y,z,total) as select student.id ,student.name,student.level,student.id * student.level from
student,tbl1 where student.id = tbl1.a;
#####使用view名称当做表名直接用
update  v_tbl1 set z=4 where x=21;

#####对view修改插入删除数据，对应的基础表也跟着变更，反之亦然。

####视图不能更新场景
* join视图
* select语句里面使用了聚合函数
* distinct,union,top,group by or having子句


