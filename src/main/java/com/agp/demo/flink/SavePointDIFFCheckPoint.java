package com.agp.demo.flink;

/**
 *创建算法两个基本一致
 *
 * Sp可以多些额外的元数据
 * Sp手动触发，主要用来
 * 定时备份，更新应用程序（Sp后，停止应用程序，修Bug）
 * 确保可以从源状态点继续执行。
 * 里面的算子不能动，都有自动分配的UID。 除非原程序明确指定UID。
 *
 * 算子个数不一致后，恢复时，新算子是没有之前对应的checkpoint状态的。UID也对不上。
 *
 *
 * 版本迁移，暂停和重启应用。
 *
 *
 */
public class SavePointDIFFCheckPoint {
}
