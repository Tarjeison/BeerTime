package com.example.beertime

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.example.beertime.feature.countdown.CountDownController
import com.example.beertime.models.DrinkingCalculation
import com.example.beertime.util.CHANNEL_ID

import kotlinx.android.synthetic.main.activity_main.*
import nl.joery.animatedbottombar.AnimatedBottomBar
import org.koin.android.ext.android.inject
import java.util.*

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private var notificationBuilder: NotificationCompat.Builder? = null
    private val countDownController: CountDownController by inject()

    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setUpBottomBar()
        notificationBuilder = createNotificationChannel()
        observeNotificationLiveData()
    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
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
                    R.id.tab_settings -> findNavController(R.id.nav_host_fragment).navigate(R.id.countDownFragment)
                    R.id.tab_profile -> findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_profileFragment)
                    else -> throw ClassNotFoundException()
                }
            }

        })
    }

    private fun observeNotificationLiveData() {
        countDownController.getNotificationLiveData().observe(this, androidx.lifecycle.Observer {
            if (this::countDownTimer.isInitialized) {
                countDownTimer.cancel()
            }
            createCountDownTimer(it)
        })
    }

    private fun createCountDownTimer(calculation: DrinkingCalculation) {
        countDownTimer = object : CountDownTimer(calculation.d.toMillis(), 1000) {
            override fun onFinish() {
                createNotification()

                if (calculation.num > 0) {
                    calculation.num--
                    createCountDownTimer(calculation)
                }

            }

            override fun onTick(millisUntilFinsihed: Long) {
                Log.d("COUNTODWN", millisUntilFinsihed.toString())
                countDownController.getCountDownLiveData().postValue(countDownController.timeString(millisUntilFinsihed))
            }

        }.start()

    }

    private fun createNotificationChannel(): NotificationCompat.Builder? {
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

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_icon_beer)
            .setContentTitle(this.getText(R.string.notification_title))
            .setContentText(this.getString(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    private fun createNotification() {
        Log.d("MAIN", "Not1")
        notificationBuilder?.let {
            Log.d("MAIN", "Not2")
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                it.setWhen(System.currentTimeMillis())
                notify(Random().nextInt(), it.build())
            }
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.countDownFragment -> bottom_bar.selectTabById(R.id.tab_settings)
            R.id.startDrinkingFragment -> bottom_bar.selectTabById(R.id.tab_home)
            R.id.profileFragment -> bottom_bar.selectTabById(R.id.tab_profile)
        }
        toolbar.title = destination.label
    }
}
