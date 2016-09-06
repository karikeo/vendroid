package com.shevchenko.staffapp.Common;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;

import com.shevchenko.staffapp.Model.Category;
import com.shevchenko.staffapp.Model.CompleteDetailCounter;
import com.shevchenko.staffapp.Model.CompleteTask;
import com.shevchenko.staffapp.Model.CompltedTinTask;
import com.shevchenko.staffapp.Model.DetailCounter;
import com.shevchenko.staffapp.Model.LoginUser;
import com.shevchenko.staffapp.Model.MachineCounter;
import com.shevchenko.staffapp.Model.PendingTasks;
import com.shevchenko.staffapp.Model.Report;
import com.shevchenko.staffapp.Model.TaskInfo;
import com.shevchenko.staffapp.Model.TaskType;
import com.shevchenko.staffapp.Model.TinTask;
import com.shevchenko.staffapp.Model.Producto;
import com.shevchenko.staffapp.Model.Producto_RutaAbastecimento;
import com.shevchenko.staffapp.Model.User;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by shevchenko on 2015-11-26.
 */
public class Common {
    private static Common s_instance = null;

    public String gBatteryPercent;
    public boolean gChargingUSB;
    public boolean gChargingOther;

    public static Common getInstance() {
        if (s_instance == null) {
            s_instance = new Common();

        }
        synchronized (s_instance) {
            return s_instance;
        }
    }

    public final static String PREF_KEY_TEMPSAVE = "TEMPSAVE";
    public final static String PREF_KEY_TEMPSAVE_ABASTEC = "TEMPSAVE::ABASTEC";
    public final static String PREF_KEY_TEMPSAVE_CONTADORES = "TEMPSAVE::CONTADORES";
    public final static String PREF_KEY_TEMPSAVE_RECAUDAR = "TEMPSAVE::RECAUDAR";

    public final static String PREF_KEY_CLOSEDTIME = "CLOSEDTIME";

    public final static String PREF_KEY_LATEST_LAT = "LATEST::LAT";
    public final static String PREF_KEY_LATEST_LNG = "LATEST::LNG";

    public ArrayList<TaskInfo> arrIncompleteTasks;
    public ArrayList<CompleteTask> arrCompleteTasks;
    public ArrayList<PendingTasks> arrPendingTasks;
    public ArrayList<TinTask> arrTinTasks;
    public ArrayList<CompltedTinTask> arrCompleteTinTasks;
    public ArrayList<Category> arrCategory;
    public ArrayList<Producto> arrProducto;
    public ArrayList<Producto_RutaAbastecimento> arrProducto_Ruta;
    public ArrayList<User> arrUsers;
    public ArrayList<TaskType> arrTaskTypes;
    public ArrayList<MachineCounter> arrMachineCounters;
    public ArrayList<TaskInfo> arrIncompleteTasks_copy;
    public ArrayList<CompleteTask> arrCompleteTasks_copy;
    public ArrayList<PendingTasks> arrPendingTasks_copy;
    public ArrayList<CompltedTinTask> arrCompleteTinTasks_copy;
    public ArrayList<TinTask> arrTinTasks_copy;
    public ArrayList<Category> arrCategory_copy;
    public ArrayList<Producto> arrProducto_copy;
    public String server_host = "http://vex.cl/Upload/";
    public boolean isUpload = false;
    public String latitude;
    public String longitude;
    public String signaturePath;
    public ArrayList<TinTask> arrAbastTinTasks;
    public ArrayList<DetailCounter> arrDetailCounters;
    public ArrayList<CompleteDetailCounter> arrCompleteDetailCounters;
    public ArrayList<Report> arrReports;
    public boolean isAbastec = false;
    public boolean isNeedRefresh = false;
    public boolean dayly = false;
    public String selectedNus;
    public String selectedQuantity;
    public boolean capture;
    public Common()
    {
        arrIncompleteTasks = new ArrayList<TaskInfo>();
        arrCompleteTasks = new ArrayList<CompleteTask>();
        arrPendingTasks = new ArrayList<PendingTasks>();
        arrTinTasks = new ArrayList<TinTask>();
        arrCompleteTinTasks = new ArrayList<CompltedTinTask>();
        arrCategory = new ArrayList<Category>();
        arrProducto = new ArrayList<Producto>();
        arrProducto_Ruta = new ArrayList<Producto_RutaAbastecimento>();
        arrUsers = new ArrayList<User>();
        arrTaskTypes = new ArrayList<TaskType>();
        arrMachineCounters = new ArrayList<MachineCounter>();
        arrIncompleteTasks_copy = new ArrayList<TaskInfo>();
        arrCompleteTasks_copy = new ArrayList<CompleteTask>();
        arrPendingTasks_copy = new ArrayList<PendingTasks>();
        arrCompleteTinTasks_copy = new ArrayList<CompltedTinTask>();
        arrCategory_copy = new ArrayList<Category>();
        arrProducto_copy = new ArrayList<Producto>();
        arrTinTasks_copy = new ArrayList<TinTask>();
        latitude = new String();
        longitude = new String();
        signaturePath = new String();
        arrAbastTinTasks = new ArrayList<TinTask>();
        arrDetailCounters = new ArrayList<DetailCounter>();
        arrCompleteDetailCounters = new ArrayList<CompleteDetailCounter>();
        gBatteryPercent = "Unknown";
        gChargingUSB = false;
        gChargingOther = false;
        arrReports = new ArrayList<Report>();
        isAbastec = false;
    }
    /*private String UserID;
    public  void setUserID(String userID)
    {
        UserID = userID;
    }
    public String getUserID()
    {
        return UserID;
    }*/
    private LoginUser loginUser;

    public LoginUser getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(LoginUser loginUser) {
        this.loginUser = loginUser;
    }

    public boolean isPendingTaks(int taskid)
    {
        boolean bRet = false;
        for(int i = 0; i < arrPendingTasks.size(); i++)
        {
            PendingTasks task = arrPendingTasks.get(i);
            if(task.taskid == taskid)
            {
                bRet = true;
                break;
            }
        }
        return bRet;
    }

    public static String getPathFromUri(Context context, Uri uri) {
        try {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String strSrcPath = cursor.getString(columnIndex);
            cursor.close();

            return strSrcPath;
        } catch(Exception e) {
        }
        return null;
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return formatSize(totalBlocks * blockSize);
    }

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
        } else {
            return "";
        }
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize);
        } else {
            return "";
        }
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }
}
