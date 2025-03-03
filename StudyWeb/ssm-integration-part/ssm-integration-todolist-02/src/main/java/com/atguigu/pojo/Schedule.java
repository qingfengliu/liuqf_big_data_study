package com.atguigu.pojo;

import lombok.Data;

/**
 * projectName: com.atguigu.pojo
 *
 * description: 任务实体类
 */
@Data
public class Schedule {

    private Integer id;
    private String title;
    private Boolean completed;
}
