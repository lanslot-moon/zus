package org.kitona.zus.client.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfoFacadeDto implements Serializable {

    private String name;

    private String avatar;
}
