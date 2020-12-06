package com.agp.demo.cs;

/**
 * Paxos将系统中的角色分为
 * 提议者 (Proposer)，决策者 (Acceptor)，和最终决策学习者 (Learner):
 *
 * Proposer: 提出提案 (Proposal)。Proposal信息包括提案编号 (Proposal ID) 和提议的值 (Value)。
 * Acceptor：参与决策，回应Proposers的提案。收到Proposal后可以接受提案，
 * 若Proposal获得多数Acceptors的接受，则称该Proposal被批准。
 * Learner：不参与决策，从Proposers/Acceptors学习最新达成一致的提案（Value）。
 *
 * Paxos算法通过一个决议分为两个阶段:
 *
 * 第一阶段：Prepare阶段。Proposer向Acceptors发出Prepare请求，
 * Acceptors针对收到的Prepare请求进行Promise承诺。
 *
 * 第二阶段：Accept阶段。Proposer收到多数Acceptors承诺的Promise后，
 * 向Acceptors发出Propose请求，Acceptors针对收到的Propose请求进行Accept处理。
 * 第三阶段：Learn阶段。Proposer在收到多数Acceptors的Accept之后，标志着本次Accept成功，
 * 决议形成，将形成的决议发送给所有Learners。
 *
 * Paxos算法流程中的每条消息描述如下：
 *
 * Prepare: Proposer生成全局唯一且递增的Proposal ID (可使用时间戳加Server ID)，
 * 向所有Acceptors发送Prepare请求，这里无需携带提案内容，只携带Proposal ID即可。
 * Promise: Acceptors收到Prepare请求后，做出“两个承诺，一个应答”。
 * 两个承诺：
 *
 * prepare-->客回复promise--》主过半后发起accept(n,value) --》客 落盘 并回复落盘的最新proposal
 *
 * 未过半的时候？--》 proposer不执行二阶段的accept。直接再执行新的proposalID的prepare第一阶段。
 * accept响应过半后，如果其中任何一个acceptor接收的proposalID大于自己发送的proposalID。
 * 从头开始继续发送（失败了）。
 * else 全部都响应自己提的proposalId then success。（value is chosen)
 *
 * 1. 不再接受Proposal ID小于等于（注意：这里是<= ）当前请求的Prepare请求。
 *
 * 2. 不再接受Proposal ID小于（注意：这里是< ）当前请求的Propose请求。
 *
 * 一个应答：
 *
 * 不违背以前作出的承诺下，
 * 回复已经Accept过的提案中Proposal ID最大的那个提案的Value和Proposal ID，
 * 没有则返回空值。
 *
 *
 */
public class Paxos算法 {
}
