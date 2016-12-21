package cs443.project;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends Activity {

    private GridView gridView;

    private static int w=5, destination, count =0, score =0, time = 50;

    private Random r=new Random();

    private static Integer[] numbers = new Integer[w*w];

    private int mole = R.drawable.mole;

    private int blank = android.R.color.transparent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView1);

        for(int i=0;i<numbers.length;i++)
            numbers[i]= blank;


        ArrayAdapter<Integer> adapter = new ImageAdapter(this,
                numbers);

        gridView.setAdapter(adapter);

        Thread treasure = new Thread(treasureGen);
        Thread timer = new Thread(timerThread);
        treasure.start();
        timer.start();


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                destination = position;
                whack();
                
                scoreView.setText(score);

                //Toast.makeText(getApplicationContext(),
                        Integer.toString(score),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void reset(View view){
        init();
    }

    //This button is made to quit the activity, and returns back to MenuActivity
    public void quit(View v) {
        finish();
    }

    private void init(){
        for(int i=0;i<numbers.length;i++)
            numbers[i]= blank;
        count =0;
        score =0;

        ((ArrayAdapter)gridView.getAdapter()).notifyDataSetChanged();
    }

    //HANDLER AND THREAD CODE HERE
    private Handler threadHandler = new Handler() {
        public void handleMessage (android.os.Message message){
            switch (message.what) {
                case 0:
                ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                    break;

                case 1:
                    //create a text view to monitor time
                    countTextView.setText(time);
                    break;

                case 2:
                    finish();
            }
        }


    };

    private void whack(){
            Integer y = destination/5;
            Integer x = destination%5;

            if(numbers[y * w + x] == mole)
            {
                score++;
                threadHandler.sendEmptyMessage(0);
                numbers[y * w + x] = blank;
            }
        }

    private Runnable treasureGen = new Runnable(){
        private static final int INTERVAL = 4000;
        public void run()
        {
            try {
                while (true) {

                    //amount of moles on next refresh. Example code from 1 to 3 each.
                    int moles = r.nextInt(2) +1;

                    for(int i=0;i<numbers.length;i++)
                        numbers[i]=blank;

                    for(count =0; count < moles;) {
                        int tx = r.nextInt(w);
                        int ty = r.nextInt(w);
                        if (numbers[ty * w + tx] != mole) {
                            numbers[ty * w + tx] = mole;
                            count++;
                            time = time +5;
                        }
                        threadHandler.sendEmptyMessage(0);
                    }
                    Thread.sleep(INTERVAL);
                }

            } catch (InterruptedException e){
                e.printStackTrace();
            }

        }
    };

    private Runnable timerThread = new Runnable () {
        private static final int DELAY = 900;
        public void run() {
            try {
                while (time !=0) {
                    time--;
                    Thread.sleep (DELAY);
                    threadHandler.sendEmptyMessage(1);
                }

                threadHandler.sendEmptyMessage(2);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    };


    class ImageAdapter extends ArrayAdapter<Integer> {
        private Context mcontext;
        private Integer[] array;

        ImageAdapter(Context context, Object[] objects) {

            super(context, R.layout.list_item, (Integer[]) objects);
            mcontext = context;
            array = (Integer[]) objects;
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            ImageView image;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                image = new ImageView(mcontext);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                image = (ImageView) convertView;
            }

            image.setImageResource(array[position]);
            return image;
        }
    }
}
