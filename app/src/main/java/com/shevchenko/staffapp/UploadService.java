package com.shevchenko.staffapp;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.MenuItem;
import android.widget.Toast;

import com.shevchenko.staffapp.Common.Common;
import com.shevchenko.staffapp.Model.CompleteTask;
import com.shevchenko.staffapp.Model.DetailCounter;
import com.shevchenko.staffapp.Model.LogEvent;
import com.shevchenko.staffapp.Model.LogFile;
import com.shevchenko.staffapp.Model.PendingTasks;
import com.shevchenko.staffapp.Model.TinTask;
import com.shevchenko.staffapp.db.DBManager;
import com.shevchenko.staffapp.net.NetworkManager;

import java.io.File;
import java.util.ArrayList;

public class UploadService extends Service {

    public UploadService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            new UploadThread().start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class UploadThread extends Thread {

        @Override
        public void run() {
            postAllPendingTask();
            postAllTinPendingTask();
            postAllDetailCounters();
            postAllLogEvent();
            postAllLogFile();
            Common.getInstance().isUpload = false;
            mHandler_pendingtasks.sendEmptyMessage(0);

        }
    }
    private int postAllLogFile(){
        ArrayList<LogFile> logs = DBManager.getManager().getLogFiles();
        int sum = 0;
        for (int i = 0; i < logs.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postLogFile(logs.get(i));
            if (bRet1) {
                File f = new File(logs.get(i).getFilePath());
                if(f.exists())
                    f.delete();

                DBManager.getManager().deleteLogFile(logs.get(i));
            } else
                return 0;
        }
        return 1;
    }
    private int postAllLogEvent(){
        ArrayList<LogEvent> logs = DBManager.getManager().getLogEvents(Common.getInstance().getLoginUser().getUserId());
        int sum = 0;
        for (int i = 0; i < logs.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postLogEvent(logs.get(i));
            if (bRet1)
                DBManager.getManager().deleteLogEvent(Common.getInstance().getLoginUser().getUserId(), logs.get(i).datetime);
            else
                return 0;
        }
        return 1;
    }
    private int postAllPendingTask() {
        ArrayList<PendingTasks> tasks = DBManager.getManager().getPendingTask(Common.getInstance().getLoginUser().getUserId());
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {
            String[] arrPhotos = new String[]{"", "", "", "", ""};
            int nCurIndex = 0;
            if (!tasks.get(i).file1.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file1;
                nCurIndex++;
            }
            if (!tasks.get(i).file2.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file2;
                nCurIndex++;
            }
            if (!tasks.get(i).file3.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file3;
                nCurIndex++;
            }
            if (!tasks.get(i).file4.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file4;
                nCurIndex++;
            }
            if (!tasks.get(i).file5.equals("")) {
                arrPhotos[nCurIndex] = tasks.get(i).file5;
                nCurIndex++;
            }

            Boolean bRet1 = NetworkManager.getManager().postTask(tasks.get(i).taskid, tasks.get(i).date, tasks.get(i).tasktype, tasks.get(i).RutaAbastecimiento, tasks.get(i).TaskBusinessKey, tasks.get(i).Customer, tasks.get(i).Adress, tasks.get(i).LocationDesc, tasks.get(i).Model, tasks.get(i).latitude, tasks.get(i).longitude, tasks.get(i).epv, tasks.get(i).logLatitude, tasks.get(i).logLongitude, tasks.get(i).ActionDate, tasks.get(i).MachineType, tasks.get(i).Signature, tasks.get(i).NumeroGuia, tasks.get(i).Aux_valor1, tasks.get(i).Aux_valor2, tasks.get(i).Aux_valor3, tasks.get(i).Aux_valor4, tasks.get(i).Aux_valor5, tasks.get(i).Glosa, arrPhotos, nCurIndex, tasks.get(i).Completed, tasks.get(i).Comment, tasks.get(i).Aux_valor6, tasks.get(i).QuantityResumen);
            if (bRet1)
                DBManager.getManager().deletePendingTask(Common.getInstance().getLoginUser().getUserId(), tasks.get(i).taskid);
            else
                return 0;
        }
        return 1;
    }

    private int postAllTinPendingTask() {
        ArrayList<TinTask> tasks = DBManager.getManager().getTinPendingTask(Common.getInstance().getLoginUser().getUserId());
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postTinTask(tasks.get(i));
            if (bRet1)
                DBManager.getManager().deletePendingTinTask(Common.getInstance().getLoginUser().getUserId(), tasks.get(i).taskid);
            else
                return 0;
        }
        return 1;
    }

    private int postAllDetailCounters() {
        ArrayList<DetailCounter> tasks = DBManager.getManager().getDetailCounter();
        int sum = 0;
        for (int i = 0; i < tasks.size(); i++) {

            Boolean bRet1 = NetworkManager.getManager().postDetailCounter(tasks.get(i));
            if (bRet1)
                DBManager.getManager().deleteDetailTask(tasks.get(i).taskid);
            else
                return 0;
        }
        return 1;
    }
    private Handler mHandler_pendingtasks = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            //loadTasks();
            Common.getInstance().isUpload = false;
        }
    };

    private void loadTasks() {
        new Thread(mRunnable_tasks).start();
    }

    private Runnable mRunnable_tasks = new Runnable() {

        @Override
        public void run() {
            Common.getInstance().arrIncompleteTasks_copy.clear();
            Common.getInstance().arrCompleteTasks_copy.clear();
            Common.getInstance().arrCategory_copy.clear();
            Common.getInstance().arrProducto_copy.clear();
            Common.getInstance().arrPendingTasks_copy.clear();
            Common.getInstance().arrCompleteTinTasks_copy.clear();
            Common.getInstance().arrTinTasks_copy.clear();
            //int nRet = NetworkManager.getManager().loadTasks(Common.getInstance().arrIncompleteTasks_copy, Common.getInstance().arrCompleteTasks_copy, Common.getInstance().arrCompleteTinTasks_copy);
            //NetworkManager.getManager().loadCategory(Common.getInstance().arrCategory_copy);
            //NetworkManager.getManager().loadProducto(Common.getInstance().arrProducto_copy);
            Common.getInstance().arrPendingTasks_copy = DBManager.getManager().getPendingTask(Common.getInstance().getLoginUser().getUserId());
            Common.getInstance().arrTinTasks_copy = DBManager.getManager().getTinPendingTask(Common.getInstance().getLoginUser().getUserId());
            //mHandler_task.sendEmptyMessage(nRet);
        }
    };
    private Handler mHandler_task = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            //DialogSelectOption2();
            Intent intent = new Intent(UploadService.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("position", 0);
            intent.putExtra("service", 1);
            startActivity(intent);
        }
    };
    private void DialogSelectOption2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Refresh the screen.")
                .setMessage("Loading the tasks was completed. Please refresh the app screen.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(UploadService.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("position", 0);
                        startActivity(intent);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
