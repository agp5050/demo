package com.agp.demo.security;

/**
 * 1. 服务器将颁发给客户的cookie设置为Httponly
 * 2.随机token添加到隐藏元素
 * 3.验证码： 图形验证码，
 * 4.关键交易短信验证码
 *
 *
 * 文件上传：
 * 限制文件种类，读取文件开头，判断魔数，看看是不是伪造的类型。
 * 压缩文件，破话文件的结构
 */
public class CSRFAnnotation {
}
