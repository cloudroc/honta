package cn.jarjar.core.mybatis.plugin;

import cn.jarjar.core.mybatis.SqlSessionFactoryBean;

import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.type.TypeHandler;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * honta_project
 * Created by cloudroc on 2016/4/13.
 * Jfinal集成mybatis插件
 */
public class MybatisJfPlugin implements IPlugin {

    private boolean isStarted = false;

    private DataSource dataSource;

    //mybatis配置文件路径
    private String configLocation;

    //需要自动扫描的mapper所在包
    private String typeAliasesPackage;

    private Class<?>[] typeAliases;

    private Interceptor[] plugins;

    private TypeHandler<?>[] typeHandlers;

    private String typeHandlersPackage;

    private Properties configurationProperties;

    //由jfianl初始化
    private IDataSourceProvider dataSourceProvider;

    public MybatisJfPlugin(IDataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    public boolean start() {
        if (isStarted)
            return true;

        if (dataSourceProvider != null)
            dataSource = dataSourceProvider.getDataSource();

        if (dataSource == null)
            throw new RuntimeException("MybatisJfPlugin start error: MybatisPlugin need DataSource or DataSourceProvider");


        if(configLocation==null)
            throw new RuntimeException("MybatisJfPlugin start error: need to use setConfigLocation method");

        SqlSessionFactoryBean bean= SqlSessionFactoryBean.me();
        try {

            bean.init(this.dataSource, this.configLocation, this.typeAliasesPackage,
                    this.typeAliases, this.plugins, this.typeHandlers, this.typeHandlersPackage
                    , this.configurationProperties);

            isStarted=true;

        } catch (Exception e) {
            e.printStackTrace();
            isStarted=false;
        }

        return isStarted;
    }

    public boolean stop() {
        isStarted = false;
        return false;
    }


    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public String getTypeAliasesPackage() {
        return typeAliasesPackage;
    }

    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    public Class<?>[] getTypeAliases() {
        return typeAliases;
    }

    public void setTypeAliases(Class<?>[] typeAliases) {
        this.typeAliases = typeAliases;
    }

    public Interceptor[] getPlugins() {
        return plugins;
    }

    public void setPlugins(Interceptor[] plugins) {
        this.plugins = plugins;
    }

    public TypeHandler<?>[] getTypeHandlers() {
        return typeHandlers;
    }

    public void setTypeHandlers(TypeHandler<?>[] typeHandlers) {
        this.typeHandlers = typeHandlers;
    }

    public String getTypeHandlersPackage() {
        return typeHandlersPackage;
    }

    public void setTypeHandlersPackage(String typeHandlersPackage) {
        this.typeHandlersPackage = typeHandlersPackage;
    }

    public Properties getConfigurationProperties() {
        return configurationProperties;
    }

    public void setConfigurationProperties(Properties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }
}
