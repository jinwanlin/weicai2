package com.weicai.daoCore;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
 
 
/**
 * @author jiuwuerliu@sina.com
 * 
 */
@SuppressLint("SimpleDateFormat")
public class SqliteDAO{
    static final String tag="DAO";
     
    /**
     * 访问的数据库
     */
    private SQLiteDatabase db;
     
    /**
     * 数据插入冲突处理方式:
     * 0-忽略
     * 1-抛出异常
     * 2-替换数据
     */
    private int conflictType=2;
     
    public SqliteDAO(SQLiteDatabase db){
        this.db=db;        
    }
  
    public SQLiteDatabase getSQLiteDatabase(){
        return db;
    }
     
    /**
     * 插入对象到数据库, 存储对象的所有字段到数据库的对应字段,包括NULL字段.
     * @param entity  待插入的对象
     * @return 如果插入数据库成功则返回该对象,否则返回NULL
     */
    public <T> T insert(T entity){
        return insert(entity,false);
    }
     
    /**
     * 插入对象到数据库, 仅存储对象的非空字段到数据库,对象的NULL字段将被忽略.
     * @param entity 待插入的对象
     * @return 如果插入数据库成功则返回该对象,否则返回NULL
     */
    public <T> T insertSelective(T entity){
        return insert(entity,true);
    }
     
     
    private <T> T insert(T entity,boolean selective){
        ContentValues values=getContentValues(entity,selective);
		Log.i(tag, "insert id: "+ values.getAsInteger("id")+",  name:"+values.getAsString("name"));
        T exist_obj=this.loadByPrimaryKey(entity);
        if(exist_obj!=null){
            return exist_obj;
        }
          
        long r=0;
        if(conflictType==2){
            r=db.replace(getTableName(entity), null,values);           
        }else{
            r=db.insert(getTableName(entity), null, values);            
        }
         
        if(r>=0){
            return entity;
        }
          
        return null;               
    }
     
    /**
     * 根据主键删除数据
     * @param entity 待删除的对象, 主键只必须设置.
     * @return
     */
    public <T> int delete(T entity){
        Object[] args=getPrimarySelectionAndArgs(entity);
        return db.delete(getTableName(entity), (String)args[0], (String[])args[1]);    
    }
     
     
    /**
     * 根据主键从数据库载入一条记录到对象
     * @param entity 数据实体(必须初始化主键字段)
     * @return 成功则返回的该数据库实体,失败则返回NULL
     */
    public <T> T loadByPrimaryKey(T entity){
        Object[] args=getPrimarySelectionAndArgs(entity);
        Cursor cursor=db.query(getTableName(entity), null,(String)args[0],(String[])args[1], null,null,null);
        try{
            if(cursor.moveToNext()){
                T db_entity=getEntity(cursor,entity);
                return db_entity;
            }else{
                return null;
            }
        }finally{
            cursor.close();
        }
    }
    
    /**
     * 根据主键从数据库载入一条记录到对象
     * @param entity 数据实体(必须初始化主键字段)
     * @return 成功则返回的该数据库实体,失败则返回NULL
     */
    public <T> T getByPrimaryKey(T entity, String id){
        Object[] args=getPrimarySelectionAndArgs(entity);
        Cursor cursor=db.query(getTableName(entity), null,(String)args[0],(String[])args[1], null,null,null);
        try{
            if(cursor.moveToNext()){
                T db_entity=getEntity(cursor,entity);
                return db_entity;
            }else{
                return null;
            }
        }finally{
            cursor.close();
        }
    }
     
     
    public <T> List<T> loadAll(Class<T> entity,String orderBy){   
        List<T> entities=new ArrayList<T>();
        Cursor cursor = db.query(getTableName(entity), null,null,null, null,null,orderBy);
//        Log.i(tag, "product size: "+ cursor.getCount());
        try{
            if(cursor!=null && cursor.moveToNext()){
            	do{
                    T obj=(T)entity.newInstance();
                    getEntity(cursor,obj);
                    entities.add(obj);
                }while(cursor.moveToNext());
            }
            return entities;
        }catch(Exception e){
            Log.e(tag,""+e, e);
            return entities;
        }finally{
            cursor.close();
        }
    }

    public <T> long countAll(Class<T> entity){
    	long count = 0;
        Cursor c = db.rawQuery("select count(*) from " + getTableName(entity), null);
        if (c.moveToNext()) {
        	count = c.getLong(0);
        }
        return count;
    }
     
    /**
     * 更新数据库实体,  更新对象的所有字段到数据库的对应字段,包括NULL字段.
     * @param entity 待更新的对象(必须包含主键)
     * @return 成功更新的记录数
     */
    public int updateByPrimaryKey(Object entity){
        return updateByPrimaryKey(entity,false);
    }
     
    /**
     * 更新数据库实体,  仅更新对象的非空字段到数据库的对应字段,对象的NULL字段将被忽略.
     * @param entity 待更新的对象(必须包含主键)
     * @return 成功更新的记录数
     */
    public int updateByPrimaryKeySelective(Object entity){
        return updateByPrimaryKey(entity,true);
    }
     
    private int updateByPrimaryKey(Object entity,boolean selective){
        ContentValues values=getContentValues(entity,selective);
        Object[] args=getPrimarySelectionAndArgs(entity);
         
        int r=db.update(getTableName(entity), values, (String)args[0],(String[])args[1]);
         
        return r;
    }
     
    /**
     * 从对象中解析出主键字段, 以及主键字段对应的值
     * @param entity
     * @return
     */
    private Object[] getPrimarySelectionAndArgs(Object entity){
        Object[] ret=new Object[2];
        String selection=null;
        List<String> args=new ArrayList<String>();
        try{
            Class<?> entity_class=entity.getClass();         
            Field[] fs=entity_class.getDeclaredFields();
            for(Field f:fs){
                if(isPrimaryKey(f)){               
                    Method get=getGetMethod(entity_class,f);
                    if(get!=null){
                        Object o=get.invoke(entity);                       
                        String value=null;
                        if(o!=null){
                            value=o.toString();
                            if(selection==null){
                                selection=f.getName()+"=?";                            
                            }else{
                                selection+=" AND "+f.getName()+"=?";
                            }
                             
                            args.add(value);
                             
                        }else{
                            throw new RuntimeException("Primary key: "+f.getName()+" must not be null");
                        }
                    }
                }
            }          
            if(selection==null){
                throw new RuntimeException("Primary key not found!");
            }
             
            ret[0]=selection;
            ret[1]=args.toArray(new String[args.size()]);
            return ret;
        }catch(Exception e){
            throw new RuntimeException(e.getMessage(),e);
        }
    }
      
    /**
     * 将对象转换为ContentValues
     * @param entity
     * @param selective
     * @return
     */
    private ContentValues getContentValues(Object entity,boolean selective){
        ContentValues values=new ContentValues();
        try{
            Class<?> entity_class=entity.getClass();         
            Field[] fs=entity_class.getDeclaredFields();
            for(Field f:fs){
                if(isTransient(f)==false){             
                    Method get=getGetMethod(entity_class,f);
                    if(get!=null){
                        Object o=get.invoke(entity);
                        if(!selective || (selective && o!=null)){
                            String name=f.getName();                       
                            Class<?> type=f.getType();
                            if(type==String.class){
                                values.put(name,(String)o);
                            }else if(type==int.class || type==Integer.class){
                                values.put(name,(Integer)o);
                            }else if(type==float.class || type==Float.class){
                                values.put(name,(Float)o);
                            }else if(type==double.class || type==Double.class){
                                values.put(name,(Double)o);
                            }else if(type==long.class || type==Long.class){
                                values.put(name,(Long)o);
                            }else if(type==Date.class){
                                values.put(name,datetimeToString((Date)o));
                            }else{ 
                                values.put(name,o.toString());                             
                            }
                        }
                    }
                }
            }
            return values;
        }catch(Exception e){
            throw new RuntimeException(e.getMessage(),e);
        }
    }
     
    /**
     * 将数据库记录转换为对象
     *
     * @param cursor
     * @param entity
     * @return
     */
    private <T> T getEntity(Cursor cursor, T entity){
        try{
            Class<?> entity_class=entity.getClass();
             
            Field[] fs=entity_class.getDeclaredFields();
            for(Field f:fs){
                int index=cursor.getColumnIndex(f.getName());
                if(index>=0){                   
                    Method set=getSetMethod(entity_class,f);
                    if(set!=null){
                        String value=cursor.getString(index);                                          
                        if(cursor.isNull(index)){
                            value=null;
                        }
                        Class<?> type=f.getType();
                        if(type==String.class){
                            set.invoke(entity,value);
                        }else if(type==int.class || type==Integer.class){
                            set.invoke(entity,value==null?(Integer)null:Integer.parseInt(value));
                        }else if(type==float.class || type==Float.class){
                            set.invoke(entity,value==null?(Float)null:Float.parseFloat(value));
                        }else if(type==double.class || type==Double.class){
                            set.invoke(entity,value==null?(Double)null:Double.parseDouble(value));
                        }else if(type==long.class || type==Long.class){
                            set.invoke(entity,value==null?(Long)null:Long.parseLong(value));
                        }else if(type==Date.class){
                            set.invoke(entity,value==null?(Date)null:stringToDateTime(value));
                        }else{ 
                            set.invoke(entity,value);                      
                        }                           
                    }
                }
            }          
            return entity;
        }catch(Exception e){
            throw new RuntimeException(e.getMessage(),e);
        }
    }
     
    private String datetimeToString(Date d){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(d!=null){
            return sdf.format(d);
        }
        return null;
    }
     
    private Date stringToDateTime(String s){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(s!=null){
            try {
                return sdf.parse(s);
            } catch (ParseException e) {            
                Log.e(tag,"解析时间错误: "+s,e);
            }
        }
        return null;
    }
     
    private Method getGetMethod(Class<?> entity_class,Field f){
        String fn=f.getName();
        String mn="get"+fn.substring(0,1).toUpperCase()+fn.substring(1);
        try{           
            return entity_class.getDeclaredMethod(mn);
        }catch(NoSuchMethodException e){
            Log.w(tag,"Method: "+mn+" not found.");
             
            return null;
        }
    }
     
    private Method getSetMethod(Class<?> entity_class,Field f){
        String fn=f.getName();
        String mn="set"+fn.substring(0,1).toUpperCase()+fn.substring(1);
        try{           
            return entity_class.getDeclaredMethod(mn,f.getType());
        }catch(NoSuchMethodException e){
            Log.w(tag,"Method: "+mn+" not found.");
             
            return null;
        }
    }
      
    /**
     * 检查是否为主键字段
     */
    private boolean isPrimaryKey(Field f){      
        Annotation an=f.getAnnotation(Id.class);
        if(an!=null){
            return true;            
        }
        return false;
    }
     
    private boolean isTransient(Field f){       
        Annotation an=f.getAnnotation(Transient.class);
        if(an!=null){
            return true;            
        }
        return false;
    }
     
     
 
    private String getTableName(Object entity){
        Table table=entity.getClass().getAnnotation(Table.class);
        String name= table.name();
        return name;
    }
    
    private String getTableName(Class<?> entity){
        Table table=entity.getAnnotation(Table.class);
        String name= table.name();
        return name;
    }
     
    public int getConflictType() {
        return conflictType;
    }
 
    public void setConflictType(int conflictType) {
        this.conflictType = conflictType;
    }
     
     
}