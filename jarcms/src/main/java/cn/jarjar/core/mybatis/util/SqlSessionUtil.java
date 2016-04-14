package cn.jarjar.core.mybatis.util;

import cn.jarjar.core.mybatis.SqlSessionFactoryBean;
import cn.jarjar.core.mybatis.SqlSessionManager;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;


/**
 * @title mybatis通用数据操作工具，线程安全
 * @author cloudroc
 * @email kaedeen@qq.com
 * @description
 * @date 下午11:21:15 2015-2-27
 */
public class SqlSessionUtil {

    private static SqlSession sqlSession ;

    private static SqlSessionFactory getSqlSessionFactory() {
        return SqlSessionFactoryBean.me().getSqlSessionFactory();
    }

    /**
     * 获取线程安全的SqlSessionManager
     * @param
     * @return
     */
    public static SqlSession getSqlSession(){

        if(null==sqlSession){
            sqlSession = SqlSessionManager.newInstance(getSqlSessionFactory());
        }

        return sqlSession;
    }
}
