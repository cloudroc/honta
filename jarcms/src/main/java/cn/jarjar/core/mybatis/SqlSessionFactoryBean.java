package cn.jarjar.core.mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.DefaultDatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.logging.Log;
import com.jfinal.kit.StrKit;

/**
 * @title 构建sqlSessionFactory
 * @author cloudroc
 * @email kaedeen@qq.com
 * @description
 * @date 上午11:02:56 2015-2-13
 */
public class SqlSessionFactoryBean {
    public static final String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

    private final Log logger = LogFactory.getLog(SqlSessionFactoryBean.class);

    private static final SqlSessionFactoryBean me = new SqlSessionFactoryBean();

    private SqlSessionFactoryBean() {

    }

    private SqlSessionFactory sqlSessionFactory;

    /**
     * 提供外部初始化
     *
     * @param dataSource
     * @param configLocation
     * @param typeAliasesPackage
     * @param typeAliases
     * @param plugins
     * @param typeHandlers
     * @param typeHandlersPackage
     * @param configurationProperties
     * @throws IOException
     */
    public void init(DataSource dataSource, String configLocation,
                     String typeAliasesPackage, Class<?>[] typeAliases,
                     Interceptor[] plugins, TypeHandler<?>[] typeHandlers,
                     String typeHandlersPackage, Properties configurationProperties)
            throws IOException {
        this.dataSource = dataSource;
        this.configLocation = configLocation;
        this.typeAliasesPackage = typeAliasesPackage;
        this.typeAliases = typeAliases;
        this.plugins = plugins;
        this.typeHandlers = typeHandlers;
        this.typeHandlersPackage = typeHandlersPackage;
        this.configurationProperties = configurationProperties;

        // 构建sqlSessionFactory
        sqlSessionFactory = buildSqlSessionFactory();

    }

    public static SqlSessionFactoryBean me() {
        return me;
    }

    /**
     * 获取sqlSessionFactory
     * @return
     */
    public SqlSessionFactory getSqlSessionFactory() {
        return this.sqlSessionFactory;
    }


    private DataSource dataSource;

    // mybatis配置文件路径
    private String configLocation;

    // 需要自动扫描的mapper所在包
    private String typeAliasesPackage;

    private Class<?>[] typeAliases;

    private Interceptor[] plugins;

    private TypeHandler<?>[] typeHandlers;

    private String typeHandlersPackage;

    private Properties configurationProperties;

    private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

    // 环境名称
    private String environment = SqlSessionFactoryBean.class.getSimpleName();

    private DatabaseIdProvider databaseIdProvider = new DefaultDatabaseIdProvider();

    /**
     * 构建SqlSessionFactory
     *
     * @return
     * @throws IOException
     */
    protected SqlSessionFactory buildSqlSessionFactory() throws IOException {

        logger.debug("buildSqlSessionFactory start.....");
        Configuration configuration;
        XMLConfigBuilder xmlConfigBuilder = null;
        if (this.configLocation != null) {
            InputStream is = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(configLocation);
            xmlConfigBuilder = new XMLConfigBuilder(is, null,
                    this.configurationProperties);
            configuration = xmlConfigBuilder.getConfiguration();
        } else {
            this.logger
                    .debug("Property 'configLocation' not specified, using default MyBatis Configuration");
            configuration = new Configuration();
            configuration.setVariables(this.configurationProperties);
        }

        // typeAliasesPackage
        if (StrKit.notBlank(this.typeAliasesPackage)) {
            String[] typeAliasPackageArray = StrKit.tokenizeToStringArray(
                    this.typeAliasesPackage, CONFIG_LOCATION_DELIMITERS);
            for (String packageToScan : typeAliasPackageArray) {
                configuration.getTypeAliasRegistry().registerAliases(
                        packageToScan);
                this.logger.debug("Scanned package: '" + packageToScan
                        + "' for aliases");
            }
        }

        // typeAliases
        if (!StrKit.isEmpty(this.typeAliases)) {
            for (Class<?> typeAlias : this.typeAliases) {
                configuration.getTypeAliasRegistry().registerAlias(typeAlias);
                this.logger.debug("Registered type alias: '" + typeAlias + "'");
            }
        }

        // plugins
        if (!StrKit.isEmpty(this.plugins)) {
            for (Interceptor plugin : this.plugins) {
                configuration.addInterceptor(plugin);
                this.logger.debug("Registered plugin: '" + plugin + "'");
            }
        }

        // typeHandlersPackage
        if (StrKit.notBlank(this.typeHandlersPackage)) {
            String[] typeHandlersPackageArray = StrKit.tokenizeToStringArray(
                    this.typeHandlersPackage, CONFIG_LOCATION_DELIMITERS);
            for (String packageToScan : typeHandlersPackageArray) {
                configuration.getTypeHandlerRegistry().register(packageToScan);
                this.logger.debug("Scanned package: '" + packageToScan
                        + "' for type handlers");
            }
        }

        // typeHandlers
        if (!StrKit.isEmpty(this.typeHandlers)) {
            for (TypeHandler<?> typeHandler : this.typeHandlers) {
                configuration.getTypeHandlerRegistry().register(typeHandler);
                this.logger.debug("Registered type handler: '" + typeHandler
                        + "'");
            }
        }

        if (xmlConfigBuilder != null) {
            try {
                xmlConfigBuilder.parse();
                this.logger.debug("Parsed configuration file: '"
                        + this.configLocation + "'");
            } catch (Exception ex) {
                logger.error("Failed to parse config resource: "
                        + this.configLocation);
                throw new IOException("Failed to parse config resource: "
                        + this.configLocation, ex);

            } finally {
                logger.error("finally parse config resource: "
                        + this.configLocation);
                ErrorContext.instance().reset();
            }
        }

        // 直接使用jdbc事务控制
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment(this.environment,
                transactionFactory, dataSource);
        configuration.setEnvironment(environment);

        if (this.databaseIdProvider != null) {
            try {
                configuration.setDatabaseId(this.databaseIdProvider
                        .getDatabaseId(this.dataSource));
            } catch (SQLException e) {
                logger.error("Failed getting a databaseId");
                throw new IOException("Failed getting a databaseId", e);
            }
        }

        // TODO mapperLocations Spring推荐方式，具体情况待研究

        // 设置默认重用提高性能
        configuration.setDefaultExecutorType(ExecutorType.REUSE);

        logger.debug("buildSqlSessionFactory suceed!");
        return this.sqlSessionFactoryBuilder.build(configuration);
    }
}
