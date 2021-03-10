package com.pd.beertimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.pd.beertimer.room.Drink
import com.pd.beertimer.room.DrinkDao
import com.pd.beertimer.util.AlarmUtils
import com.pd.beertimer.util.CHANNEL_ID
import com.pd.beertimer.util.SHARED_PREF_ROOM_INIT
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.joery.animatedbottombar.AnimatedBottomBar
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private var menu: Menu? = null
    private val sharedPreferences: SharedPreferences by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setUpBottomBar()
        createNotificationChannel()
        setupToolbar()
        initRoom()


        if (savedInstanceState == null) {
            if (AlarmUtils(this).getExistingDrinkTimesFromSharedPref() != null) {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_countDownFragment)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            menu.findItem(R.id.action_info).iconTintList = this.getColorStateList(R.color.selector)
        }
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_info -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_infoFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(this)
    }

    override fun onStop() {
        super.onStop()
        findNavController(R.id.nav_host_fragment).removeOnDestinationChangedListener(this)

    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener {
            findNavController(R.id.nav_host_fragment).popBackStack()
        }
    }

    private fun initRoom() {
        if (!sharedPreferences.getBoolean(SHARED_PREF_ROOM_INIT, false)) {
            GlobalScope.launch {
            getKoin().get<DrinkDao>().insertAll(
                Drink(
                    name = "Small Beer",
                    volume = 0.33F,
                    percentage = 0.047F,
                    iconName = "ic_beer_small"
                ),
                Drink(
                    name = "Large Beer",
                    volume = 0.50F,
                    percentage = 0.047F,
                    iconName = "ic_beer"
                ),
                Drink(
                    name = "Wine",
                    volume = 0.150F,
                    percentage = 0.125F,
                    iconName = "ic_wine"
                )
            )
            }
            sharedPreferences.edit().putBoolean(SHARED_PREF_ROOM_INIT, true).apply()
        }
    }

    private fun setUpBottomBar() {
        bottom_bar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                when (newTab.id) {
                    R.id.tab_home -> findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_startDrinkingFragment)
                    R.id.tab_timer -> findNavController(R.id.nav_host_fragment).navigate(R.id.countDownFragment)
                    R.id.tab_profile -> findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_meFragment)
                    else -> throw ClassNotFoundException()
                }
            }

        })
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        bottom_bar.visibility = View.VISIBLE

        this.menu?.findItem(R.id.action_info)?.isVisible = true
        when (destination.id) {
            R.id.countDownFragment -> bottom_bar.selectTabById(R.id.tab_timer)
            R.id.startDrinkingFragment -> bottom_bar.selectTabById(R.id.tab_home)
            R.id.meFragment -> bottom_bar.selectTabById(R.id.tab_profile)
            R.id.infoFragment, R.id.profileFragment, R.id.myDrinksFragment -> {
                this.menu?.findItem(R.id.action_info)?.isVisible = false
                bottom_bar.visibility = View.GONE
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setDisplayShowHomeEnabled(true)
            }
        }
    }
}
