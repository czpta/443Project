package cs443.project;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends Activity {

    GridView gridView;

    private static int w=5, destination, count =0, score =0;

    private Random r=new Random();

    static Integer[] numbers = new Integer[w*w];

    int mole = R.drawable.mole;

    int blank = android.R.color.transparent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView1);

        for(int i=0;i<numbers.length;i++)
            numbers[i]= blank;


        ArrayAdapter<Integer> adapter = new ImageAdapter(this,
                R.layout.list_item, numbers);

        gridView.setAdapter(adapter);

        Thread treasure = new Thread(treasureGen);
        treasure.start();


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                destination = position;
                whack();

                Toast.makeText(getApplicationContext(),
                        (CharSequence) (new Integer(score).toString()),
                        Toast.LENGTH_SHORT).show();

            }
        });


    }

    public void reset(View view){
        init();
    }

    void init(){
        for(int i=0;i<numbers.length;i++)
            numbers[i]= blank;
        count =0;
        score =0;

        ((ArrayAdapter)gridView.getAdapter()).notifyDataSetChanged();
    }

    //HANDLER AND THREAD CODE HERE
    public Handler threadHandler = new Handler() {
        public void handleMessage (android.os.Message message){
            ((ArrayAdapter)gridView.getAdapter()).notifyDataSetChanged();
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
        private static final int INTERVAL = 6000;
        public void run()
        {
            try {
                while (true) {

                    //amount of moles on next refresh. Example code from 1 to 3 each.
                    int moles = r.nextInt(2) +1;

                    for(int i=0;i<numbers.length;i++)
                        numbers[i]=blank;

                    for(count =0; count < moles; count++) {
                        int tx = r.nextInt(w);
                        int ty = r.nextInt(w);
                        numbers[ty * w + tx] = mole;
                        threadHandler.sendEmptyMessage(0);
                    }
                    Thread.sleep(INTERVAL);
                }

            } catch (InterruptedException e){
                e.printStackTrace();
            }

        }
    };


    class ImageAdapter extends ArrayAdapter<Integer> {
        private Context mcontext;
        private Integer[] array;

        public ImageAdapter(Context context, int resource, Object[] objects) {

            super(context, resource, (Integer[]) objects);
            mcontext = context;
            array = (Integer[]) objects;
        }


        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
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


