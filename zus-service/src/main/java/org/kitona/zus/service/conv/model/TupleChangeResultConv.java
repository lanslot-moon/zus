package org.kitona.zus.service.conv.model;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/*
 * Author: 登林
 * Email: wangli.liu@kitona.org
 * Date: 2026/4/30 14:40
 * Version: V1.0
 * Description: Xxxx
 */
@Mapper(componentModel = "spring")
public class TupleChangeResultConv {

    public static TupleChangeResultConv INSTANCE = Mappers.getMapper(TupleChangeResultConv.class);
}
