package com.skp.fintech.p2plending.backoffice.mapper.plpdb;

import com.skp.fintech.p2plending.backoffice.user.application.UserInformation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    UserInformation selectLoginUserInfo(String userId);

    boolean insertUser(UserInformation param);

}
