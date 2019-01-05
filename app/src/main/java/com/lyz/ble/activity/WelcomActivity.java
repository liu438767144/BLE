package com.lyz.ble.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lyz.ble.R;

/**
 * 欢迎界面
 * 
 * 
 */
public class WelcomActivity extends BaseActivity {
	Handler handler = new Handler();
	TextView tx ;
	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcom_activity);
		
		tx = (TextView)findViewById(R.id.textView2);
		tx.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 获取手机本地的蓝牙适配器
				final BluetoothManager bluetoothManager = (BluetoothManager) WelcomActivity.this.getSystemService(Context.BLUETOOTH_SERVICE);
				// 蓝牙适配器
				BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
				// 打开蓝牙权限
				if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
				{
					Intent enableBtIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, 1);
				} else {
					Intent intent = new Intent(WelcomActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
		init_ble();
	}
	
	
	/**
	 * @Title: init_ble
	 * @Description: TODO(初始化蓝牙)
	 * @return void
	 * @throws
	 */
	@SuppressLint("NewApi")
	private void init_ble()
	{
		// 手机硬件支持蓝牙
		if (!this.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE))
		{
			Toast.makeText(this, "不支持BLE", Toast.LENGTH_SHORT).show();
			finish();
		}
		// Initializes Bluetooth adapter.
		// 获取手机本地的蓝牙适配器
		final BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
		// 蓝牙适配器
		BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
		// 打开蓝牙权限
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
		{
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 1);
			tx.setText("请打开蓝牙后点此进入");
		} else {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Intent intent = new Intent(WelcomActivity.this,
							LoginActivity.class);
					startActivity(intent);
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							finish();
						}
					}, 500);
				}
			}, 2000);
		}
	}
}
