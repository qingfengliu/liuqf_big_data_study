package org.liuqf.make_random_data_product.Dao;

import org.apache.ibatis.annotations.Param;
import org.liuqf.make_random_data_product.pojo.SaleData;

import java.util.List;

public interface SaleDataDao {
    /**
     * 获取所有用户信息
     * @return
     */
    List<SaleData> getAll();
    List<SaleData> getPage(@Param("start") int start,@Param("pageSize") int end);
}