package de.vodafone.innogarage.sfcdmonitoring;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TableLayout;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScannerTask extends AsyncTask<ConnectionManager, Void, JSONObject> {




    @Override
    protected JSONObject doInBackground(ConnectionManager... params) {



        ConnectionManager conMan = null;
        if(params.length == 1)
            conMan = params[0];

        Connection con = null;
        List<JSONObject> msgList = null;
        JSONObject actMsg = null;
        //CopyOnWriteArrayList<String> rs = new CopyOnWriteArrayList<>();
        List<Connection> cons = null;

        if(!conMan.getConnections().isEmpty()){

            cons = conMan.getConnections();

            for(Connection temp : cons){

                if(temp.isFocus()){
                    con = temp;
                }
            }
            if(con == null){
                con = cons.get(0); //Default connection
            }

            msgList = con.getIncomingData();

            if(!msgList.isEmpty()) {

                System.out.print("Received msglist");
                actMsg = msgList.get(0);
                msgList.remove(0);
                if (actMsg == null){
                    try {
                        actMsg.put("Value","Error");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }






            }
        }
        return actMsg;
    }








}