package com.viewshine.exportexcel.mongodb;

import lombok.Builder;
import lombok.Data;

/**
 * @author changWei[changwei@viewshine.cn]
 */
@Data
@Builder
class AddressInChina {

    private String province;

    private String city;

}
