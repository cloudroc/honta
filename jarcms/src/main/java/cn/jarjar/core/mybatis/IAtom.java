package cn.jarjar.core.mybatis;

import java.sql.SQLException;

/**
 * honta
 * Created by cloudroc on 2016/4/14.
 */
public interface IAtom {
    boolean run () throws SQLException;
}
