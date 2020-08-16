package com.agp.demo.codedesign;

import com.agp.demo.ABC;

import java.util.ArrayList;
import java.util.List;

/**
 * 责任链模式：
 * 比如网站过滤，可能有HTML安全过滤器、敏感词过滤器、权限过滤器等等
 * 后续可能还会继续增加。
 * 可以用 List<Filter> filters=new ArrayList();
 * 每新增一个就添加到链条里面。
 *
 * 代码固定写为遍历filters，然后都执行filter就可以了。每次增加。
 * 这就是责任链模式。
 */
public class ChainResponsibilityAnnotation {
}

/**
 * 让FilterChain也实现Filter接口。
 * FilterChain f2,f1
 * f2.add(f1)  会导致。 遍历执行doFilter时，轮到这个f1的doFilterChain会递归调用里面的filterChain
 *
 *
 */
/*doFilter是true的时候继续执行，Else就停止不再进行链条传送了。*/
class FilterChain implements Filter{
    List<Filter> filterChain=new ArrayList<>();
//    public void doFilter(ABC abc){  //原doFilter没有返回值，不能帮助filter结果，是否向下传递。
    public boolean doFilter(ABC abc){
        for (Filter f:filterChain){
          if (!f.doFilter(abc)) return false; //如果其中一条过滤失败，return false。
        }
        //遍历 执行filter。
        return true;
    }

    /**
     * @param filter
     * @return 返回当前对象，便于连续添加
     */
    public FilterChain addFilter(Filter filter){
        filterChain.add(filter);
        return this;
    }


}
interface Filter {
    boolean doFilter(ABC abc);
}