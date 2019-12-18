package com.baldino.myapp003.activities;

import android.os.Bundle;

import com.baldino.myapp003.R;
import com.baldino.myapp003.singletons.IngredientManagerSingleton;
import com.baldino.myapp003.singletons.RecipeManagerSingleton;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.baldino.myapp003.singletons.WeekManagerSingleton;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //----//
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_day_format, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //----//

        IngredientManagerSingleton sIngredientManager = IngredientManagerSingleton.getInstance();
        RecipeManagerSingleton sRecipeManager = RecipeManagerSingleton.getInstance();
        WeekManagerSingleton sWeekManager = WeekManagerSingleton.getInstance();

        sIngredientManager.setContext(this);
        sRecipeManager.setContext(this);
        sWeekManager.setContext(this);

        sIngredientManager.loadData();

        sRecipeManager.loadTypeNames();
        sRecipeManager.loadFirstCourses();
        sRecipeManager.loadSecondCourses();
        sRecipeManager.loadSideDishes();

        sWeekManager.loadWeeks();
        sWeekManager.loadDailyMeals();
        sWeekManager.loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // NdP: attualmente vuoto
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
