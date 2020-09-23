package com.agp.demo.hive;

/**
 * Hive 常用函数
 */
public class HiveFunctionAnnotation {
    /*select nvl(bonus,0),name from employees*/
    /**NVL(column_name,replace value)用默认的value代替*/
    /*select nvl(bonus,deptno),name from employees 用另一个列名代替*/
    /*时间格式 select date_format('2020-09-16','yyyy-MM-dd HH:mm:ss') 将时间格式化输出*/
    /*select date_add('2020-09-16',5)   or -5 负数也可*/
    /*date_sub('2020-09-16',5)类似*/
    /*select datediff('2020-09-16','2020-09-14')   2 日期相减*/
    /*select regexp_replace('2020/09/16','/','-')  -> 2020-09-16 正则替换*/

    /*select concat(name,'_',age,'-',level) 参数个数不限制*/
    /*concat_ws('-',name,age,level) ws-》with ，将连接符定义为第一个然后指定后续*/
    /*select collect_set(level) => [1,2,3,4] 返回一个不重复的数组*/
    /**
     * table person_info
     * constellation name blood_type
     * 射手座          a   A
     * 射手座          b   A
     * 狮子座          c   O
     *
     * 输出 射手座,A  a|b
     *      狮子座,O  c
     * 第一步：
     * select
     * concat(constellation,',',blood_type) as constellation_blood_type,
     * name
     * from person info (作为t1）
     *
     * 第二步：
     *
     * select
     * constellation_blood_type,
     * concat_ws('|',collect_set(name))
     * from (
     *       * select
     *      * concat(constellation,',',blood_type) as constellation_blood_type,
     *      * name
     *      * from person info
     * ) t1
     * group by constellation_blood_type
     *
     *一层层嵌套
     *
     * */

    /*lateral view udtf(column)  函数 + 其他列  侧写*/
    /**select
     * movie
     * category_name
     * from
     * movie_info lateral view explode(category) table_tmp as category_name*/

}
