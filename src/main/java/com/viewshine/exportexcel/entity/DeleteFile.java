package com.viewshine.exportexcel.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表示要删除文件对象
 * @author changWei[changwei@viewshine.cn]
 */
@Data
@AllArgsConstructor
public class DeleteFile {

    /**
     * 表示唯一标识
     */
    private String UUID;

    /**
     * 文件在磁盘的路径
     */
    private String path;

    /**
     * 表示文件要删除的时间
     */
    private LocalDateTime expire;

}
