package com.agp.demo.security;

/**
 * 1. 浏览正规网站
 * 2. 对用户发表评论进行转义比如将 <></> 这种转义为&gt，让脚本失效。 （消毒机制）
 *3.服务器将cookie返回时，将cookie设置为httpOnly  防止cookie被盗。
 */
public class XSSAnnotation {
}
