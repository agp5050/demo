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

