package timemaster.smart_calendar.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import timemaster.smart_calendar.R;

import java.io.IOException;
import java.util.HashMap;

public class LoginActivity extends Activity {
    private Button btnLogin;
    private Button btnRegister;
    private EditText editId;
    private EditText editPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        editId=findViewById(R.id.edit_id);
        editPwd=findViewById(R.id.edit_pwd);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id=editId.getText().toString().trim();
                String pwd=editPwd.getText().toString().trim();
                login(id,pwd);
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id=editId.getText().toString().trim();
                String pwd=editPwd.getText().toString().trim();
                register(id,pwd);
            }
        });
    }

    private void register(String id, String pwd) {
        if(id==null || id.length()<=0){
            Toast.makeText(this,"请输入用户名",Toast.LENGTH_SHORT).show();
            return;
        }
        if(pwd==null || pwd.length()<=0){
            Toast.makeText(this,"请输入密码",Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpClient client=new OkHttpClient.Builder().build();
        HashMap<String,String> map=new HashMap<>();
        map.put("username",id);
        map.put("password",pwd);
        String jsonStr=new JSONObject(map).toString();
        RequestBody requestBody=RequestBody.
                create(MediaType.parse("application/json;charset=utf-8"),jsonStr);
        final Request request=new Request.Builder()
                .url(getString(R.string.Url)+"register")
                .addHeader("contentType","application/json;charset=UTF-8")
                .post(requestBody)
                .build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Register","Failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    if(jsonObject.getString("status").equals("success")){
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else{
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this,"已存在该用户名",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void login(String id, String pwd) {
        if(id==null || id.length()<=0){
            Toast.makeText(this,"请输入用户名",Toast.LENGTH_SHORT).show();
            return;
        }
        if(pwd==null || pwd.length()<=0){
            Toast.makeText(this,"请输入密码",Toast.LENGTH_SHORT).show();
            return;
        }
        OkHttpClient client=new OkHttpClient.Builder().build();
        Log.e("Login",getString(R.string.Url)+"login?username="+id+"&password="+pwd);
        final Request request=new Request.Builder()
                .url(getString(R.string.Url)+"login?username="+id+"&password="+pwd)
                .get()
                .build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Login","Failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    if(jsonObject.getString("status").equals("success")){
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, UsageActivity.class);
                        startActivity(intent);
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Looper.prepare();
                        Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                    }
                    Looper.loop();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}