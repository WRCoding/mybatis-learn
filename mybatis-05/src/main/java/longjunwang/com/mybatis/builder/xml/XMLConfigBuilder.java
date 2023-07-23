package longjunwang.com.mybatis.builder.xml;

import longjunwang.com.mybatis.builder.BaseBuilder;
import longjunwang.com.mybatis.datasource.DataSourceFactory;
import longjunwang.com.mybatis.io.Resource;
import longjunwang.com.mybatis.mapping.BoundSql;
import longjunwang.com.mybatis.mapping.Environment;
import longjunwang.com.mybatis.mapping.MappedStatement;
import longjunwang.com.mybatis.mapping.SqlCommandType;
import longjunwang.com.mybatis.session.Configuration;
import longjunwang.com.mybatis.transaction.TransactionFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import javax.sql.DataSource;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * desc: XMLConfigBuilder
 *
 * @author ink
 * date:2023-07-15 16:25
 */
public class XMLConfigBuilder extends BaseBuilder {

    private Element root;

    public XMLConfigBuilder(Reader reader) throws DocumentException {
        super(new Configuration());
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new InputSource(reader));
        root = document.getRootElement();
    }

    public Configuration parse() {
        try {
            environmentsElement(root.element("environments"));
            // 解析映射器
            mapperElement(root.element("mappers"));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
        return configuration;
    }

    private void environmentsElement(Element context) throws Exception {
        String environment = context.attributeValue("default");
        List<Element> elementList = context.elements("environment");
        for (Element element : elementList) {
            String id = element.attributeValue("id");
            if (environment.equals(id)){
                String txType = element.element("transactionManager").attributeValue("type");
                TransactionFactory txFactory = (TransactionFactory) typeAliasRegistry.resolveAlias(txType).newInstance();
                Element dataSourceElement = element.element("dataSource");
                String dataSourceType = dataSourceElement.attributeValue("type");
                DataSourceFactory dataSourceFactory = (DataSourceFactory) typeAliasRegistry.resolveAlias(dataSourceType).newInstance();
                List<Element> propertyList = dataSourceElement.elements("property");
                Properties props = new Properties();
                for (Element property : propertyList) {
                    props.setProperty(property.attributeValue("name"), property.attributeValue("value"));
                }
                DataSource dataSource = getDataSource(dataSourceFactory, props);
                setEnvironment(id, txFactory, dataSource);
            }
        }
    }

    private void setEnvironment(String id, TransactionFactory txFactory, DataSource dataSource) {
        Environment environment = new Environment.Builder(id).transactionFactory(txFactory).dataSource(dataSource).build();
        configuration.setEnvironment(environment);
    }

    private static DataSource getDataSource(DataSourceFactory dataSourceFactory, Properties props) {
        dataSourceFactory.setProperties(props);
        return dataSourceFactory.getDataSource();
    }


    private void mapperElement(Element mappers) throws Exception {
        List<Element> mapperList = mappers.elements("mapper");
        for (Element e : mapperList) {
            // 解析处理，具体参照源码
            String resource = e.attributeValue("resource");
            Reader resourceAsReader = Resource.getResourceAsReader(resource);
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new InputSource(resourceAsReader));
            Element rootElement = document.getRootElement();
            String namespace = rootElement.attributeValue("namespace");
            List<Element> selectNodes = rootElement.elements("select");
            for (Element selectNode : selectNodes) {
                String id = selectNode.attributeValue("id");
                String parameterType = selectNode.attributeValue("parameterType");
                String resultType = selectNode.attributeValue("resultType");
                String sql = selectNode.getText();

                // ? 匹配
                Map<Integer, String> parameter = new HashMap<>();
                Pattern pattern = Pattern.compile("(#\\{(.*?)})");
                Matcher matcher = pattern.matcher(sql);
                for (int i = 1; matcher.find(); i++) {
                    String g1 = matcher.group(1);
                    String g2 = matcher.group(2);
                    parameter.put(i, g2);
                    sql = sql.replace(g1, "?");
                }

                String msId = namespace + "." + id;
                String nodeName = selectNode.getName();
                SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
                BoundSql boundSql = new BoundSql(sql, parameter, parameterType, resultType);
                MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType, boundSql).build();
                // 添加解析 SQL
                configuration.addMappedStatement(mappedStatement);
            }

            configuration.addMapper(Resource.classForName(namespace));
        }

        // 注册Mapper映射器
    }
}

