package com.shevchenko.staffapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shevchenko.staffapp.Model.TinTask;

import java.util.ArrayList;

public class AbastecConfirmDialog extends Dialog {
	private Context pContext;
	LinearLayout lnContainer;

	public AbastecConfirmDialog(Context context) {
		super(context, android.R.style.Theme_Black_NoTitleBar);

		// TODO Auto-generated constructor stub
		pContext = context;
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.abastec_confirm_dialog);
		// setTitle("Input Post Information");

		lnContainer = (LinearLayout)findViewById(R.id.lnContainer);

		this.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				// TODO Auto-generated method stub
			}

		});
	}
	public void displayList(ArrayList<TinTask> arrTinTasks){
		for(int i = 0; i < arrTinTasks.size(); i++){
			LinearLayout lnChild = new LinearLayout(pContext);
			final int a = i;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
			params.leftMargin = (int)pContext.getResources().getDimension(R.dimen.space_10);
			params.rightMargin = (int)pContext.getResources().getDimension(R.dimen.space_10);
			params.topMargin = (int)pContext.getResources().getDimension(R.dimen.space_20);
			params.gravity = Gravity.CENTER;
			lnChild.setLayoutParams(params);
			lnChild.setOrientation(LinearLayout.HORIZONTAL);
			lnContainer.addView(lnChild, i);

			TextView txtContent = new TextView(pContext);
			LinearLayout.LayoutParams param_text = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT/*(int) getResources().getDimension(R.dimen.space_40)*/);
			param_text.weight = 70;
			param_text.gravity = Gravity.CENTER_VERTICAL;
			txtContent.setText(arrTinTasks.get(i).nus + ":");
			txtContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, pContext.getResources().getDimension(R.dimen.space_15));
			txtContent.setLayoutParams(param_text);
			txtContent.setTextColor(pContext.getResources().getColor(R.color.clr_graqy));
			lnChild.addView(txtContent);

			final TextView txtQuantity = new TextView(pContext);
			LinearLayout.LayoutParams param_content = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
			param_content.weight = 30;
			param_content.gravity = Gravity.CENTER;
			param_content.leftMargin = (int)pContext.getResources().getDimension(R.dimen.space_3);
			txtQuantity.setPadding((int)pContext.getResources().getDimension(R.dimen.space_5), (int) pContext.getResources().getDimension(R.dimen.space_5), (int) pContext.getResources().getDimension(R.dimen.space_5), (int) pContext.getResources().getDimension(R.dimen.space_5));
			txtQuantity.setGravity(Gravity.CENTER);
			txtQuantity.setLayoutParams(param_content);
			txtQuantity.setId(i + 1);
			txtQuantity.setText(arrTinTasks.get(i).quantity);
			txtQuantity.setBackgroundResource(R.drawable.tineditborder);
			txtQuantity.setTextColor(pContext.getResources().getColor(R.color.clr_graqy));
			txtQuantity.setTextSize(TypedValue.COMPLEX_UNIT_PX, pContext.getResources().getDimension(R.dimen.space_15));
			lnChild.addView(txtQuantity);
		}
	}
}
