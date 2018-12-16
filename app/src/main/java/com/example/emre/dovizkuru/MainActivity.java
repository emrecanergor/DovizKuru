package com.example.emre.dovizkuru;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(android.R.id.list);

        nationList = new ArrayList<String>();
        unitList = new ArrayList<String>();
        buyingList = new ArrayList<String>();
        sellingList = new ArrayList<String>();
        KodList = new ArrayList<String>();


            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {

                    try{

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




        Timer timer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {

                try {
                    //for correct thread handling
                    Handler refresh = new Handler(Looper.getMainLooper());
                    refresh.post(new Runnable() {
                        public void run()
                        {
                            List<String> tempList = new ArrayList<String>();

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
        };
        //delay of data receiving
        timer.schedule(t,400);


    }


}
