package com.gwm.cloudecs.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ObsAccount {
    private Integer id;

    private String accessKey;

    private String cephUser;

    private String createAt;

    private String deleted;

    private String deletedAt;

    private String jobNum;

    private Integer quota;

    private String secretKey;

    private String updateAt;


}
