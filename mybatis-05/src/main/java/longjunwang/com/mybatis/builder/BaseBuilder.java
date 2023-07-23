package longjunwang.com.mybatis.builder;

import longjunwang.com.mybatis.session.Configuration;
import longjunwang.com.mybatis.type.TypeAliasRegistry;

/**
 * desc: BaseBuilder
 *
 * @author ink
 * date:2023-07-15 16:25
 */
public abstract class BaseBuilder {
    protected final Configuration configuration;
    protected final TypeAliasRegistry typeAliasRegistry;
    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
        typeAliasRegistry = this.configuration.getTypeAliasRegistry();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

}
