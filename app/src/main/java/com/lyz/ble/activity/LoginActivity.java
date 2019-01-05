package com.lyz.ble.activity;

import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.lyz.ble.R;

public class LoginActivity extends Activity implements OnClickListener {

	public static final String KEY_SERVERIP = "Server_IP";
	public static final String KEY_SERVERPORT = "Server_Port";
	String serverip =null;
	int serverport =0;
    private String mRole = "";
	public static final int MENU_SET = 1;
	public static final int MENU_EXIT = 2;

	private Button btn_login;
	private Button btn_setting;
	private TextView tv_user;
	private TextView tv_passwd;
	RequestQueue mQueue = null;
	static ProgressDialog cDialog = null;
	public static final int START_DIALOG = 1;
	public static final int CLOSE_DIALOG = 2;
	public static final int START_CHECK = 3;
	
	public static final int CHECK_FAIL = 4;
	protected static final String TAG = null;
	protected static final int START_LOGIN = 5;
	public static final int CHECK_VERSION = 6;
	SharedPreferences prefs = null;
	ProgressDialog m_progressDlg;
	String geturl = null;
	String downurl = null;
	
	private String mUsername ="";
	private String mPassword ="";
	
	
	Button m_btnCheckNewestVersion;
	Integer m_newVerCode; //最新版的版本号
	String m_newVerName; //最新版的版本名
	String m_appNameStr; //下载到本地要给这个APP命的名字
	String m_appdis;
	// 第一次安装需要配置参数
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mQueue = Volley.newRequestQueue(LoginActivity.this);
		prefs = getSharedPreferences("appPreferences", MODE_PRIVATE);

		setContentView(R.layout.login);
		initView();
		initVariable();
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private void initVariable()
	{	
		m_progressDlg =  new ProgressDialog(this);
		m_progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		 // 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确     
		m_progressDlg.setIndeterminate(false);    
		m_appNameStr = "interlockplus.apk";
		
		cDialog = new ProgressDialog(this);
		cDialog.setMessage("正在登录中...");
		cDialog.setCancelable(false);
	}
	private void initView() {

		btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);
		btn_setting = (Button) findViewById(R.id.btn_setting);
		btn_setting.setOnClickListener(this);
		tv_user = (TextView) findViewById(R.id.et_qqNum);
		tv_passwd = (TextView) findViewById(R.id.et_qqPwd);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

//		menu.add(0, MENU_SET, 1, "设置").setIcon(R.drawable.menu_setting);
//		menu.add(0, MENU_EXIT, 2, "退出").setIcon(R.drawable.menu_exit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case MENU_SET:
			Intent intent = new Intent(this, mysettingpreference.class);
			startActivity(intent);
			break;
		case MENU_EXIT:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private final Handler mhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case START_DIALOG: {
				
				if (serverip == null || serverport == 0){
					Toast.makeText(LoginActivity.this, "初次安装请进入设置！", Toast.LENGTH_LONG).show();
					return ;
				}
				Message sendmsg = new Message();
				sendmsg.what = START_CHECK;
				mhandler.sendMessage(sendmsg);

//				cDialog.show();
//
//				new Thread() {
//					public void run() {
//						try {
//
//							Thread.sleep(3000);
//
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//
//						Message msg = new Message();
//						msg.what = CLOSE_DIALOG;
//						mhandler.sendMessage(msg);
//
//					}
//
//				}.start();

				break;
			}

			case START_CHECK: {
				System.out.println("start check");
				cDialog.show();
				Login();
				break;
			}
			case START_LOGIN: {
				Message sendmsg;
				sendmsg = new Message();
				sendmsg.what = CLOSE_DIALOG;
				mhandler.sendMessage(sendmsg);
//				Intent intent = new Intent(LoginActivity.this,
//						InterlockMainActivity.class);
//				Intent intent = new Intent(LoginActivity.this,
//						AppVersionActivity.class);
//				startActivity(intent);
//				new  checkNewestVersionAsyncTask().execute();
				
				//测试注释
			
				break;
			}
			case CLOSE_DIALOG: {
				cDialog.cancel();
                //离线登录
				break;
			}
			case CHECK_FAIL: {
				cDialog.cancel();
				Toast.makeText(LoginActivity.this, "帐号或密码错误", Toast.LENGTH_LONG)
						.show();
				break;
			}
			}

		}
	};

	private Boolean loginIsEmpty() {

		System.out.println(tv_user.getText().toString()
				+ tv_passwd.getText().toString());
		Log.v("userInfo ", "name:" + tv_user.getText().toString() + ",passwd:"
				+ tv_passwd.getText().toString());
		if ((tv_user.getText() == null)
				|| (tv_user.getText().toString().equals(""))) {
			Toast.makeText(getApplicationContext(), "帐号不能为空", Toast.LENGTH_LONG)
					.show();
			return true;
		} else if ((tv_passwd.getText() == null)
				|| (tv_passwd.getText().toString().equals(""))) {
			Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_LONG)
					.show();
			return true;
		}
		return false;
	}

	
	 private boolean isWifiConnected() {  
	        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);  
	        if (cm != null) {  
	            NetworkInfo networkInfo = cm.getActiveNetworkInfo();  
	            if (networkInfo != null 
	                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {  
	                return true;  
	            }  
	        }  
	        return false;  
	    }  
	   
	    /** 
	     * 检测3G是否连接 
	     *  
	     * @return 
	     */ 
	    private boolean is3gConnected() {  
	        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);  
	        if (cm != null) {  
	            NetworkInfo networkInfo = cm.getActiveNetworkInfo();  
	            if (networkInfo != null 
	                    && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {  
	                return true;  
	            }  
	        }  
	        return false;  
	    }  

	    boolean isConnected(){
	    	boolean wifistate = isWifiConnected();
	    	boolean mobile = is3gConnected();
	    	if (wifistate ||mobile ){
	    		return true;
	    	}else{
	    		return false;
	    	}
	    }
	private void Login() {

		final String username = tv_user.getText().toString();
		final String passwd = tv_passwd.getText().toString();
		String logurl = "http://"+serverip+":"+serverport+"/newinterlockwebservice/checkuser";
		Log.i(TAG, logurl);
		StringRequest loginreq = new StringRequest(Request.Method.GET, logurl,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.d(TAG, response);
						boolean ret = false;
						Gson gson = new Gson();
						JsonResult  result = gson.fromJson(response, JsonResult.class);
						ret = result.getResults();
						if (ret) {
							Message sendmsg;
							sendmsg = new Message();
							sendmsg.what = START_LOGIN;
							Intent intent = new Intent(LoginActivity.this,
									MainActivity.class);
							startActivity(intent);
							finish();
						} else {
							Message sendmsg;
							sendmsg = new Message();
							sendmsg.what = CHECK_FAIL;
							//sendmsg.what = START_LOGIN;
							mhandler.sendMessage(sendmsg);
							
							Toast.makeText(LoginActivity.this,
									result.getErrorinfo(), Toast.LENGTH_LONG).show();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {

						Log.e(TAG, error.getMessage(), error);
						Message sendmsg;
						sendmsg = new Message();
						
						sendmsg.what=CLOSE_DIALOG;
						mhandler.sendMessage(sendmsg);
						Toast.makeText(LoginActivity.this,
								"连接服务器失败，请重新连接！", Toast.LENGTH_LONG).show();
						
					}

				}

		) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("username", username);
				headers.put("pwd", passwd);
				return headers;
			}

		};
		mQueue.add(loginreq);

	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_login: {
			serverip = prefs.getString(KEY_SERVERIP, null);
			if (serverip != null) {
				System.out.println("serverip:" + serverip);
			}
			String port = prefs.getString(KEY_SERVERPORT, null);
			try {
				serverport = Integer.parseInt(port);
				System.out.println("port:" + serverport);
			} catch (Exception e) {
				System.out.println("serverport is empty");
				serverport = 8442;
			}
			
			// 测试登录注释
			 if (loginIsEmpty()) {
			 return;
			 }
			//检测网络
			if (!isConnected()){
				Toast.makeText(getApplicationContext(), "网络连接不可用，请稍后重试！", Toast.LENGTH_LONG)
				.show();
				return ;
			}
//			new  checkNewestVersionAsyncTask().execute();
			Message msg = new Message();
			//测试
			msg.what = START_DIALOG;
			//msg.what =START_LOGIN;
			mhandler.sendMessage(msg);

			break;
		}
		case R.id.btn_setting: {
			Intent intent = new Intent(this, mysettingpreference.class);
			startActivity(intent);
			break;
		}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (cDialog != null)
			cDialog.dismiss();
	}
}