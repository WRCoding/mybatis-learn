package longjunwang.com.mybatis.builder;

import longjunwang.com.mybatis.session.Configuration;

/**
 * desc: BaseBuilder
 *
 * @author ink
 * date:2023-07-15 16:25
 */
public abstract class BaseBuilder {
    protected final Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

}
