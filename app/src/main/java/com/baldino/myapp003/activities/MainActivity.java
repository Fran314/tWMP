package com.baldino.myapp003.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.baldino.myapp003.R;
import com.baldino.myapp003.Util;
import com.baldino.myapp003.singletons.Database;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.baldino.myapp003.singletons.ShoppingListSingleton;
import com.baldino.myapp003.singletons.WeekManagerSingleton;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.ContactsContract;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Menu;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    WeekManagerSingleton sWeekManager;
    ShoppingListSingleton sShoppingList;

    Database d;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //--- The following block of code should always be the first to be run, before
        //    anything else, and should stay in this order, too. Not strictly, but it's better if
        //    it stays this way ---//
        Util.context = this;

        sWeekManager = WeekManagerSingleton.getInstance();
        sShoppingList = ShoppingListSingleton.getInstance();

        d = Database.getInstance();

        sWeekManager.setContext(this);
        sShoppingList.setContext(this);

        d.setContext(this);
        //--- ---//

        //--- Code created by the toolkit. Don't really know what it does. Don't touch it ---//
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_recipes, R.id.nav_ingredients,
                R.id.nav_day_format, R.id.nav_weeks_data,
                R.id.nav_info, R.id.nav_settings, R.id.nav_export).setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //--- ---//

        final MainActivity mainActivity = this;

        if(Util.isFirstStart())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.dialog_title_first_start));

            //  These are split in different blocks so the bolding part is easier
            String intro = getResources().getString(R.string.dialog_text_first_start_intro);
            String fresh_start = getResources().getString(R.string.dialog_text_first_start_fresh_start);
            String fs_explain = " " + getResources().getString(R.string.dialog_text_first_start_fs_explain);
            String basic_template = getResources().getString(R.string.dialog_text_first_start_basic_template);
            String bt_explain = " " + getResources().getString(R.string.dialog_text_first_start_bt_explain);

            int start, end;
            SpannableStringBuilder  dialog_body = new SpannableStringBuilder ();
            dialog_body.append(intro);

            start = dialog_body.length();
            dialog_body.append(fresh_start);
            end = dialog_body.length();
            dialog_body.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            dialog_body.append(fs_explain);

            start = dialog_body.length();
            dialog_body.append(basic_template);
            end = dialog_body.length();
            dialog_body.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            dialog_body.append(bt_explain);

            builder.setMessage(dialog_body);
            builder.setPositiveButton(getResources().getString(R.string.dialog_button_first_start_basic_template), new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    Util.createInitFiles();

                    dialog.dismiss();

                    Intent intent = mainActivity.getIntent();
                    mainActivity.finish();
                    startActivity(intent);
                }
            });

            builder.setNegativeButton(getResources().getString(R.string.dialog_button_first_start_fresh_start), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // Do nothing
                    dialog.dismiss();
                }
            });
            builder.setCancelable(false);

            AlertDialog alert = builder.create();
            alert.show();
        }

        Util.loadSettings();

        sWeekManager.loadWeeks();
        sWeekManager.loadDailyMeals();
        sWeekManager.loadData();

        sShoppingList.updateShoppingList();
        sShoppingList.loadValues();

        d.loadAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // Currently empty. I don't wanna delete it just yet, just in case I end up using it yknow?
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
