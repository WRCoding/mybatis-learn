package longjunwang.com.mappers;

import longjunwang.com.entity.User;

public interface IUserDao {

    User queryUserInfoById(Long id);

}
