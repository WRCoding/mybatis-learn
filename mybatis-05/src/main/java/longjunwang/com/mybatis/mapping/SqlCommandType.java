package longjunwang.com.mybatis.mapping;

import org.omg.CORBA.UNKNOWN;

/**
 * desc:
 *
 * @author ink
 * date:2023-07-15 16:27
 */
public enum SqlCommandType {
    /**
     * 未知
     */
    UNKNOWN,
    /**
     * 插入
     */
    INSERT,
    /**
     * 更新
     */
    UPDATE,
    /**
     * 删除
     */
    DELETE,
    /**
     * 查找
     */
    SELECT;

}
