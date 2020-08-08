package com.agp.demo.cs;

/**
 * 4层协议  数据链路层（以太网协议：将光电磁信号调成包、网卡mac地址发送）
 * 网络层（IP），区分不同网络。根据IP区分，子网掩码相与。
 * 传输层（TCP：数据包到达对方IP和网卡后，传递到哪个端口号 socket）
 * 应用层（比如FTP，HTTP等，如何将数据组合进行解析。
 *
 * 以太网包15000字节限制。所以HTTP请求包一般会切分成多个以太网包。
 *
 * 传输中，再各个子网络中都是广播的形式传输。
 * 如果不是本地网络的MAC地址， ARP协议查看LAN网络的对应MAC。
 * 就想包裹的下一站MAC设置为配置的网关IP对应的MAC地址。
 *
 * 网关将包裹修改MAC地址，转发到本网络的另一个目标或者吓一跳的网关。
 *
 *
 * DNS服务器都是IP地址。先去给domain name service服务器发送域名解析请求。
 * 转发包MAC为本地网关，然后接着路由只到对应的IP所在机器，然后解析IP包，解析TCP包。解析对应的
 * DNS服务器请求。 将响应信息封装为DNS响应报文， 封装为TCP包（加上D、S的端口） ，封装为IP包，封装为以太网包。（以太网包可能要切包）
 *
 * 收到DNS解析后，将请求包封装为HTTP请求报文，封装TCP包，封装IP包，封装以太网包。 下一站。。。
 *
 */
public class ComputerScience {
}
