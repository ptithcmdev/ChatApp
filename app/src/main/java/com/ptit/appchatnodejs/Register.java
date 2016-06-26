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
                String image = "";
                if (checkConnectToInternet.isConnectingToInternet())
                    mSocket.emit("client-send-information", name, password, email, phone, image);

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
                    int ketQua;
                    try {
                        ketQua = (int) data.get("noidung");
                        if (ketQua == 2) {
                            Toast.makeText(Register.this, "Đăng kí thất bại", Toast.LENGTH_SHORT).show();
                        } else if (ketQua == 1) {
                                Toast.makeText(Register.this, "Tên đăng nhập đã tồn tại ", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(Register.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (JSONException e) {
                        Log.e("ERROR OnRegister", e.toString());
                    }

                }
            });
        }
    };

    private void addControls() {
        mSocket = MainActivity.mSocket;

        activity=getParent();
        txtEmailRegister= (EditText) findViewById(R.id.txtEmailRegister);
        txtPassword= (EditText) findViewById(R.id.txtPassword);
        txtPhonenumber= (EditText) findViewById(R.id.txtPhonenumber);
        txtUserNameRegister= (EditText) findViewById(R.id.txtUserNameRegister);
        btnRegister= (Button) findViewById(R.id.btnRegister1);

    }
}
