package com.xmrbi.unware.component.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.xmrbi.unware.data.entity.main.StoreHouse;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "STORE_HOUSE".
*/
public class StoreHouseDao extends AbstractDao<StoreHouse, Long> {

    public static final String TABLENAME = "STORE_HOUSE";

    /**
     * Properties of entity StoreHouse.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UseunitId = new Property(1, Long.class, "useunitId", false, "USEUNIT_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property LesseeId = new Property(3, Long.class, "lesseeId", false, "LESSEE_ID");
        public final static Property IsRfid = new Property(4, Boolean.class, "isRfid", false, "IS_RFID");
    }


    public StoreHouseDao(DaoConfig config) {
        super(config);
    }
    
    public StoreHouseDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"STORE_HOUSE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"USEUNIT_ID\" INTEGER," + // 1: useunitId
                "\"NAME\" TEXT," + // 2: name
                "\"LESSEE_ID\" INTEGER," + // 3: lesseeId
                "\"IS_RFID\" INTEGER);"); // 4: isRfid
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"STORE_HOUSE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, StoreHouse entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long useunitId = entity.getUseunitId();
        if (useunitId != null) {
            stmt.bindLong(2, useunitId);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        Long lesseeId = entity.getLesseeId();
        if (lesseeId != null) {
            stmt.bindLong(4, lesseeId);
        }
 
        Boolean isRfid = entity.getIsRfid();
        if (isRfid != null) {
            stmt.bindLong(5, isRfid ? 1L: 0L);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, StoreHouse entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long useunitId = entity.getUseunitId();
        if (useunitId != null) {
            stmt.bindLong(2, useunitId);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        Long lesseeId = entity.getLesseeId();
        if (lesseeId != null) {
            stmt.bindLong(4, lesseeId);
        }
 
        Boolean isRfid = entity.getIsRfid();
        if (isRfid != null) {
            stmt.bindLong(5, isRfid ? 1L: 0L);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public StoreHouse readEntity(Cursor cursor, int offset) {
        StoreHouse entity = new StoreHouse( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // useunitId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3), // lesseeId
            cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0 // isRfid
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, StoreHouse entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUseunitId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setLesseeId(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
        entity.setIsRfid(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(StoreHouse entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(StoreHouse entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(StoreHouse entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}