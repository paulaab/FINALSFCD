package de.vodafone.innogarage.sfcdmonitoring;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by paulabohorquez on 5/16/17.
 */

public class ConnectionManager {
    private ServerSocket serverSocketForSFCD = null;
    private List<Connection> connections;
    boolean ServerOn = true;


    public ConnectionManager(){
        connections = new CopyOnWriteArrayList<>();
        //Create Socket for receiving SFCD Data
        try {
            serverSocketForSFCD = new ServerSocket(MainActivity.socketServerPortForSFCD);
            Log.e("ConnectionManager "," Constructor SFCD : Server socker successfuly created");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ConnectionManager "," Constructor SFCD : Could not create Socket");
        }
        //Start listening TCP messages - *New Thread*
        new ConnectionListenerForSFCD().start();
       // new ConnectionCheckerThread().start();
    }

/*

    public void sendToAll(JSONObject msg){

        for(Connection con : connections){

            con.sendMessage(msg);
        }
    }
    /**
     * Thread Class
     *
     * @author Steffen.Ryll
     */
    private class ConnectionCheckerThread extends Thread {

        public void run() {
            while (true) {

                if(!connections.isEmpty()){

                    for(Connection con : connections){

                        if(con.isClose()){

                            connections.remove(con);
                        }
                    }

                    JSONObject msg = new JSONObject();
                    try {
                        msg.put("msgType","areualive");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                   // sendToAll(msg);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendInvitation(){
        new Broadcaster().start();
    }

    /**
     * Thread Class
     * Akzeptiert eingehende TCP Verbindungen und sichert diese Verbindung
     * in einer ConnectionListe
     *
     * @author Steffen.Ryll
     */
    private class ConnectionListenerForSFCD extends Thread {

        public void run() {

            while (ServerOn) {

                Socket socket = null;

                try {
                    socket = serverSocketForSFCD.accept();
                    Log.e("ConnectionManager "," ConListener :Waiting for connections!");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                connections.add(new Connection(socket));
                Log.e("ConnectionManager "," ConListener : New SFCD added. IP= " + socket.getInetAddress().toString());

            }

            try{
                serverSocketForSFCD.close();
            }catch (IOException ex){
                ex.printStackTrace();
                System.out.print("Problem closing server socket");
            }

        }

    }


    public List<Connection> getConnections() {

        return connections;
    }

    public void setServerState (boolean serverState){
        this.ServerOn = serverState;

    }

    public void clearConnections(){
        connections.clear();
    }

   /* private class ConnectionCheckerThread extends Thread {

        public void run() {
            while (true) {

                if(!connections.isEmpty()){

                    for(Connection con : connections){

                        if(con.isClose()){

                            connections.remove(con);
                        }
                    }


                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void setIsServerOn(boolean status) {
        ServerOn = status;
    }*/
}




