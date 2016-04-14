package cn.jarjar.config;

import cn.jarjar.core.mybatis.plugin.MybatisJfPlugin;
import cn.jarjar.test.TestController;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.*;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;

/**
 * honta_project
 * Created by cloudroc on 2016/4/13.
 * Jfinal 启动配置类
 */
public class BootConfig extends JFinalConfig {

    Routes routes;

    /**
     * Config constant
     *
     * @param me
     */
    @Override
    public void configConstant(Constants me) {
        // 加载少量必要配置，随后可用getProperty(...)获取值
        loadPropertyFile("config.txt");
        me.setDevMode(getPropertyToBoolean("devMode", false));
    }

    /**
     * Config route
     *
     * @param me
     */
    @Override
    public void configRoute(Routes me) {
        this.routes = me;
        me.add("/test", TestController.class, "/test");
    }

    /**
     * Config plugin
     *
     * @param me
     */
    @Override
    public void configPlugin(Plugins me) {
        // 配置Druid数据库连接池插件
        DruidPlugin dp = new DruidPlugin(getProperty("jdbcUrl"), getProperty("user"), getProperty("password"));
        dp.setTestWhileIdle(true).setTestOnBorrow(false).setTestOnReturn(false);
        dp.addFilter(new StatFilter());
        WallFilter wall = new WallFilter();
        wall.setDbType("mysql");
        dp.addFilter(wall);
        me.add(dp);

        //配置mybatis插件
        MybatisJfPlugin mybatisPlugin=new MybatisJfPlugin(dp);
        mybatisPlugin.setConfigLocation("mybatis-config.xml");
        me.add(mybatisPlugin);
    }

    /**
     * Config interceptor applied to all actions.
     *
     * @param me
     */
    @Override
    public void configInterceptor(Interceptors me) {

    }

    /**
     * Config handler
     *
     * @param me
     */
    @Override
    public void configHandler(Handlers me) {
        DruidStatViewHandler dvh =  new DruidStatViewHandler("/druid");
        me.add(dvh);
    }

}
