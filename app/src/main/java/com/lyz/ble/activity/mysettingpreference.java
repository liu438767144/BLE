package com.lyz.ble.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.EditTextPreference;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.lyz.ble.R;

public class mysettingpreference extends PreferenceActivity implements OnSharedPreferenceChangeListener ,OnClickListener {
	 public static final String KEY_SERVERIP = "Server_IP";  
	 public static final  String KEY_SERVERPORT = "Server_Port";  
	 public static final  String KEY_TALKTIMEOUT = "talk_timeout";  
	 public static final  String KEY_CONNECTTIMEOUT = "connect_timeout";  
	 private EditTextPreference serverippre;
	 private EditTextPreference serveripport;
	 private EditTextPreference talktopre;
	 private EditTextPreference connecttopre;
	 private String ip_default = "192.168.1.1";
	 private String port_default = "80";
	 private String talk_time_default ="5";
	 private String conncet_time_default = "5";
	 
	 private TextView tv_top_title;
	 private Button btn_title_left;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);  
        PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName("appPreferences");
        
        
		setContentView(R.layout.setting);
		
        //---load the preferences from an XML file---
        addPreferencesFromResource(R.xml.setpreference);
        serverippre =  (EditTextPreference)getPreferenceScreen().findPreference(KEY_SERVERIP);  
        serveripport =  (EditTextPreference)getPreferenceScreen().findPreference(KEY_SERVERPORT);
        talktopre = (EditTextPreference)getPreferenceScreen().findPreference(KEY_TALKTIMEOUT);
        connecttopre = (EditTextPreference)getPreferenceScreen().findPreference(KEY_CONNECTTIMEOUT);
        
        initView();
        
    }

	private void initView(){
		tv_top_title= (TextView) findViewById(R.id.tv_top_title);
		tv_top_title.setText("系统设置");
		
		btn_title_left = (Button)findViewById(R.id.btn_title_left);
		btn_title_left.setOnClickListener( this);
	}
	@Override  
	 protected void onResume() {
		super.onResume(); 
		
		
		serverippre.setSummary((serverippre.getText() == null) ? ip_default :  serverippre.getText()) ;
		if (serverippre.getText() == null)
		{
			serverippre.setText(ip_default);
		}	
		
		serveripport.setSummary( (serveripport.getText() == null) ? port_default : serveripport.getText()); 
		if (serveripport.getText() == null)
		{
			serveripport.setText( port_default );
		}
		
		talktopre.setSummary((talktopre.getText() == null) ? (talk_time_default + "秒") :(talktopre.getText()+"秒") );
		if (talktopre.getText() == null)
		{
			talktopre.setText(talk_time_default);
			
		}
		
		
		connecttopre.setSummary((connecttopre.getText() == null) ? (conncet_time_default + "秒") : (connecttopre.getText()+"秒"));
		if (connecttopre.getText() == null)
		{
			
			connecttopre.setText(conncet_time_default);
		}
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);  
		
	}

	@Override  
	   protected void onPause() {  
	      super.onPause();  
	 
      // Unregister the listener whenever a key changes              
	       getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);      
   }  

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		if (key.equals(KEY_SERVERIP)) {
			serverippre.setSummary(serverippre.getText()); 
			
		}else if (key.equals(KEY_SERVERPORT)){
			serveripport.setSummary(serveripport.getText());
		}else if (key.equals(KEY_TALKTIMEOUT)){
			talktopre.setSummary(talktopre.getText()+"秒");
			
		}else if (key.equals(KEY_CONNECTTIMEOUT)){
			connecttopre.setSummary(connecttopre.getText()+"秒");
		}
		
	}
	public void onClick(View v) {
		switch(v.getId()){
		 case R.id.btn_title_left:
			 mysettingpreference.this.finish();
			break;
		
		}
	}
}

