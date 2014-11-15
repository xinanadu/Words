package info.zhegui.words;

import java.util.Timer;
import java.util.TimerTask;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLiteOpenHelper是一个辅助类，用来管理数据库的创建和版本他，它提供两个方面的功能
 * 第一，getReadableDatabase()、getWritableDatabase
 * ()可以获得SQLiteDatabase对象，通过该对象可以对数据库进行操作
 * 第二，提供了onCreate()、onUpgrade()两个回调函数，允许我们再创建和升级数据库时，进行自己的操作
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private Context mContext;

	private static final int VERSION = 1;

	private final static String DB_NAME = "database";
	public final static String TBL_NAME = "words";

	public final static String COL_ID = "_id";
	public final static String COL_KEY= "key";
	public final static String COL_REMEMBER = "remember";
	public final static String COL_TIMES= "times";
	public final static String COL_LESSON = "lesson";
    public final static String COL_TYPE = "type";
    
    public static final int REMEMBER=1,FORGET=2;

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		// 必须通过super调用父类当中的构造函数
		super(context, name, factory, version);
		mContext = context;
	}


	public DatabaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DatabaseHelper(Context context) {
		this(context, DB_NAME, VERSION);
	}

	// 该函数是在第一次创建的时候执行，实际上是第一次得到SQLiteDatabase对象的时候才会调用这个方法
	@Override
	public void onCreate(SQLiteDatabase db) {
		log("onCreate()");
		// execSQL用于执行SQL语句
		createTable(db);
	}

	private void createTable(SQLiteDatabase db) {
        log("createTable()");
		String sql = "create table " + TBL_NAME + "(";
		sql += COL_ID + " integer primary key autoincrement,";
		sql += COL_KEY + " text,";
		sql += COL_REMEMBER + " text,";
		sql += COL_TIMES + " text,";
        sql += COL_LESSON + " text,";
		sql += COL_TYPE + " text";
		sql += ")";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + TBL_NAME);
		createTable(db);
	}

	public long insert(Word word) {
		SQLiteDatabase sqliteDatabase = null;
		try {

			ContentValues values = new ContentValues();
			values.put(COL_KEY, word.key);
            values.put(COL_TIMES, word.times);
			values.put(COL_REMEMBER, word.remember?REMEMBER:FORGET);
			values.put(COL_LESSON, word.lesson);
			values.put(COL_TYPE,word.type);
			sqliteDatabase = this.getWritableDatabase();
			long line = sqliteDatabase.insert(TBL_NAME, null, values);
			sqliteDatabase.close();
			return line;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (sqliteDatabase != null) {
				sqliteDatabase.close();
				sqliteDatabase = null;
			}
		}

		return 0;
	}

	public Word query(String wordKey) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		Word word = null;
		try {
			db = this.getWritableDatabase();
			cursor = db.query(DatabaseHelper.TBL_NAME, null,
					DatabaseHelper.COL_KEY + " = ? ",
					new String[] { wordKey }, null, null, null);
			if (cursor.moveToNext()) {
				long id = cursor.getLong(cursor
						.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
				int remember = cursor.getInt(cursor
						.getColumnIndexOrThrow(DatabaseHelper.COL_REMEMBER));
				int lesson = cursor.getInt(cursor
						.getColumnIndexOrThrow(DatabaseHelper.COL_LESSON));
				int times  = cursor.getInt(cursor
						.getColumnIndexOrThrow(DatabaseHelper.COL_TIMES));
				String type = cursor.getString(cursor
						.getColumnIndexOrThrow(DatabaseHelper.COL_TYPE));
				word = new Word((int) id,wordKey, remember==REMEMBER?true:false,times,lesson,type);
			}
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			if (db != null) {
				db.close();
				db.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			if (db != null) {
				db.close();
				db.close();
			}
		}

		return word;
	}

	public long update(Word word, int wordId) {
		SQLiteDatabase sqliteDatabase = null;
		try {

			ContentValues values = new ContentValues();
			values.put(COL_KEY, word.key);
			values.put(COL_TYPE, word.type);
			values.put(COL_TIMES, word.times);
			values.put(COL_LESSON, word.lesson);
			values.put(COL_REMEMBER, word.remember);

			sqliteDatabase = this.getWritableDatabase();

			int line = sqliteDatabase.updateWithOnConflict(TBL_NAME, values,
					COL_ID + "=?", new String[] { wordId + "" },
					SQLiteDatabase.CONFLICT_REPLACE);

			sqliteDatabase.close();

			return line;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (sqliteDatabase != null) {
				sqliteDatabase.close();
				sqliteDatabase = null;
			}
		}
		return 0;

	}

	private void log(String msg) {
		Log.e(this.getClass().getSimpleName(), msg);
	}
}
