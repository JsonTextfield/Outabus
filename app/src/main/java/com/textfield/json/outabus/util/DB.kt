package com.textfield.json.outabus.util

import java.io.IOException

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class DB(mContext: Context) {
    private var mDb: SQLiteDatabase? = null
    private val mDbHelper: DBHelper = DBHelper(mContext)

    @Throws(SQLException::class)
    fun createDatabase(): DB {
        try {
            mDbHelper.createDataBase()
        } catch (mIOException: IOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase")
            throw Error("UnableToCreateDatabase")
        }

        return this
    }

    @Throws(SQLException::class)
    fun open(): DB {
        try {
            mDbHelper.openDataBase()
            mDbHelper.close()
            mDb = mDbHelper.readableDatabase
        } catch (mSQLException: SQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString())
            throw mSQLException
        }

        return this
    }

    fun close() {
        mDbHelper.close()
    }

    fun runQuery(sql: String): Cursor? {
        println(sql)
        try {
            //String sql ="select routenum from routes order by route_id*1 asc;";
            //String sql ="select name from stops group by name;";
            //String sql ="select routenum,name,direction from busroutes natural join stops natural join routes group by stop_id,routenum,direction;";

            val mCur = mDb!!.rawQuery(sql, null)
            mCur?.moveToNext()
            return mCur
        } catch (mSQLException: SQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString())
            throw mSQLException
        }

    }

    companion object {
        protected val TAG = "DataAdapter"
    }
}