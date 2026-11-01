package com.zeewoncode.dto;

import com.zeewoncode.validation.InValues;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusDTO implements Serializable {


    @InValues(value = {1, 0}, message = "值必须是1或0")
    private Integer status; // 1正常 0冻结
}
