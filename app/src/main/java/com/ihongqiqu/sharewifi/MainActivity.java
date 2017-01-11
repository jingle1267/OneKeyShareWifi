package com.ihongqiqu.sharewifi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    ToggleButton tb_wifi_switch;
    TextView tv_ap_info;

    WifiAPMgr mWifiAPMgr;

    String apName = "freeWifi";
    String apPwd = "987654321";

    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWifiAPMgr = new WifiAPMgr(this, apName, apPwd);
        tb_wifi_switch = (ToggleButton) findViewById(R.id.tb_wifi_switch);
        tv_ap_info = (TextView) findViewById(R.id.tv_ap_info);

        tb_wifi_switch.setChecked(mWifiAPMgr.isApOn());

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tb_wifi_switch:
                boolean isOn = mWifiAPMgr.isApOn();
                boolean isSuccess = mWifiAPMgr.setWifiApEnabled(!isOn);
                StringBuilder msg = new StringBuilder();
                msg.append(isOn ? "关闭" : "开启").append("AP").append(isSuccess ? "成功" : "失败");
                mToast.setText(msg);
                mToast.show();
                if (!isOn && isSuccess) {
                    tv_ap_info.setText("热点名称:" + apName + "\n"
                            + "热点密码:" + apPwd);
                    tv_ap_info.setVisibility(View.VISIBLE);
                } else {
                    tv_ap_info.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }


}
