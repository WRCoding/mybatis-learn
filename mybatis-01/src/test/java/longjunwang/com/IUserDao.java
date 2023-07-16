package longjunwang.com;

/**
 * desc: IUserDao
 *
 * @author ink
 * date:2023-07-09 18:09
 */
public interface IUserDao {
    String queryUserName(String uId);

    Integer queryUserAge(String uId);
}
