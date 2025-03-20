package org.liuqf.make_random_data_product.Dao;

import org.apache.ibatis.annotations.Param;
import org.liuqf.make_random_data_product.pojo.PersonData;
import org.liuqf.make_random_data_product.pojo.SaleData;

import java.util.List;

public interface PersonDataDao {
    PersonData getDataBySaleInfo(@Param("saleInfo") SaleData saleInfo);
}
