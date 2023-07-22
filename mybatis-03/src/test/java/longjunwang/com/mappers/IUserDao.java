package longjunwang.com.mappers;

import longjunwang.com.entity.User;

public interface IUserDao {

    User queryUserInfoById(String uId);

}
