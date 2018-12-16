package com.example.emre.dovizkuru;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ListActivity {


    ListView lv;
    Context context = this;

    private List<String> nationList;
    private List<String> unitList;
    private List<String> buyingList;
    private List<String> sellingList;
    private List<String> KodList;
    private ConstraintLayout layout2;
    private Button refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(android.R.id.list);
        layout2 = findViewById(R.id.Layout2);
        layout2.setVisibility(View.INVISIBLE);

        nationList = new ArrayList<String>();
        unitList = new ArrayList<String>();
        buyingList = new ArrayList<String>();
        sellingList = new ArrayList<String>();
        KodList = new ArrayList<String>();

        refreshButton = findViewById(R.id.buttonRefresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSetData();
            }
        });


        getSetData();

    }



    private void getData()
    {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                try{

                    KodList.clear();
                    nationList.clear();
                    unitList.clear();
                    buyingList.clear();
                    sellingList.clear();


                    URL url = new URL("http://tcmb.gov.tr/kurlar/today.xml");
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser parser = factory.newPullParser();
                    parser.setInput(url.openConnection().getInputStream(), "UTF_8");
                    boolean insideItem = false;

                    int eventType = parser.getEventType();
                    while(eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            if (parser.getName().equalsIgnoreCase("Currency")) {
                                KodList.add(
                                        parser.getAttributeValue(null, "Kod"));
                                insideItem = true;
                            } else if (parser.getName().equalsIgnoreCase("Unit")) {
                                if (insideItem)
                                    unitList.add(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("CurrencyName")) {
                                if (insideItem)
                                    nationList.add(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("ForexBuying")) {
                                if (insideItem)
                                    buyingList.add(parser.nextText());
                            } else if (parser.getName().equalsIgnoreCase("ForexSelling")) {
                                if (insideItem)
                                    sellingList.add(parser.nextText());
                            }
                        } else if (eventType == XmlPullParser.END_TAG &&
                                parser.getName().equalsIgnoreCase("Currency")) {
                            insideItem = false;
                        }
                        eventType = parser.next();
                    }

                }
                catch(MalformedURLException ex) {
                    ex.printStackTrace();
                }
                catch(XmlPullParserException ex){
                    ex.printStackTrace();
                }
                catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        });

        thread.start();
    }


    private void setData()
    {

        try {
            //for correct thread handling
            Handler refresh = new Handler(Looper.getMainLooper());
            refresh.post(new Runnable() {
                public void run()
                {
                    List<String> tempList = new ArrayList<String>();

                    //List size control.
                    if(KodList.size() == 0)
                    {
                        layout2.setVisibility(View.VISIBLE);
                        return;
                    }
                    else
                    {
                        layout2.setVisibility(View.INVISIBLE);
                    }


                    for(int i=0; i<KodList.size();i++) {
                        tempList.add(KodList.get(i)+
                                "\t\t  ..-..  "+unitList.get(i)+
                                "\t\t  ..-..  "+buyingList.get(i)+
                                "\t\t  ..-..  "+sellingList.get(i));
                    }
                    ArrayAdapter adapter = new
                            ArrayAdapter(context, android.R.layout.simple_list_item_1, tempList);
                    //setListAdapter(adapter);
                    lv.setAdapter(adapter);
                }
            });

        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

    }


    private void getSetData()
    {
        getData();

        Timer timer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                setData();
            }
        };
        //delay of data receiving
        timer.schedule(t,700);
    }




}
