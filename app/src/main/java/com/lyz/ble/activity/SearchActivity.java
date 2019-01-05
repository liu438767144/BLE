package com.lyz.ble.activity;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.lyz.ble.App.AppManager;
import com.lyz.ble.R;
import com.lyz.ble.adapter.BAdapter;

/**
 * 添加和修改member
 * 
 * 
 */
public class SearchActivity extends BaseActivity implements OnItemClickListener, AppManager.RFStarManageListener {
	private ListView list = null;
	private BAdapter bleAdapter;
	private ArrayList<BluetoothDevice> arraySource = new ArrayList<>();

	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		this.initView();
		
		if (manager.cubicBLEDevice != null)
		{
			manager.cubicBLEDevice.disconnectedDevice();
			
			manager.cubicBLEDevice = null;
			
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		manager.startScanBluetoothDevice();
		manager.isEdnabled(this);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		arraySource.clear();
		bleAdapter.notifyDataSetChanged();
		manager.stopScanBluetoothDevice();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.activity_in_left,
				R.anim.activity_out_bottom);
	}

	protected void initView() {
		initNavigation("搜索");
		list = (ListView) this.findViewById(R.id.list);

		list.setOnItemClickListener(this);

		manager.setRFstarBLEManagerListener(this);
		arraySource = new ArrayList<>();
		bleAdapter = new BAdapter(this, arraySource);

		list.setAdapter(bleAdapter);

	}

	@Override
	protected void initNavigation(String title) {
		// TODO Auto-generated method stub
		super.initNavigation(title);
		navigateView.setRightHideBtn(false);
		navigateView.rightBtn.setText("刷新");
		navigateView.rightBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				manager.startScanBluetoothDevice();
			}
		});
		navigateView.setEnable(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		super.onActivityResult(requestCode, resultCode, data);
		manager.onRequestResult(requestCode, resultCode, data);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
//		app.manager.bluetoothDevice = arraySource.get(arg2);
//		app.manager.cubicBLEDevice = new CubicBLEDevice(this.getApplicationContext(), app.manager.bluetoothDevice);
//		app.manager.cubicBLEDevice.setBLEBroadcastDelegate(SearchActivity.this);
		
		String mDeviceAddress = arraySource.get(arg2).getAddress();		
		SharedPreferences sharedPreferences_mac = getSharedPreferences("SSBLE_DEVICE_ADDRESS", Context.MODE_PRIVATE);
		Editor editor_mac = sharedPreferences_mac.edit();
		editor_mac.putString("SSBLE_DEVICE_ADDRESS", mDeviceAddress);		 
		editor_mac.commit();
		
		this.finish();

	}

	/**
	 * 扫描到的蓝牙设备
	 */
	@Override
	public void RFstarBLEManageListener(BluetoothDevice device, int rssi,byte[] scanRecord) 
	{
		// TODO Auto-generated method stub
		Log.d("_TAG", "scanrecord : " + device.getAddress());// device.getName());
		arraySource.add(device);
		bleAdapter.notifyDataSetChanged();
	}

	@Override
	public void RFstarBLEManageStartScan() {
		// TODO Auto-generated method stub
		// showMessage("开始扫描设备");
		this.arraySource.clear();
	}

	@Override
	public void RFstarBLEManageStopScan() {
		// TODO Auto-generated method stub
		// showMessage("扫描设备结束");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//dialog.dismiss();
	}

	
}
