# honta
honta project

#2016.4.14 
##1.实现了jfinal集成原生mybatis，以jfinal plugin的方式集成
##2.工具类SqlSessionUtil实现了线程安全的sqlsession的调用，不用使用原mybatis sqlsession先open再close
###使用示例：
User user = SqlSessionUtil.getSqlSession().selectOne("cn.jarjar.test.dao.UserMapper.selectByUserName", "admin");
##3.实现简单的编程式事务模板，能保证执行事务
###使用示例

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
