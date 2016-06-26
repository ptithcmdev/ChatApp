package com.ptit.appchatnodejs;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.ptit.utils.ConnectionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class Register extends AppCompatActivity {
    ConnectionManager checkConnectToInternet = new ConnectionManager(this);
    Activity activity;
    private  Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://nodejs-chatptit.rhcloud.com/");
//            mSocket = IO.socket("http://10.0.3.2:3000/");
        } catch (URISyntaxException e) {
            Log.d(e.getMessage(), "instance initializer: ");
        }
    }

    EditText txtUserNameRegister,txtPassword,txtEmailRegister,txtPhonenumber;
    Button btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        addControls();
        addEvent();
        mSocket.on("result-register", onServerSendUser);
    }

    private void addEvent() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = txtUserNameRegister.getText().toString();
                String password = txtPassword.getText().toString();
                String email = txtEmailRegister.getText().toString();
                String phone = txtPhonenumber.getText().toString();

                if (checkConnectToInternet.isConnectingToInternet())
                    mSocket.emit("client-send-information", name, password, email, phone);

            }
        });
    }
    private Emitter.Listener onServerSendUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    boolean ketQua;
                    try {
                        ketQua = (boolean) data.get("noidung");
                        Toast.makeText(Register.this, String.valueOf(ketQua), Toast.LENGTH_LONG).show();
                        if (!ketQua) {
                            Toast.makeText(Register.this, "Đăng kí thất bại", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(Register.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                            activity.finish();
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR OnRegister", e.toString());
                    }

                }
            });
        }
    };

    private void addControls() {
        activity=getParent();
        txtEmailRegister= (EditText) findViewById(R.id.txtEmailRegister);
        txtPassword= (EditText) findViewById(R.id.txtPassword);
        txtPhonenumber= (EditText) findViewById(R.id.txtPhonenumber);
        txtUserNameRegister= (EditText) findViewById(R.id.txtUserNameRegister);
        btnRegister= (Button) findViewById(R.id.btnRegister1);

    }
}
