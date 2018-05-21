/*
 This file is part of Privacy Friendly App Example.

 Privacy Friendly App Example is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly App Example is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly App Example. If not, see <http://www.gnu.org/licenses/>.
 */

package org.secuso.privacyfriendlyexample.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlyexample.R;
import org.secuso.privacyfriendlyexample.activities.helper.BaseActivity;
import org.secuso.privacyfriendlyexample.helpers.FirstLaunchManager;

import java.io.File;

/**
 * @author Christopher Beckmann, Karola Marky
 * @version 20171016
 */
public class MainActivity extends BaseActivity {

    private ViewPager viewPager;
    private MainActivity.MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private ImageButton btnSkip, btnNext;
    private FirstLaunchManager firstLaunchManager;
    private int currentPage = 0;


    private int[] layouts = new int[]{
            R.layout.choose_slide1,
            R.layout.choose_slide2,
            R.layout.choose_slide3,
            R.layout.choose_slide4,
    };
    private boolean[] gameResumeable = new boolean[]{
            false,
            false,
            false,
            false
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        overridePendingTransition(0, 0);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        firstLaunchManager = new FirstLaunchManager(this);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (ImageButton) findViewById(R.id.btn_skip);
        btnNext = (ImageButton) findViewById(R.id.btn_next);



        //checking resumable
        File directory = getFilesDir();
        File [] files = directory.listFiles();

        for(int i = 0; i < files.length;i++)
        {
            Log.i("files",files[i].getName());
            for(int j = 0; j < gameResumeable.length;j++)
            {
                if(files[i].getName().equals("state" + (j+4) + ".txt"))
                    gameResumeable[j] = true;
            }
        }
        for(boolean b :gameResumeable)
        {
            Log.i("files ","found: " + b);
        }


        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MainActivity.MyViewPagerAdapter();

        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
            btnSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current = getItem(-1);
                    if (current >= 0) {
                        // move to next screen
                        viewPager.setCurrentItem(current);
                    } else {
                    }
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // checking for last page
                    // if last page home screen will be launched
                    int current = getItem(+1);
                    if (current < layouts.length) {
                        // move to next screen
                        viewPager.setCurrentItem(current);
                    } else {
                    }
                }
            });
    }
    private void addListener(Button b1,Button b2,int n)
    {
        final int temp = n;
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("n",temp);
                intent.putExtra("points",0);
                intent.putExtra("new",true);
                intent.putExtra("filename","state"+temp+".txt");
                createBackStack(intent);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("setting",""+temp);
                Intent  intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("n",temp);
                intent.putExtra("new",false);
                intent.putExtra("filename","state"+temp+".txt");
                createBackStack(intent);
            }
        });
    }
    private void createBackStack(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder builder = TaskStackBuilder.create(this);
            builder.addNextIntentWithParentStack(intent);
            builder.startActivities();
        } else {
            startActivity(intent);
            finish();
        }
    }
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int activeColor = ContextCompat.getColor(this, R.color.dot_light_screen);
        int inactiveColor = ContextCompat.getColor(this, R.color.dot_dark_screen);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(inactiveColor);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(activeColor);
    }
    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            currentPage = position;

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Log.i("position", ""+position);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            //newGameButtons
            Button newGameButton;
            switch (position){
                case 0:
                    newGameButton = view.findViewById(R.id.button_newGame4x4);
                    break;
                case 1:
                    newGameButton = view.findViewById(R.id.button_newGame5x5);
                    break;
                case 2:
                    newGameButton = view.findViewById(R.id.button_newGame6x6);
                    break;
                case 3:
                    newGameButton = view.findViewById(R.id.button_newGame7x7);
                    break;
                default:
                    newGameButton = new Button(MainActivity.this);
            }

            //continueButton
            Button continueButton;
            switch (position){
                case 0:
                    continueButton = view.findViewById(R.id.button_continueGame4x4);
                    break;
                case 1:
                    continueButton = view.findViewById(R.id.button_continueGame5x5);
                    break;
                case 2:
                    continueButton = view.findViewById(R.id.button_continueGame6x6);
                    break;
                case 3:
                    continueButton = view.findViewById(R.id.button_continueGame7x7);
                    break;
                default:
                    continueButton = new Button(MainActivity.this);
            }
            try {
                continueButton.setEnabled(gameResumeable[position]);
                Log.i("Button", "activated = " + gameResumeable[position]);
            }
            catch(ArrayIndexOutOfBoundsException aie)
            {
                aie.printStackTrace();
            }
            //highScoreButtons
            Button highScoreButton;
            switch (position){
                case 0:
                    highScoreButton = view.findViewById(R.id.button_highScore4x4);
                    break;
                case 1:
                    highScoreButton = view.findViewById(R.id.button_highScore5x5);
                    break;
                case 2:
                    highScoreButton = view.findViewById(R.id.button_highScore6x6);
                    break;
                case 3:
                    highScoreButton = view.findViewById(R.id.button_highScore7x7);
                    break;
                default:
                    highScoreButton = new Button(MainActivity.this);
            }
            highScoreButton.setEnabled(false);

            addListener(newGameButton,continueButton,position+4);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    /**
     * This method connects the Activity to the menu item
     * @return ID of the menu item it belongs to
     */
    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_example;
    }

}