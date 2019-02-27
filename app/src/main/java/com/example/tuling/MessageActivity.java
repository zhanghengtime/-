package com.example.tuling;

//用来实现聊天活动

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;;
import java.util.List;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;


public class MessageActivity extends AppCompatActivity{
    private ChatAdapter chatAdapter;
    private ListView lv_chat_dialog;
    private Button exitbtn;
    private List<PersonChat> personChats = new ArrayList();
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch(what) {
                case 1:
                    MessageActivity.this.lv_chat_dialog.setSelection(MessageActivity.this.personChats.size());
                default:
            }
        }
    };
    public MessageActivity() {
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(1);
        this.setContentView(R.layout.activity_message);
        exitbtn = (Button) findViewById(R.id.btn_exit);

        PersonChat personChat = new PersonChat();
        personChat.setMeSend(false);
        personChat.setChatMessage("你好，我是语音助手!");
        this.personChats.add(personChat);

        this.lv_chat_dialog = (ListView)this.findViewById(R.id.lv_chat_dialog);
        Button btn_chat_message_send = (Button)this.findViewById(R.id.btn_chat_message_send);
        final EditText et_chat_message = (EditText)this.findViewById(R.id.et_chat_message);
        this.chatAdapter = new ChatAdapter(this, this.personChats);
        this.lv_chat_dialog.setAdapter(this.chatAdapter);
        exitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });
        btn_chat_message_send.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (TextUtils.isEmpty(et_chat_message.getText().toString())) {
                    Toast.makeText(MessageActivity.this, "发送内容不能为空",Toast.LENGTH_SHORT).show();
                } else {
                    final PersonChat personChat = new PersonChat();
                    personChat.setMeSend(true);
                    personChat.setChatMessage(et_chat_message.getText().toString());
                    MessageActivity.this.personChats.add(personChat);
                    et_chat_message.setText("");
                    MessageActivity.this.chatAdapter.notifyDataSetChanged();
                    MessageActivity.this.handler.sendEmptyMessage(1);
                    if(personChat.getChatMessage().equals("手电筒"))
                    {
                        Toast.makeText(MessageActivity.this,"手电筒！",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MessageActivity.this, FlashActivity.class);
                        startActivity(intent);
                    }
                    else if(personChat.getChatMessage().equals("xxxx") || personChat.getChatMessage().equals("拍照") || personChat.getChatMessage().equals("照相") || personChat.getChatMessage().equals("相机"))
                    {
                        Toast.makeText(MessageActivity.this,"打开相机！",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MessageActivity.this, CameraActivity.class);
                        startActivity(intent);
                    }
                    else if(personChat.getChatMessage().equals("联系人") || personChat.getChatMessage().equals("我要打电话") || personChat.getChatMessage().equals("打电话"))
                    {
                        Toast.makeText(MessageActivity.this,"打开通讯录！",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        startActivity(intent);
                    }
                    else if(personChat.getChatMessage().equals("买票") || personChat.getChatMessage().equals("buy ticket")) {
                        Toast.makeText(MessageActivity.this,"打开购票界面！",Toast.LENGTH_SHORT).show();
                       // Intent intent= new Intent(Intent.ACTION_VIEW);
                      //  intent.setAction("android.intent.action.VIEW");
                        //Uri content_url = Uri.parse("192.168.56.1:82/common/index.html");
                       // intent.setData(content_url);
                      //  startActivity(intent);
                       // finish();
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse("192.168.56.1:82/common/index.html");
                        intent.setData(content_url);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
                        startActivity(intent);
                    }
                    Ask ask = new Ask();
                    Ask.UserInfoBean info = new Ask.UserInfoBean();
                    info.setApiKey("6848c7920ab9445da7c1f8e261fc420d");//将机器人的key值填入
                    info.setUserId("237985");//将用户id填入
                    ask.setUserInfo(info);
                    Ask.PerceptionBean.InputTextBean pre = new Ask.PerceptionBean.InputTextBean(personChat.getChatMessage());//将要发送给机器人书文本天趣
                    ask.setPerception(new Ask.PerceptionBean(pre));

//       创建Retrofit对象
                    final Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://openapi.tuling123.com/")//设置网络请求url，后面一段写在网络请求接口里面
                            .addConverterFactory(GsonConverterFactory.create())//Gson解析
                            .build();
//       创建网络请求接口的实例
                    API api = retrofit.create(API.class);
//      Take为响应实体类，用来接受机器人返回的回复数据
                    Call<Take> call = api.request(ask);
                    Call<Take> clone = call.clone();
                    clone.enqueue(new Callback<Take>()
                    {
                        @Override
                        public void onResponse(Call<Take> call, Response<Take> response) {
                            // Get result bean from response.body()
                            String mText= response.body().getResults().get(0).getValues().getText();
                            // Get header item from response
                           // String links = response.headers().get("Link");
                            final PersonChat personChat1 = new PersonChat();
                            personChat1.setMeSend(false);
                            personChat1.setChatMessage(mText);
                            MessageActivity.this.personChats.add(personChat1);
                            et_chat_message.setText("");
                            MessageActivity.this.chatAdapter.notifyDataSetChanged();
                            MessageActivity.this.handler.sendEmptyMessage(1);
                            /**
                             * 不同于retrofit1 可以同时操作序列化数据javabean和header
                             */
                        }
                        @Override
                        public void onFailure(Call<Take> call, Throwable t)
                        {
                            final PersonChat personChat1 = new PersonChat();
                            personChat1.setMeSend(false);
                            personChat1.setChatMessage("请求失败");
                            MessageActivity.this.personChats.add(personChat1);
                            et_chat_message.setText("");
                            MessageActivity.this.chatAdapter.notifyDataSetChanged();
                            MessageActivity.this.handler.sendEmptyMessage(1);
                        }
                    });
                }
            }
        });
    }
}