package cau.cse.capstone.focus;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ReAdapter myAdapter;
    ArrayList<ItemInfo> ItemInfoArrayList;

    Button btn, btn2;
    TextView cur_data;
    String sms_info;

    //  TCP Connection
    private Socket clientSocket;
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private int port = 12345;
    private final String ip = "175.197.22.200";
    private MyHandler myHandler;
    private MyThread myThread;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("i", "Main_onCreate");
        setContentView(R.layout.activity_main);

        //get notification data info
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //bundle must contain all info sent in "data" field of the notification
        }

        // Recycler View
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        ItemInfoArrayList = new ArrayList<>();
        myAdapter = new ReAdapter(ItemInfoArrayList);
        mRecyclerView.setAdapter(myAdapter);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        ItemInfo info = ItemInfoArrayList.get(position);
                        showImage(info.bm, position);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        // StrictMode는 개발자가 실수하는 것을 감지하고 해결할 수 있도록 돕는 일종의 개발 툴
        // - 메인 스레드에서 디스크 접근, 네트워크 접근 등 비효율적 작업을 하려는 것을 감지하여
        //   프로그램이 부드럽게 작동하도록 돕고 빠른 응답을 갖도록 함, 즉  Android Not Responding 방지에 도움
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            clientSocket = new Socket(ip, port);
            socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        myHandler = new MyHandler();
        myThread = new MyThread();
        myThread.start();

        btn = (Button) findViewById(R.id.btn);
        btn2 = (Button) findViewById(R.id.btn2);
        cur_data = (TextView) findViewById(R.id.t_cur_data);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socketOut.println(123);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GoogleSignInActivity)GoogleSignInActivity.mContext).signOut();
                startActivity(new Intent(MainActivity.this, GoogleSignInActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        Log.i("i", "Main_onResume");
        super.onResume();
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    // InputStream의 값을 읽어와서 data에 저장
                    String data = socketIn.readLine();
                    // Message 객체를 생성, 핸들러에 정보를 보낼 땐 이 메세지 객체를 이용
                    Message msg = myHandler.obtainMessage();
                    msg.obj = data;
                    myHandler.sendMessage(msg);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            String data = msg.obj.toString();
            StringTokenizer st = new StringTokenizer(data, ":");
            String str_bm = st.nextToken();
            Log.i("Packet BM", " " + str_bm.length());
            Log.i("Packet DATA", " " + data.length());
            Bitmap bm = StringToBitMap(data);

            String s1 = st.nextToken();
            StringTokenizer time = new StringTokenizer(s1, " ");
            String date = time.nextToken();
            String hour = time.nextToken();
            String min = time.nextToken();
            String sec = time.nextToken();
            s1 = date + " " + hour + "시 " + min + "분 " + sec + "초";

            String s2 = st.nextToken();
            String s3 = st.nextToken();
            String s4 = st.nextToken();

            Log.i("i", s1);
            Log.i("i", s2);
            Log.i("i", s3);
            Log.i("i", s4);

            // 사고 발생 시 Processing
            if(Integer.parseInt(s2) >= 50 && s3 == "1" && s4 == "1"){
                Toast.makeText(getApplicationContext(), "사고 발생", Toast.LENGTH_SHORT).show();
            }

            cur_data.setText("최근 온도 : " + s2 + "\n" + "기울기 유무: " + s3 + "\n" + "불꽃 유무: " + s4);
            String temp_str = "시간 : " + s1 + "\n" + "온도 : " + s2 + "\n" + "기울기 : " + s3 + "\n" + "불꽃 : " + s4;
            ItemInfoArrayList.add(0, new ItemInfo(bm, temp_str));
            mRecyclerView.setAdapter(myAdapter);
        }
    }

    public void showImage(Bitmap bm, int position) {
        ItemInfo info = ItemInfoArrayList.get(position);
        sms_info = info.data.toString();

        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bm);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(700,560));

        Button button = new Button(this);
        button.setText("119");
        button.setTextColor(Color.parseColor("#CC0000"));
        button.setTextSize(20);
        button.setBackgroundColor(Color.parseColor("#ffffff"));

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:119"));
                //Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:12345"));
                startActivity(intent);
            }
        });

        Button sms_button = new Button(this);
        sms_button.setText("SMS");
        sms_button.setTextColor(Color.parseColor("#CC0000"));
        sms_button.setTextSize(20);
        sms_button.setBackgroundColor(Color.parseColor("#ffffff"));

        sms_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                // 전송 구현
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:119"));
                smsIntent.putExtra("sms_body", sms_info);
                startActivity(smsIntent);
            }
        });

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);;
        lp.setMargins(550,0,0, 0);
        builder.addContentView(button, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        builder.addContentView(sms_button, lp);
        builder.show();
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            // byte [] encodeByte = encodedString.getBytes();
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            Log.i("Packet STB : ", e.getMessage());
            return null;
        }
    }
}