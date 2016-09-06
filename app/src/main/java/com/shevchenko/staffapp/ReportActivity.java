package com.shevchenko.staffapp;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapsInitializer;
import com.shevchenko.staffapp.Common.Common;
import com.shevchenko.staffapp.Model.CompleteDetailCounter;
import com.shevchenko.staffapp.Model.CompleteTask;
import com.shevchenko.staffapp.Model.CompltedTinTask;
import com.shevchenko.staffapp.Model.DetailCounter;
import com.shevchenko.staffapp.Model.GpsInfo;
import com.shevchenko.staffapp.Model.LocationLoader;
import com.shevchenko.staffapp.Model.LogFile;
import com.shevchenko.staffapp.Model.MachineCounter;
import com.shevchenko.staffapp.Model.MenuItemButton;
import com.shevchenko.staffapp.Model.MenuListAdapter;
import com.shevchenko.staffapp.Model.PendingTasks;
import com.shevchenko.staffapp.Model.Producto;
import com.shevchenko.staffapp.Model.Report;
import com.shevchenko.staffapp.Model.TaskInfo;
import com.shevchenko.staffapp.Model.TinTask;
import com.shevchenko.staffapp.connectivity.AuditManagerJofemarRD;
import com.shevchenko.staffapp.db.DBManager;
import com.shevchenko.staffapp.net.NetworkManager;
import com.shevchenko.staffapp.viewholder.CaptureViewHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Pattern;

public class ReportActivity extends Activity implements View.OnClickListener {

    private ProgressDialog mProgDlgLoading;
    private LinearLayout lnContainer;
    TextView txtTaskCount, txtCompleteTaskCount, txtPendingTaskCount, txtAbastecimiento, txtRecaudacion, txtTotalQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        lnContainer = (LinearLayout) findViewById(R.id.lnContainer);

        txtTaskCount = (TextView) findViewById(R.id.txtTaskCount);
        int taskCount = Common.getInstance().arrIncompleteTasks.size() + Common.getInstance().arrCompleteTasks.size();
        txtTaskCount.setText(String.valueOf(taskCount));

        txtCompleteTaskCount = (TextView) findViewById(R.id.txtCompleteTaskCount);
        txtCompleteTaskCount.setText(String.valueOf(Common.getInstance().arrCompleteTasks.size()));

        txtPendingTaskCount = (TextView) findViewById(R.id.txtPendingTaskCount);
        txtPendingTaskCount.setText(String.valueOf(Common.getInstance().arrIncompleteTasks.size()));

        txtAbastecimiento = (TextView) findViewById(R.id.txtAbastecimiento);
        ArrayList<Integer> arrTaskId = new ArrayList<>();
        if (Common.getInstance().arrCompleteTinTasks.size() > 0)
            arrTaskId.add(Common.getInstance().arrCompleteTinTasks.get(0).taskid);

        boolean equal = false;
        for (int i = 0; i < Common.getInstance().arrCompleteTinTasks.size(); i++) {
            for (int j = 0; j < arrTaskId.size(); j++) {
                if (arrTaskId.get(j) == Common.getInstance().arrCompleteTinTasks.get(i).taskid) {
                    equal = true;
                }
            }
            if(equal == false)
                arrTaskId.add(Common.getInstance().arrCompleteTinTasks.get(i).taskid);
            equal = false;

        }
        txtAbastecimiento.setText(String.valueOf(arrTaskId.size()) + " de " + String.valueOf(Common.getInstance().arrCompleteTasks.size()));

        txtRecaudacion = (TextView) findViewById(R.id.txtRecaudacion);
        ArrayList<String> arrTaskId_rac = new ArrayList<>();
        if (Common.getInstance().arrDetailCounters.size() > 0)
            arrTaskId_rac.add(Common.getInstance().arrDetailCounters.get(0).taskid);

        for (int i = 0; i < Common.getInstance().arrDetailCounters.size(); i++) {
            for (int j = 0; j < arrTaskId_rac.size(); j++) {
                if (!arrTaskId_rac.get(j).equals(Common.getInstance().arrDetailCounters.get(i).taskid)) {
                    arrTaskId_rac.add(Common.getInstance().arrDetailCounters.get(i).taskid);
                    break;
                }
            }
        }
        txtRecaudacion.setText(String.valueOf(arrTaskId_rac.size()) + " de " + String.valueOf(Common.getInstance().arrCompleteTasks.size()));

        txtTotalQuantity = (TextView) findViewById(R.id.txtTotalQuantity);
        int quantity_aba = 0;
        for (int i = 0; i < Common.getInstance().arrCompleteTinTasks.size(); i++) {
            if(!Common.getInstance().arrCompleteTinTasks.get(i).quantity.equals(""))
                quantity_aba += Integer.parseInt(Common.getInstance().arrCompleteTinTasks.get(i).quantity);
        }
        txtTotalQuantity.setText(String.valueOf(quantity_aba));

        mProgDlgLoading = new ProgressDialog(this);
        mProgDlgLoading.setCancelable(false);
        mProgDlgLoading.setTitle("Reporte");
        mProgDlgLoading.setMessage("Loading Now!");
        Common.getInstance().arrReports.clear();
        if (getConnectivityStatus()) {
            mProgDlgLoading.show();
            new Thread(mRunnable_report).start();
        }else{
            Toast.makeText(ReportActivity.this, "Por favor conectese a interne", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean getConnectivityStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }

    private Runnable mRunnable_report = new Runnable() {
        @Override
        public void run() {
            int ret = NetworkManager.getManager().report(Common.getInstance().getLoginUser().getUserId());
            mHandler_report.sendEmptyMessage(ret);

        }
    };
    private Handler mHandler_report = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            mProgDlgLoading.hide();
            if (msg.what == 1) {
                if (Common.getInstance().arrReports.size() > 0) {
                    lnContainer.setVisibility(View.VISIBLE);
                    for (int i = 0; i < Common.getInstance().arrReports.size(); i++) {
                        View v = LayoutInflater.from(ReportActivity.this).inflate(R.layout.report_item, null);
                        ((TextView) v.findViewById(R.id.txtNus)).setText(Common.getInstance().arrReports.get(i).nus);
                        ((TextView) v.findViewById(R.id.txtQuantity)).setText(Common.getInstance().arrReports.get(i).quantity);
                        lnContainer.addView(v);
                    }
                }
            } else if (msg.what == 0) {
                Toast.makeText(ReportActivity.this, "Getting report was failed!!!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        }
    }
}
