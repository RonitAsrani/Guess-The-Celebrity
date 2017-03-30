package com.example.asrani.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

   // int unchosenceleb1 = 0;
   // int unchosenceleb2 = 0;
   // int unchosenceleb3 = 0;


    //create arraylist to store the list of urls and names of the celebs
    ArrayList<String> celeburl = new ArrayList<String>();
    ArrayList<String> celebnames = new ArrayList<String>();
    int chosenceleb = 0;
    ImageView imageView;
    int locationOfCorrectAnswer = 0;
    String[] answers = new String[4];
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void celebchosen(View view) {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {
            Toast.makeText(getApplicationContext(), "Correct Answer", Toast.LENGTH_LONG).show();
            createnewquestion();

        } else {
            Toast.makeText(getApplicationContext(), "Wrong, the answer was " + celebnames.get(chosenceleb), Toast.LENGTH_LONG).show();
        }
    }

    public class imagedownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                Bitmap mybitmap = BitmapFactory.decodeStream(in);
                return mybitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // send string to it, nothing do while downloading, return String

    public class downloadtask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = null;
            URL url = null;
            try {
                url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result = result + current;
                    data = reader.read();
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.img);
        button0 = (Button) findViewById(R.id.button1);
        button1 = (Button) findViewById(R.id.button2);
        button2 = (Button) findViewById(R.id.button3);
        button3 = (Button) findViewById(R.id.button4);
        downloadtask task = new downloadtask();
        String result1;
        try {
            result1 = task.execute("http://www.posh24.se/kandisar").get();
            //  Log.i("Result",result1);
            String splitresult[] = result1.split("<div class=\"sidebarContainer\">");
            Log.i("Result", String.valueOf(splitresult));
// if we search only for src, then we will get incorrect results for the options
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitresult[0]);
            while (m.find()) {
                celeburl.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitresult[0]);
            while (m.find()) {
                celebnames.add(m.group(1));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        createnewquestion();
    }

    // Log.i("Contents of URL", result);

    public void createnewquestion() {

        Random random = new Random();
        chosenceleb = random.nextInt(celeburl.size());
       // unchosenceleb1 = random.nextInt(celebnames.size());
       // unchosenceleb2 = random.nextInt(celebnames.size());
       // unchosenceleb3 = random.nextInt(celebnames.size());


        imagedownloader imgtask = new imagedownloader();
        Bitmap celeb;
        try {
            celeb = imgtask.execute(celeburl.get(chosenceleb)).get();
            imageView.setImageBitmap(celeb);
            locationOfCorrectAnswer = random.nextInt(4);
            int incorrectanswerlocation;
            for (int i = 0; i < answers.length; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = celebnames.get(chosenceleb);


                } else {

                    incorrectanswerlocation = random.nextInt(celeburl.size());
                    answers[i] = celebnames.get(incorrectanswerlocation);
                    while (incorrectanswerlocation == chosenceleb) {
                        incorrectanswerlocation = random.nextInt(celeburl.size());
                    }
                }
               button0.setText(answers[0]);
               //button0.setText("" + celebnames.get(chosenceleb));
               // button1.setText(""+celebnames.get(unchosenceleb1));
               // button2.setText(""+celebnames.get(unchosenceleb2));
               // button3.setText(""+celebnames.get(unchosenceleb3));
                button1.setText(answers[1]);
                button2.setText(answers[2]);
                button3.setText(answers[3]);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

