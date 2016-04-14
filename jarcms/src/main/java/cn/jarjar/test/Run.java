package cn.jarjar.test;

import cn.jarjar.core.mybatis.util.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;

public class Run implements Runnable {
    private static String lock = "lock";

    public void run() {
        for (int i = 13; i < 33; i++) {
            SqlSession session = SqlSessionUtil.getSqlSession();
            User user = (User) session.selectOne("cn.jarjar.test.dao.UserMapper.selectById", i);

            synchronized (lock) {
                System.out.println("***********{"+Thread.currentThread().getName()+"}***************");
                System.out.println("index:{"+i+"}, user:{"+user+"}");
                System.out.println("********************************");
 
            }
 
        }
    }
}