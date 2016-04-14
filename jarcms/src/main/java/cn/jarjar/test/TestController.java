package cn.jarjar.test;

import cn.jarjar.core.mybatis.IAtom;
import cn.jarjar.core.mybatis.MyTx;
import cn.jarjar.core.mybatis.util.SqlSessionUtil;
import com.jfinal.core.Controller;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * honta
 * Created by cloudroc on 2016/4/14.
 */
public class TestController extends Controller {

    Logger logger = Logger.getLogger(TestController.class);

    /**
     * 测试SqlSessionUtil.getSqlSession()的正常使用
     */
    public void test1() {

        //查询
        final User user = SqlSessionUtil.getSqlSession().selectOne(
                "cn.jarjar.test.dao.UserMapper.selectByUserName", "admin");

        logger.info("user="+user);

        //插入
        for(int i=0;i<10;i++){
            user.setUsername("admin"+i);
            SqlSessionUtil.getSqlSession().insert("cn.jarjar.test.dao.UserMapper.insert",user);
        }

        //测试编程式事务

        //测试成功提交
        MyTx.tx(new IAtom(){

            public boolean run() throws SQLException {
                //插入
                for(int i=0;i<10;i++){
                    user.setUsername("SUCCESS"+i);
                    SqlSessionUtil.getSqlSession().insert("cn.jarjar.test.dao.UserMapper.insert",user);
                }
                return true;
            }
        });

        //测试失败提交
        MyTx.tx(new IAtom(){

            public boolean run() throws SQLException {
                //插入
                for(int i=0;i<10;i++){
                    user.setUsername("FAIL"+i);
                    SqlSessionUtil.getSqlSession().insert("cn.jarjar.test.dao.UserMapper.insert",user);
                }

                //失败情况
                SqlSessionUtil.getSqlSession().insert("cn.jarjar.test.dao.UserMapper.insertXXXX",user);

                return true;
            }
        });


        render("test1.html");
    }

    /**
     * 测试SqlSessionUtil.getSqlSession()的压力承受情况
     */
    public void test2() throws InterruptedException {

        Run run = new Run();
        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(run);
            threads.add(t);
            System.out.println("thread:{"+t.getName()+"}, start");
            t.start();
        }
        for (Thread t : threads) {
            System.out.println("thread:{"+t.getName()+"},join");
            t.join();
        }

        render("test2.html");
    }
}
