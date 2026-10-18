package com.zeewoncode.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrouponInitiateDTO implements Serializable {

    private Long addressId;
    private Long defineId;
    private Integer quantity;
}
