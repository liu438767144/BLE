package com.lyz.ble.activity;

import java.util.ArrayList;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.lyz.ble.App.AppManager;
import com.lyz.ble.R;
import com.lyz.ble.params.BLEDevice;
import com.lyz.ble.params.CubicBLEDevice;
import com.lyz.ble.service.RFStarBLEService;
import com.lyz.ble.view.ReceiveDataView;

/**
 * 获取数据
 *
 * @author fcoolt
 */
public class MainActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, BLEDevice.RFStarBLEBroadcastReceiver {

    private byte[] reduCode1 = new byte[4];
    private Button resetBtn;
    private Button GetStatusBtn;
    private Button OpenBtn;
    private TextView TextInfo;
    private ReceiveDataView receiveDataView;
    //保存已经打开过的蓝牙设备
    private ArrayList<BluetoothDevice> arraySource = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavigation("test");
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiveDataView.changeEditBackground(true);
        if (manager.cubicBLEDevice == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("SSBLE_DEVICE_ADDRESS", Context.MODE_PRIVATE);
            String mDeviceAddress = sharedPreferences.getString("SSBLE_DEVICE_ADDRESS", "00:00:00:00:00:00");
            manager.bluetoothDevice = AppManager.bleAdapter.getRemoteDevice(mDeviceAddress);
            manager.cubicBLEDevice = new CubicBLEDevice(this.getApplicationContext(), manager.bluetoothDevice);
        }
        manager.cubicBLEDevice.setBLEBroadcastDelegate(MainActivity.this);

    }

    private void initView() {
        //Intent intent = this.getIntent();
        //MemberItem member = (MemberItem) intent.getSerializableExtra(App.TAG);
        this.initNavigation("测试");
        resetBtn = (Button) this.findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(this);
        GetStatusBtn = (Button) this.findViewById(R.id.GetStatusBtn);
        GetStatusBtn.setOnClickListener(this);
        OpenBtn = (Button) this.findViewById(R.id.OpenBtn);
        OpenBtn.setOnClickListener(this);
        this.findViewById(R.id.getid).setOnClickListener(this);
        TextInfo = (TextView) this.findViewById(R.id.Data_Info);
        TextInfo.setText("\r\n");
        receiveDataView = (ReceiveDataView) this.findViewById(R.id.getDataViewShow);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.resetBtn:
                receiveDataView.reset();
                TextInfo.setText("\r\n");
                break;
            case R.id.GetStatusBtn:
                openLockState();
                break;
            case R.id.OpenBtn:
                openDoor();
                break;
            case R.id.getid:
                getLockID();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    }

    /**
     * 返回数据
     */
    @Override
    public void onReceive(Context context, Intent intent, String macData, String uuid) {
        String action = intent.getAction();
        this.connectedOrDis(intent.getAction());
        if (RFStarBLEService.ACTION_GATT_CONNECTED.equals(action)) {
            Log.d("_TAG", "111111111 连接完成");
        } else if (RFStarBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
            Log.d("_TAG", "111111111 连接断开");
            manager.cubicBLEDevice.disconnectedDevice();
            manager.cubicBLEDevice = null;
            SharedPreferences sharedPreferences = getSharedPreferences("SSBLE_DEVICE_ADDRESS", Context.MODE_PRIVATE);
            String mDeviceAddress = sharedPreferences.getString("SSBLE_DEVICE_ADDRESS", "00:00:00:00:00:00");
            manager.bluetoothDevice = AppManager.bleAdapter.getRemoteDevice(mDeviceAddress);
            manager.cubicBLEDevice = new CubicBLEDevice(this.getApplicationContext(), manager.bluetoothDevice);
            manager.cubicBLEDevice.setBLEBroadcastDelegate(MainActivity.this);
        } else if (RFStarBLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            openLockState();
            boolean Is_SetNotif = true;
            for (BluetoothDevice dev : arraySource) {
                if (manager.bluetoothDevice.equals(dev)) {
                    Is_SetNotif = false;
                    break;
                }
            }
            if (Is_SetNotif) {
                manager.cubicBLEDevice.setNotification("ffe0", "ffe4", true);
                arraySource.add(manager.bluetoothDevice);
            }
        } else if (RFStarBLEService.ACTION_DATA_AVAILABLE.equals(action)) {
            byte[] data = intent.getByteArrayExtra(RFStarBLEService.EXTRA_DATA);
            if (uuid.contains("ffe4")) {
                try {
                    //从特征值获取数据
                    String nTemp = "";
                    if (data != null && data.length > 0) {
                        final StringBuilder stringBuilder = new StringBuilder(data.length);
                        for (byte byteChar : data) {
                            stringBuilder.append(String.format("%02X ", byteChar));
                        }
						nTemp = "接收：" + stringBuilder.toString() +  "\n";//HexToString.encodeHexStr(data);
                    }
//					nTemp = "接收：回调命令"+  "\n";
                    receiveDataView.appendString(nTemp);
                    analysisData(data);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //开锁
    private void openDoor() {
        byte[] arrayOfByte = new byte[9];
        arrayOfByte[0] = (byte) 170;//aa
        arrayOfByte[1] = (byte) 187;//bb
        arrayOfByte[2] = (byte) 6;//06
        arrayOfByte[3] = (byte) 1;//01
        arrayOfByte[4] = (byte) 255;
        arrayOfByte[5] = (byte) 255;
        arrayOfByte[6] = (byte) 255;
        arrayOfByte[7] = (byte) 255;
        arrayOfByte[8] = ((byte) (arrayOfByte[2] ^ arrayOfByte[3] ^ arrayOfByte[4] ^ arrayOfByte[5] ^ arrayOfByte[6] ^ arrayOfByte[7]));
        SendData(arrayOfByte);
    }

    //获取锁ID
    private void getLockID() {
        byte[] arrayOfByte = new byte[9];
        arrayOfByte[0] = (byte) 170;//aa
        arrayOfByte[1] = (byte) 187;//bb
        arrayOfByte[2] = (byte) 6;//06
        arrayOfByte[3] = (byte) 2;//02
        arrayOfByte[4] = (byte) 255;
        arrayOfByte[5] = (byte) 255;
        arrayOfByte[6] = (byte) 255;
        arrayOfByte[7] = (byte) 255;
        arrayOfByte[8] = ((byte)(arrayOfByte[2] ^ arrayOfByte[3] ^ arrayOfByte[4] ^ arrayOfByte[5] ^ arrayOfByte[6] ^ arrayOfByte[7]));
        SendData(arrayOfByte);
    }

    //获取锁状态
    private void openLockState() {
        byte[] arrayOfByte = new byte[9];
        arrayOfByte[0] = (byte) 170;//aa
        arrayOfByte[1] = (byte) 187;//bb
        arrayOfByte[2] = (byte) 6;//06
        arrayOfByte[3] = (byte) 3;//03
        arrayOfByte[4] = (byte) 255;
        arrayOfByte[5] = (byte) 255;
        arrayOfByte[6] = (byte) 255;
        arrayOfByte[7] = (byte) 255;
        arrayOfByte[8] = ((byte) (arrayOfByte[2] ^ arrayOfByte[3] ^ arrayOfByte[4] ^ arrayOfByte[5] ^ arrayOfByte[6] ^ arrayOfByte[7]));
        SendData(arrayOfByte);
    }

    private void analysisData(byte[] _data) {
        String nTempText = "";
        if (null != _data && _data.length > 0) {
            int flag = (int) _data[3];
            switch (flag) {
                case 1:
                    if ((int) _data[4] == 1)
                        nTempText = "开锁成功" + "\n";
                    else
                        nTempText = "开锁失败" + "\n";
                    receiveDataView.appendString(nTempText);
                    break;
                case 2:
                    byte[] lockId = new byte[4];
                    lockId[0] = _data[4];
                    lockId[1] = _data[5];
                    lockId[2] = _data[6];
                    lockId[3] = _data[7];
                    StringBuilder stringBuilder = new StringBuilder(lockId.length);
                    for (byte byteChar : lockId) {
                        stringBuilder.append(String.format("%02X ", byteChar));
                    }
                    nTempText = "ID：" + stringBuilder.toString() +  "\n";
                    receiveDataView.appendString(nTempText);
                    break;
                case 3:
                    if ((int) _data[4] == 1) {
                        nTempText = "门状态：开" + "\n";
                    } else {
                        nTempText = "门状态：关" + "\n";
                    }
                    receiveDataView.appendString(nTempText);
                    if ((int) _data[5] == 1) {
                        nTempText = "锁状态：开" + "\n";
                    } else {
                        nTempText = "锁状态：关" + "\n";
                    }
                    receiveDataView.appendString(nTempText);
                    break;
            }
        }
    }

    private void SendData(byte[] paramArrayOfByte) {
        if (manager.cubicBLEDevice != null) {
            manager.cubicBLEDevice.writeValue("ffe5", "ffe9", paramArrayOfByte);
            manager.cubicBLEDevice.writeValue("ffe0", "ffe9", paramArrayOfByte);
        }
    }

    /**
     * 把int转化为byte数组
     *
     * @param value 传入用户id
     * @see [类、类#方法、类#成员]
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    public static int byte2int(byte[] res) {
        // 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
        int targets = (res[3] & 0xff) | ((res[2] << 8) & 0xff00) // | 表示安位或
                | ((res[1] << 24) >>> 8) | (res[0] << 24);
        return targets;
    }

    public static byte[] shortToByte(short number) {
        int value = number;
//    	byte[] b = new byte[2];         
//    	for (int i = 0; i < b.length; i++) {             
//    		b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位            
//    		temp = temp >> 8; // 向右移8位         
//    	}  
        byte[] b = new byte[2];
        b[0] = (byte) ((value >> 8) & 0xFF);
        b[1] = (byte) (value & 0xFF);
        return b;
    }

    public static short byteToShort(byte[] b) {
        short s = 0;
        short s0 = (short) (b[1] & 0xff);// 最低位
        short s1 = (short) (b[0] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }


}
