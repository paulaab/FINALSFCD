package de.vodafone.innogarage.sfcdmonitoring;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by paulabohorquez on 5/16/17.
 */

public class Connection {
    private Socket socket;
    
    private InputStream incomingStream;
    private List<JSONObject> incomingData;
    private List<JSONObject> outgoingData;
    private boolean close,focus;
    private String name;
    private String clientip;
    private DataOutputStream outgoingStream;



    public Connection(Socket socket) {
        clientip = socket.getInetAddress().toString();
        name = socket.getInetAddress().getHostName();
        this.socket = socket;
        incomingData = new CopyOnWriteArrayList<>();
        outgoingData = new CopyOnWriteArrayList<>();

        close = false;
        focus = false;

        try {
            outgoingStream = new DataOutputStream(socket.getOutputStream());


        }catch (IOException e ){
            e.printStackTrace();
        }




        //Get incoming stream and place it in an arraylist
        try {
            incomingStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Start reading incoming stream above in a and convert it to
        // JSON Object that will be accessed after that. *NEW THREAD*
        new InputStreamThread().start();
        //new OutputStreamThread().start;
    }


    private class InputStreamThread extends Thread {
        BufferedReader breader = new BufferedReader(new InputStreamReader(incomingStream));

        public void run() {
            String inmsg = null;
            JSONObject jObj = null;
            while (!close) {
                try {
                    //inmsg = breader.readLine();

                    char[] b = new char[1024];
                    int count = breader.read(b, 0, 1024);
                    if (count!=0){
                        inmsg = new String(b, 0, count);
                        inmsg = inmsg.substring(8);
                        System.out.println(socket.getInetAddress() + "     Incomming message stream received:  " + inmsg);
                        if (inmsg != null) {
                            try {
                                jObj = new JSONObject(inmsg);

                            } catch (JSONException e) {
                                //e.printStackTrace();

                                Log.e("InputStreamThread: ", socket.getInetAddress() + "   Could not save Json Object with incoming stream");
                                jObj = new JSONObject();


                            }
                            incomingData.add(jObj);
                            Log.e("Connection: ", socket.getInetAddress() + " Message received: " + jObj.toString() + " => Placed in incomingData, parsed as JSON");


                        }



                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }


//    private class OutputStream


    public String getName() {return name;}
    public String getIP(){return clientip;}
    public List<JSONObject> getIncomingData(){return incomingData;}
    public boolean isFocus() {return focus;}
    public void setFocus(boolean focus) {this.focus = focus;}
    public Socket getSocket(){return socket;}
    public boolean isClose() {return close;}
    public void setClose(boolean close) {this.close = close;}






}
