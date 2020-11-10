package com.gwm.clouduser.model.vo;

import com.gwm.clouduser.model.entity.Flavor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Setter
@Getter
@ToString
public class PriceModelVO {
    private Long id;

    private String uuid;

    private Date createdAt;

    private Date updatedAt;

    private Date deletedAt;

    private Integer deleted;

    private String region;

    private String zone;

    private String cloudType;

    private String type;

    private String flavorId;

    private String price;

    private String diskType;

    private String instanceType;

    private static final long serialVersionUID = 1L;

    private Flavor flavor;

}
