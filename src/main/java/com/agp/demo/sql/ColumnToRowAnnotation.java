package com.agp.demo.sql;

/**
 * 列传行
 * select user_name, '语文' COURSE , CN_SCORE as SCORE from test_tb_grade2
 * union select user_name, '数学' COURSE, MATH_SCORE as SCORE from test_tb_grade2
 * union select user_name, '英语' COURSE, EN_SCORE as SCORE from test_tb_grade2
 * order by user_name,COURSE;
 *
 * 主要靠union。将结果row串起来。
 */
public class ColumnToRowAnnotation {
}
