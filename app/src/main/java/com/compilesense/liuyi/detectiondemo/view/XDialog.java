package com.compilesense.liuyi.detectiondemo.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.compilesense.liuyi.detectiondemo.R;


/**
 * 加载中Dialog
 * 
 * @author xm
 */
public class XDialog extends AlertDialog {

	private TextView tips_loading_msg;
	private ProgressBar tips_loading_img;

	Context mContext;

	private String message = null;

	public XDialog(Context context) {
		super(context);
		message = "加载中...";
		mContext = context;
	}

	public XDialog(Context context, String message) {
		super(context);
		this.message = message;
		mContext = context;
		this.setCancelable(false);
	}

	public XDialog(Context context, int theme, String message) {
		super(context, theme);
		this.message = message;
		mContext = context;
		this.setCancelable(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.xdg_loading);
		tips_loading_img = (ProgressBar) findViewById(R.id.progressbar);
		tips_loading_msg = (TextView) findViewById(R.id.tips_loading_msg);

		if (TextUtils.isEmpty(message)) {
			tips_loading_msg.setVisibility(View.GONE);
		} else {
			tips_loading_msg.setText(this.message);
		}
		// // 加载动画
		// Animation loadingAnim = AnimationUtils.loadAnimation(mContext,
		// R.anim.xdg_loading);
		// // 使用ImageView显示动画
		// tips_loading_img.startAnimation(loadingAnim);

	}

	public void setText(String message) {
		this.message = message;
		tips_loading_msg.setText(this.message);
	}

	public void setText(int resId) {
		setText(getContext().getResources().getString(resId));
	}

}
