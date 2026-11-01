package com.zeewoncode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusDTO implements Serializable {

    private Integer status; // 1正常 0冻结
}
