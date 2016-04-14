package cn.jarjar.core.mybatis;

import cn.jarjar.core.mybatis.util.SqlSessionUtil;

/**
 * honta
 * Created by cloudroc on 2016/4/14.
 * 启用编程式事务
 */
public class MyTx {

    private static final SqlSessionManager sqlSession =(SqlSessionManager) SqlSessionUtil.getSqlSession();

    public static final boolean tx(IAtom atom){

        boolean flag = false;

        try{
            sqlSession.startManagedSession(false);
            flag = atom.run();
            sqlSession.commit();
        } catch (Exception e){
            e.printStackTrace();
            sqlSession.rollback();
            throw new RuntimeException("事务出错:"+e);
        } finally{
            sqlSession.close();
        }

        return flag ;
    }


}
