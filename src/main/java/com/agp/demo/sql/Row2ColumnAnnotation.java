package com.agp.demo.sql;

/**
 * SELECT cname ,
 *     MAX(CASE cource WHEN '数学' THEN score ELSE 0 END ) 数学,
 *     MAX(CASE cource WHEN '语文' THEN score ELSE 0 END ) 语文,
 *     MAX(CASE cource WHEN '英语' THEN score ELSE 0 END ) 英语
 * FROM t_user_score
 * GROUP BY cname;
 *
 * Max也可以替换为SUM
 *
 * 用IF替换case when then else
 * SELECT userid,
 * SUM(IF(`subject`='语文',score,0)) as '语文',
 * SUM(IF(`subject`='数学',score,0)) as '数学',
 * SUM(IF(`subject`='英语',score,0)) as '英语',
 * SUM(IF(`subject`='政治',score,0)) as '政治'
 * FROM tb_score
 * GROUP BY userid
 *
 *
 */
public class Row2ColumnAnnotation {
}
