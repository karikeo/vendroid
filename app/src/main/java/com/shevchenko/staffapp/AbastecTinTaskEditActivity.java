package com.shevchenko.staffapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapsInitializer;
import com.shevchenko.staffapp.Common.Common;
import com.shevchenko.staffapp.Model.LocationLoader;
import com.shevchenko.staffapp.Model.Producto;
import com.shevchenko.staffapp.Model.TaskInfo;
import com.shevchenko.staffapp.Model.TinTask;
import com.shevchenko.staffapp.db.DBManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Pattern;

public class AbastecTinTaskEditActivity extends Activity implements View.OnClickListener {

    private ComponentName mService;
    private int nTaskID;
    private ArrayList<Producto> currentProductos = new ArrayList<Producto>();
    LocationLoader mLocationLoader;
    private Location mNewLocation;
    private Boolean isEnter = false;
    private TextView txtNus;
    private EditText edtContent;
    LinearLayout lnContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abastectinedit);
        txtNus = (TextView)findViewById(R.id.txtNus);
        txtNus.setText(getIntent().getStringExtra("nus"));
        edtContent = (EditText)findViewById(R.id.edtContent);
        if(getIntent().getStringExtra("quantity").equals("0"))
            edtContent.setText("");
        else
            edtContent.setText(getIntent().getStringExtra("quantity"));
        edtContent.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtContent.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtContent, InputMethodManager.SHOW_IMPLICIT);

        edtContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER){
                    Common.getInstance().selectedQuantity = edtContent.getText().toString();
                    Common.getInstance().selectedNus = txtNus.getText().toString();
                    //addPendingTask();
                    onBackPressed();
                    return true;
                }
                return false;
            }
        });

        Common.getInstance().selectedNus = "";
        Common.getInstance().selectedQuantity = "";

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected InputFilter filterNum = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }

    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent;
        switch (v.getId()) {
            case R.id.btnSendForm:
                setService("The user clicks the Send Form Button");
                Common.getInstance().selectedQuantity = edtContent.getText().toString();
                Common.getInstance().selectedNus = txtNus.getText().toString();
                //addPendingTask();
                onBackPressed();
                break;
            case R.id.btnBack:
                setService("The user clicks the Volver Button");
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            Intent i = new Intent();
            i.setComponent(mService);
            stopService(i);
        }
    }
    private void loadProductos() {
        String strData = getSharedPreferences(Common.PREF_KEY_TEMPSAVE, MODE_PRIVATE).getString(Common.PREF_KEY_TEMPSAVE_ABASTEC + nTaskID, "");
        String[] arrData = strData.split(";");
        for (int i = 0; i < currentProductos.size(); i++) {
            LinearLayout lnChild = new LinearLayout(AbastecTinTaskEditActivity.this);
            final int a = i;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = (int) getResources().getDimension(R.dimen.space_10);
            params.rightMargin = (int) getResources().getDimension(R.dimen.space_10);
            params.topMargin = (int) getResources().getDimension(R.dimen.space_20);
            params.gravity = Gravity.CENTER;
            lnChild.setLayoutParams(params);
            lnChild.setOrientation(LinearLayout.HORIZONTAL);
            lnContainer.addView(lnChild, i);

            TextView txtContent = new TextView(AbastecTinTaskEditActivity.this);
            LinearLayout.LayoutParams param_text = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT/*(int) getResources().getDimension(R.dimen.space_40)*/);
            param_text.weight = 70;
            param_text.gravity = Gravity.CENTER_VERTICAL;
            txtContent.setText(currentProductos.get(i).nus + ":");
            txtContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.space_15));
            txtContent.setLayoutParams(param_text);
            txtContent.setTextColor(getResources().getColor(R.color.clr_graqy));
            lnChild.addView(txtContent);

            final TextView txtQuantity = new TextView(AbastecTinTaskEditActivity.this);
            LinearLayout.LayoutParams param_content = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            param_content.weight = 30;
            param_content.gravity = Gravity.CENTER;
            param_content.leftMargin = (int) getResources().getDimension(R.dimen.space_3);
            txtQuantity.setPadding((int) getResources().getDimension(R.dimen.space_5), (int) getResources().getDimension(R.dimen.space_5), (int) getResources().getDimension(R.dimen.space_5), (int) getResources().getDimension(R.dimen.space_5));
            txtQuantity.setGravity(Gravity.CENTER);
            txtQuantity.setLayoutParams(param_content);
            txtQuantity.setId(i + 1);
            if(i < arrData.length) {
                txtQuantity.setText(arrData[i]);
            }else
                txtQuantity.setText("0");
            txtQuantity.setBackgroundResource(R.drawable.tineditborder);
            txtQuantity.setTextColor(getResources().getColor(R.color.clr_graqy));
            txtQuantity.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.space_15));
            lnChild.addView(txtQuantity);
            lnChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
    public void setService(String description) {

        Intent service = new Intent(AbastecTinTaskEditActivity.this, LogService.class);
        service.putExtra("userid", Common.getInstance().getLoginUser().getUserId());
        service.putExtra("taskid", String.valueOf(nTaskID));
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        service.putExtra("datetime", time);
        service.putExtra("description", description);
        service.putExtra("latitude", Common.getInstance().latitude);
        service.putExtra("longitude", Common.getInstance().longitude);
        mService = startService(service);
    }
}
