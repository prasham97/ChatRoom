package com.example.sheth.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {


    static final String hostname = "192.168.1.122";
    static final int portnumber = 6666;
    Socket clientSocket;
    private static PrintStream os = null;
    private static DataInputStream is = null;
    private static BufferedReader inputLine = null;
    private static boolean closed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView  tv= (TextView) findViewById(R.id.textView);
        new Thread() {
            @Override
            public void run() {
                try {
                    InetAddress serverAddr = InetAddress.getByName(hostname);

                    Log.i("debug", "attempting to connect");
                    clientSocket = new Socket(serverAddr,portnumber);
                    inputLine = new BufferedReader(new InputStreamReader(System.in));
                    os = new PrintStream(clientSocket.getOutputStream());
                    is = new DataInputStream(clientSocket.getInputStream());
                    Log.i("debug", "connected");

                    // Message will be sent whenever user presses send button...
                    //receive message...
                    String msg;
                    BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    while((msg=br.readLine())!=null){

                        final String finalMsg = msg;
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(tv.getText()+"\n"+ finalMsg);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("debug", e.getMessage());
                }
            }
        }.start();
    }

    public void SendMessage(View view) {
        EditText et = (EditText) findViewById(R.id.editText);
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            bw.write(et.getText().toString());
            bw.newLine();
            bw.flush();
            et.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void who(View view) {

        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            bw.write("#who");
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
