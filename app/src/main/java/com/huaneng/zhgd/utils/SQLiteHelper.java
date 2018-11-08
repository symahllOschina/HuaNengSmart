package com.huaneng.zhgd.utils;

import com.huaneng.zhgd.bean.User;
import com.orhanobut.logger.Logger;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import static com.huaneng.zhgd.Constants.DB_NAME;
import static com.huaneng.zhgd.Constants.DB_VERSION;

/**
 * Created by Administrator on 2017/11/11.
 */

public class SQLiteHelper {

    private DbManager db;

    // 静态的daoConfig，保证只初始化一次，多个DbManager共享一个daoConfig
    private static DbManager.DaoConfig daoConfig;

    static {
        daoConfig = new DbManager.DaoConfig()
                .setDbName(DB_NAME)
                .setDbVersion(DB_VERSION)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        // TODO: ...
                        // db.addColumn(...);
                        // db.dropTable(...);
                        // ...
                        // or
                        // db.dropDb();

                        for (int i = oldVersion; i < newVersion; i++) {
                            switch (i) {
                                case 1:
                                    upgradeToVersion2(db);
                                    break;
                                case 2:
                                    break;
                            }
                        }
                    }
                });
    }

    private static void upgradeToVersion2(DbManager db) {
        try {
            db.execNonQuery("alter table user add graduate text, major text, typework text, certificate_no text, blood_type text, job text");
        } catch (Exception e) {
            Logger.e(e, "修改user表出错.");
        }
    }

    public SQLiteHelper() {
        db = x.getDb(daoConfig);
    }

    public static SQLiteHelper create() {
        return new SQLiteHelper();
    }

    public void clear(Class<?> clazz) {
        try {
            db.delete(clazz);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void save(Object entity) {
        try {
            db.save(entity);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public  <T> T getOne(Class<T> clazz) {
        try {
            return db.findFirst(clazz);
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }

}
