package de.vodafone.innogarage.sfcdmonitoring;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    ExpandableRelativeLayout expandableLayout1, expandableLayout2, expandableLayout3, expandableLayout4, expandableLayout5;
    //Define socket variables (ports)
    public static boolean debugMode = true;
    public static int socketPortForBroadcast = 45555;
    public static int socketServerPortForSFCD = 45556;
    //Define variables for connection
    ConnectionManager conMan = new ConnectionManager();
    List<Connection> cons = conMan.getConnections();
    TimerTask timerTask;
    Timer timer = new Timer();
    Context globalContext;


    private Handler handler = new Handler();
    public ListView msgView;
    public ArrayAdapter<String> msgList;
    public ArrayList<String> list;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        globalContext = this;
        /*---------------List of messages initialization - MSG-----------------*/
        /*msgView = (ListView) findViewById(R.id.lv1);
        msgList = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        msgView.setAdapter(msgList);*/

        final Button button = (Button) findViewById(R.id.buttonInvitation);
        button.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                conMan.sendInvitation();
            }
        });


        timerTask = new TimerTask() {

            public void run() {
                new ScannerTask() {

                    protected void onPostExecute(JSONObject result) {

                        if (!cons.isEmpty()&&result!=null) {

                            ListView listDevices = (ListView) findViewById(R.id.mSFCDList);
                            listDevices.setAdapter(new ListViewAdapter(globalContext, conMan));
                            //TableLayout tableLayout = (TableLayout) findViewById(R.id.detailsTable);
                            //tableLayout.removeAllViews();

                           try {
                                SetValuesInTable(result);
                           }
                           catch (JSONException e){
                                System.out.println("Could not set values in table for JSON Object");
                           }

                        }
                    }
                }.execute(conMan);
            }
        };
        timer.schedule(timerTask, 0, 200);




    }
    /*------------Methods for displaying the data on the list--------------*/



public void SetValuesInTable(JSONObject jobj) throws JSONException{
    JSONObject dataset = jobj;
    Iterator<?> keys = dataset.keys();

    while (keys.hasNext()){
        String mKey = (String) keys.next();
        switch (mKey){
            case "location":
                try{
                    JSONObject lobj = dataset.getJSONObject("location");
                    if (lobj != null){
                        Iterator<?> lkeys = lobj.keys();
                        while (lkeys.hasNext()){
                            int id = getResources().getIdentifier(String.valueOf(lkeys), "id", getPackageName());
                            TextView temp = (TextView) findViewById(id);
                            temp.setText(lobj.getString(String.valueOf(lkeys)));
                        }
                    }
                }
                catch (JSONException e){
                    System.out.println("Could not extract location object -->");
                    e.printStackTrace();
                    break;
                }
                break;

            case "gstatus":
                try{
                    JSONObject gobj = dataset.getJSONObject("gstatus");
                    if(gobj!= null){
                        Iterator<?> gKey = gobj.keys();
                        while (gKey.hasNext()){
                            if (String.valueOf(gKey) == "ltebw(mhz)"){
                                TextView temp = (TextView) findViewById(R.id.ltebw);
                                temp.setText(gobj.getString(String.valueOf(gKey)));
                            }
                            else if(String.valueOf(gKey) == "rsrp(dbm)pccrxdrssi"){
                                TextView temp = (TextView) findViewById(R.id.pccrxdrssi);
                                temp.setText(gobj.getString(String.valueOf(gKey)));

                            }
                            else if(String.valueOf(gKey) == "rsrp(dbm)pccrxmrssi"){
                                TextView temp = (TextView) findViewById(R.id.pccrxrmrssi);
                                temp.setText(gobj.getString(String.valueOf(gKey)));

                            }
                            else if(String.valueOf(gKey) == "rsrq(db)"){
                                TextView temp = (TextView) findViewById(R.id.grsrq);
                                temp.setText(gobj.getString(String.valueOf(gKey)));


                            }
                            else if(String.valueOf(gKey) == "sinr(db)"){
                                TextView temp = (TextView) findViewById(R.id.gsinr);
                                temp.setText(gobj.getString(String.valueOf(gKey)));
                            }

                            else if(String.valueOf(gKey) == "mode"){
                                TextView temp = (TextView) findViewById(R.id.gmode);
                                temp.setText(gobj.getString(String.valueOf(gKey)));

                            }
                            else{
                                int id = getResources().getIdentifier(String.valueOf(gKey), "id", getPackageName());
                                TextView temp = (TextView) findViewById(id);
                                temp.setText(gobj.getString(String.valueOf(gKey)));
                            }


                        }
                    }
                }
                catch (JSONException e){
                    System.out.println("Could not extract gstatus object -->");
                    e.printStackTrace();
                    break;
                }
                break;


            case "serving":
                try {
                    JSONArray sarr = dataset.getJSONArray("serving");
                    if(sarr!=null){
                        JSONObject sobj= sarr.getJSONObject(0);
                        Iterator<?> sKey = sobj.keys();
                        while (sKey.hasNext()){
                            if (String.valueOf(sKey)=="EARFCN"){
                                    TextView temp = (TextView) findViewById(R.id.searfcn);
                                    temp.setText(sobj.getString(String.valueOf(sKey)));}
                            else if(String.valueOf(sKey)=="MCC"){
                                    TextView temp = (TextView) findViewById(R.id.smcc);
                                    temp.setText(sobj.getString(String.valueOf(sKey)));}
                            else if(String.valueOf(sKey)=="MNC"){
                                    TextView temp = (TextView) findViewById(R.id.smnc);
                                    temp.setText(sobj.getString(String.valueOf(sKey)));}
                            else if(String.valueOf(sKey)=="TAC"){
                                    TextView temp = (TextView) findViewById(R.id.stac);
                                    temp.setText(sobj.getString(String.valueOf(sKey)));}
                            else if(String.valueOf(sKey)=="CID"){
                                    TextView temp = (TextView) findViewById(R.id.scid);
                                    temp.setText(sobj.getString(String.valueOf(sKey)));}
                            else if(String.valueOf(sKey)=="Bd"){
                                    TextView temp = (TextView) findViewById(R.id.sbd);
                                    temp.setText(sobj.getString(String.valueOf(sKey)));}
                            else if(String.valueOf(sKey)=="D"){
                                    TextView temp = (TextView) findViewById(R.id.sd);
                                    temp.setText(sobj.getString(String.valueOf(sKey)));}
                            else if(String.valueOf(sKey)=="U"){
                                    TextView temp = (TextView) findViewById(R.id.su);
                                    temp.setText(sobj.getString(String.valueOf(sKey)));}
                            else if(String.valueOf(sKey)=="SNR"){
                                    TextView temp = (TextView) findViewById(R.id.ssnr);
                                    temp.setText(sobj.getString(String.valueOf(sKey)));}
                            else if(String.valueOf(sKey)=="PCI"){
                                    TextView temp = (TextView) findViewById(R.id.spci);
                                    temp.setText(sobj.getString(String.valueOf(sKey)));}
                            else if(String.valueOf(sKey)=="RSRQ"){
                                    TextView temp = (TextView) findViewById(R.id.srsrq);
                                    temp.setText(sobj.getString(String.valueOf(sKey)));}
                            else if(String.valueOf(sKey)=="RSRP"){
                                    TextView temp = (TextView) findViewById(R.id.srsrp);
                                    temp.setText(sobj.getString(String.valueOf(sKey)));}
                            else if(String.valueOf(sKey)=="RSSI"){
                                    TextView temp = (TextView) findViewById(R.id.srssi);
                                    temp.setText(sobj.getString(String.valueOf(sKey)));}
                            else if(String.valueOf(sKey)=="RXLV"){
                                    TextView temp = (TextView) findViewById(R.id.srxlv);
                                    temp.setText(sobj.getString(String.valueOf(sKey)));}
                        }
                    }

                }catch (JSONException e){
                    System.out.println("Could not extract serving object -->");
                    e.printStackTrace();
                    break;
                }
                break;

            case "interfreq":
                try {
                    JSONArray itearr = dataset.getJSONArray("interfreq");
                    if(itearr!=null){
                        JSONObject iteobj= itearr.getJSONObject(0);
                        Iterator<?> iteKey = iteobj.keys();
                        while (iteKey.hasNext()){
                            if (String.valueOf(iteKey)=="EARFCN"){
                                TextView temp = (TextView) findViewById(R.id.iteearfcn);
                                temp.setText(iteobj.getString(String.valueOf(iteKey)));
                            }
                            else if (String.valueOf(iteKey)=="ThresholdLow"){
                                TextView temp = (TextView) findViewById(R.id.thresholdlow);
                                temp.setText(iteobj.getString(String.valueOf(iteKey)));
                            }
                            else if (String.valueOf(iteKey)=="ThresholdHi"){
                                TextView temp = (TextView) findViewById(R.id.thresholdhi);
                                temp.setText(iteobj.getString(String.valueOf(iteKey)));
                            }
                            else if (String.valueOf(iteKey)=="Priority"){
                                TextView temp = (TextView) findViewById(R.id.priority);
                                temp.setText(iteobj.getString(String.valueOf(iteKey)));
                            }
                            else if (String.valueOf(iteKey)=="PCI"){
                                TextView temp = (TextView) findViewById(R.id.itepci);
                                temp.setText(iteobj.getString(String.valueOf(iteKey)));
                            }
                            else if (String.valueOf(iteKey)=="RSRQ"){
                                TextView temp = (TextView) findViewById(R.id.itersrq);
                                temp.setText(iteobj.getString(String.valueOf(iteKey)));
                            }
                            else if (String.valueOf(iteKey)=="RSRP"){
                                TextView temp = (TextView) findViewById(R.id.itersrp);
                                temp.setText(iteobj.getString(String.valueOf(iteKey)));
                            }
                            else if (String.valueOf(iteKey)=="RSSI"){
                                TextView temp = (TextView) findViewById(R.id.iterssi);
                                temp.setText(iteobj.getString(String.valueOf(iteKey)));
                            }
                            else if (String.valueOf(iteKey)=="RXLV"){
                                TextView temp = (TextView) findViewById(R.id.iterxlv);
                                temp.setText(iteobj.getString(String.valueOf(iteKey)));
                            }
                        }
                    }

                }catch (JSONException e){
                    System.out.println("Could not extract interfreq object -->");
                    e.printStackTrace();
                    break;
                }
                break;

            case "intrafreq":
                try {
                    JSONArray itaarr = dataset.getJSONArray("intrafreq");
                    if(itaarr!=null){
                        JSONObject itaobj1= itaarr.getJSONObject(0);
                        Iterator<?> itaKey = itaobj1.keys();
                        while (itaKey.hasNext()){
                            if (String.valueOf(itaKey)=="PCI"){
                                TextView temp = (TextView) findViewById(R.id.itapci1);
                                temp.setText(itaobj1.getString(String.valueOf(itaKey)));
                            }
                            else if (String.valueOf(itaKey)=="RSRQ"){
                                TextView temp = (TextView) findViewById(R.id.itarsrq1);
                                temp.setText(itaobj1.getString(String.valueOf(itaKey)));
                            }
                            else if (String.valueOf(itaKey)=="RSRP"){
                                TextView temp = (TextView) findViewById(R.id.itarsrp1);
                                temp.setText(itaobj1.getString(String.valueOf(itaKey)));
                            }
                            else if (String.valueOf(itaKey)=="RSSI"){
                                TextView temp = (TextView) findViewById(R.id.itarssi1);
                                temp.setText(itaobj1.getString(String.valueOf(itaKey)));
                            }
                            else if (String.valueOf(itaKey)=="RXLV"){
                                TextView temp = (TextView) findViewById(R.id.itarxlv1);
                                temp.setText(itaobj1.getString(String.valueOf(itaKey)));
                            }

                        }
                        JSONObject itaobj2= itaarr.getJSONObject(1);
                        Iterator<?> itaKey2 = itaobj2.keys();
                        while (itaKey.hasNext()){
                            if (String.valueOf(itaKey2)=="PCI"){
                                TextView temp = (TextView) findViewById(R.id.itapci2);
                                temp.setText(itaobj2.getString(String.valueOf(itaKey)));
                            }
                            else if (String.valueOf(itaKey2)=="RSRQ"){
                                TextView temp = (TextView) findViewById(R.id.itarsrq2);
                                temp.setText(itaobj2.getString(String.valueOf(itaKey)));
                            }
                            else if (String.valueOf(itaKey2)=="RSRP"){
                                TextView temp = (TextView) findViewById(R.id.itarsrp2);
                                temp.setText(itaobj2.getString(String.valueOf(itaKey)));
                            }
                            else if (String.valueOf(itaKey2)=="RSSI"){
                                TextView temp = (TextView) findViewById(R.id.itarssi2);
                                temp.setText(itaobj2.getString(String.valueOf(itaKey)));
                            }
                            else if (String.valueOf(itaKey2)=="RXLV"){
                                TextView temp = (TextView) findViewById(R.id.itarxlv2);
                                temp.setText(itaobj2.getString(String.valueOf(itaKey)));
                            }

                        }
                    }

                }catch (JSONException e){
                    System.out.println("Could not extract interfreq object -->");
                    e.printStackTrace();
                    break;
                }
                break;

            default:
                break;


        }

    }


}




/*

    public void SetValuesInTable(JSONObject jobj) throws JSONException{
        JSONObject dataset = jobj;
        Iterator<?> keys = dataset.keys();


        //TableLayout mTable = (TableLayout) findViewById(R.id.detailsTable);
        //mTable.removeAllViews();
        while(keys.hasNext()) {

            String mKey = (String) keys.next();

            switch (mKey) {
                case "location":
                    JSONObject locObject = dataset.getJSONObject("location");
                    Iterator<?> locKey = locObject.keys();

                    while (locKey.hasNext()){
                        TableLayout detailsTable = (TableLayout) findViewById(R.id.detailsTable);

                        TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row, null);
                        TextView tv;
                        String iKey = (String) locKey.next();
                        tv = (TextView) tableRow.findViewById(R.id.dataKey);
                        tv.setText(iKey);
                        tv = (TextView) tableRow.findViewById(R.id.dataValue);
                        tv.setText(locObject.getString(iKey));

                        detailsTable.addView(tableRow);
                    }

                    break;

                case "intrafreq":
                    JSONObject inaObject = dataset.getJSONArray("intrafreq").getJSONObject(0);
                    Iterator<?> inaKey = inaObject.keys();

                    while (inaKey.hasNext()){
                        TableLayout detailsTable = (TableLayout) findViewById(R.id.detailsTable);

                        TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row, null);
                        TextView tv;
                        String iKey = (String) inaKey.next();
                        tv = (TextView) tableRow.findViewById(R.id.dataValue);
                        tv.setText(iKey);
                        tv = (TextView) tableRow.findViewById(R.id.dataKey);
                        tv.setText(inaObject.getString(iKey));

                        detailsTable.addView(tableRow);
                    }

                    break;

                case "interfreq":
                    JSONObject ineObject = dataset.getJSONArray("interfreq").getJSONObject(0);
                    Iterator<?> ineKey = ineObject.keys();

                    while (ineKey.hasNext()){
                        TableLayout detailsTable = (TableLayout) findViewById(R.id.detailsTable);

                        TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row, null);
                        TextView tv;
                        String iKey = (String) ineKey.next();
                        tv = (TextView) tableRow.findViewById(R.id.dataValue);
                        tv.setText(iKey);
                        tv = (TextView) tableRow.findViewById(R.id.dataKey);
                        tv.setText(ineObject.getString(iKey));

                        detailsTable.addView(tableRow);
                    }

                case "serving":
                    JSONObject servObject = dataset.getJSONArray("serving").getJSONObject(0);
                    Iterator<?> sKey = servObject.keys();

                    while (sKey.hasNext()){
                         TableLayout detailsTable = (TableLayout) findViewById(R.id.detailsTable);

                        TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row, null);
                        TextView tv;
                        String iKey = (String) sKey.next();
                        tv = (TextView) tableRow.findViewById(R.id.dataValue);
                        tv.setText(iKey);
                        tv = (TextView) tableRow.findViewById(R.id.dataKey);
                        tv.setText(servObject.getString(iKey));

                        detailsTable.addView(tableRow);
                    }

                case "gstatus":
                    JSONObject gstatObject = dataset.getJSONObject("gstatus");
                    Iterator<?> gKey = gstatObject.keys();

                    while (gKey.hasNext()){
                        TableLayout detailsTable = (TableLayout) findViewById(R.id.detailsTable);

                        TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row, null);
                        TextView tv;
                        String iKey = (String) gKey.next();
                        tv = (TextView) tableRow.findViewById(R.id.dataKey);
                        tv.setText(iKey);
                        tv = (TextView) tableRow.findViewById(R.id.dataValue);
                        tv.setText(gstatObject.getString(iKey));

                        detailsTable.addView(tableRow);
                    }
                default:

                    break;
            }


        }


        //final TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.table_row, null);





    }*/




    public class ListViewAdapter extends BaseAdapter {

        ConnectionManager conMan;
        private Context context;

        public ListViewAdapter(Context c,ConnectionManager conMan) {
            this.conMan = conMan;
            context = c;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.clients_list, parent , false);
            Button button = (Button) row.findViewById(R.id.detailsButton);
            TextView tv_hostname = (TextView) row.findViewById(R.id.client_name);
            TextView tv_clientIP = (TextView) row.findViewById(R.id.client_ip);



            final Connection con = conMan.getConnections().get(position);

            button.setOnClickListener(new View.OnClickListener(){

                public void onClick(View v){

                    for(Connection con : conMan.getConnections()){

                        con.setFocus(false);

                    }
                    con.setFocus(true);
                }
            });

            tv_hostname.setText(con.getName());
            tv_clientIP.setText(con.getIP());


            if (con.isFocus()) {

                button.setBackgroundColor(Color.GREEN);
                button.setText("Selected");
            } else {

                button.setBackgroundColor(Color.GRAY);
                button.setText("--");

            }

            return row;
        }

        @Override
        public int getCount() {
            return cons.size();
        }

        @Override
        public Object getItem(int position) {
            return cons.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
    public void expandableButton1(View view) {
        expandableLayout1 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout1);
        expandableLayout1.toggle(); // toggle expand and collapse
    }

    public void expandableButton2(View view) {
        expandableLayout2 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout2);
        expandableLayout2.toggle(); // toggle expand and collapse
    }

    public void expandableButton3(View view) {
        expandableLayout3 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout3);
        expandableLayout3.toggle(); // toggle expand and collapse
    }

    public void expandableButton4(View view) {
        expandableLayout4 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout4);
        expandableLayout4.toggle(); // toggle expand and collapse
    }

    public void expandableButton5(View view) {
        expandableLayout5 = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout5);
        expandableLayout5.toggle(); // toggle expand and collapse
    }





















}
