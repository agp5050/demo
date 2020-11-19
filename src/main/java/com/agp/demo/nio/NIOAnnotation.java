package com.agp.demo.nio;

/**
 * man 2 查看linux kernal api
 * man 2 select

 *        int select(int nfds, fd_set *readfds, fd_set *writefds,
 *                   fd_set *exceptfds, struct timeval *timeout)
 *  * man 2 poll
 *         int poll(struct pollfd *fds, nfds_t nfds, int timeout);
 *         poll-select 需要程序自己维护fds，每次查询时，需要传递大量的fdset去请求
 *         IO占用大，CPU也占用大，不断查询
 *
 * man epoll:  epoll_create---epoll_ctl---epoll_wait
 *        The  epoll API performs a similar task to poll(2): monitoring multiple file descriptors to see if I/O is possible on any of them.  The epoll API can be used either as an edge-triggered or a level-
 *        triggered interface and scales well to large numbers of watched file descriptors.  The following system calls are provided to create and manage an epoll instance:
 *
 *        *  epoll_create(2) creates a new epoll instance and returns a file descriptor referring to that instance.  (The more recent epoll_create1(2) extends the functionality of epoll_create(2).)
 *
 *        *  Interest in particular file descriptors is then registered via epoll_ctl(2).  The set of file descriptors currently registered on an epoll instance is sometimes called an epoll set.
 *
 *        *  epoll_wait(2) waits for I/O events, blocking the calling thread if no events are currently available.
 */
public class NIOAnnotation {
    /**jdk1.4之前BIO 执行时调用kernal的accept阻塞接口*/
    /*jdk1.8 BIO 没有Selector多路复用器时，
    执行时调用kernal的poll  =》 poll([{fd=5,events=POLLIN|POLLERR}],1,-1)
    * 这个后面的是传递了一个fd=5的这个socket进行poll*/

    /**没有selector就会执行poll select 传递fds 然后kernal遍历这些fds查看是否有新消息*/


    /**epoll不一样，有了selector之后：
     * 1. kernal里面开辟空间存放fds
     * 2. EPOLL_CTL_ADD 往空间添加新到的fds    java代码如下：
     * newClient.register(selector,SelectionKey.OP_READ);
     * 3.epoll_wait 等待kernal返回变更消息，如果某个fd断开 EPOLL_CTL_DEL*/


    /*socket(AF_INET6, SOCK_STREAM, IPPROTO_IP) = 7
bind(7, {sa_family=AF_INET6, sin6_port=htons(8080), inet_pton(AF_INET6, "::", &sin6_addr), sin6_flowinfo=htonl(0), sin6_scope_id=0}, 28) = 0
listen(7, 50)
epoll_create(256)  ==》相当于 selector.open()
epoll_ctl(10, EPOLL_CTL_ADD, 8, {EPOLLIN, {u32=8, u64=139981574111240}}) = 0
epoll_ctl(10, EPOLL_CTL_ADD, 7, {EPOLLIN, {u32=7, u64=139981574111239}}) = 0
epoll_wait(10, [{EPOLLIN, {u32=7, u64=139981574111239}}], 8192, -1) = 1  ==》java selector.select()
epoll_ctl(10, EPOLL_CTL_DEL, 11, 0x7f50f5fff49c) = -1 ENOENT (No such file or directory)
openat(AT_FDCWD, "/proc/net/if_inet6", O_RDONLY) = 8*/
}

