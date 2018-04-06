package com.cpxiao.life.progress.activity

import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import com.cpxiao.lib.Config
import com.cpxiao.life.progress.R
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.content_home.*
import java.util.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    val DEBUG = Config.Log.DEBUG
    val TAG = "HomeActivity"

    val random = Random()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        fab.visibility = View.GONE


        batteryView.setOnClickListener(this)
        if (batteryView.getBatteryCount() == 0) {
            showSetupDateButton()
        } else {
            hideSetupDateButton()
        }

        setMsgTextView()

        setupDateButton.setOnClickListener(this)
        changeDateButton.setOnClickListener(this)
        clearDateButton.setOnClickListener(this)


        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setMsgTextView() {
        val stringArrayRes = resources.getStringArray(R.array.msg_array)
        msgTextView.text = stringArrayRes[random.nextInt(stringArrayRes.size)]

    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onResume(this)
    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
//            R.id.nav_camera -> {
//                // Handle the camera action
//            }
//            R.id.nav_gallery -> {
//
//            }
//            R.id.nav_slideshow -> {
//
//            }
//            R.id.nav_manage -> {
//
//            }
//            R.id.nav_share -> {
//
//            }
//            R.id.nav_send -> {
//
//            }
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onClick(v: View?) {
        val id = v?.id
        if (id == R.id.batteryView) {
            batteryView.clickBattery()
            subTitleTextView.text = batteryView.getSubTitle()
        } else if (id == R.id.setupDateButton) {
            showDatePickerDialog(true)
        } else if (id == R.id.changeDateButton) {
            showDatePickerDialog(false)
        } else if (id == R.id.clearDateButton) {
            showClearDateDialog()
        }

    }

    private var date_year = Calendar.getInstance().get(Calendar.YEAR)
    private var date_month = Calendar.getInstance().get(Calendar.MONTH)
    private var date_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    private fun showDatePickerDialog(isNeedHideSetupDateButton: Boolean) {
        val dialog = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_date_picker, null)
        val datePicker = view.findViewById(R.id.datePicker) as DatePicker

        datePicker.init(date_year, date_month, date_day, null)

        dialog.setView(view)
        dialog.setNegativeButton(R.string.cancel) {
            dialog, _ ->
            dialog.dismiss()
        }
        dialog.setPositiveButton(R.string.ok) {
            dialog, _ ->
            dialog.dismiss()
            val date = datePicker.year * 10000L + (datePicker.month + 1) * 100 + datePicker.dayOfMonth
            batteryView.addBattery(date)
            setMsgTextView()
            if (isNeedHideSetupDateButton) {
                hideSetupDateButton()
            }
            date_year = datePicker.year
            date_month = datePicker.month
            date_day = datePicker.dayOfMonth
            if (DEBUG) {
                val msg = datePicker.year.toString() + "," + datePicker.month.toString() + "," + datePicker.dayOfMonth.toString()
                Log.d(TAG, msg)
            }
            subTitleTextView.text = batteryView.getSubTitle()
        }
        dialog.show()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            val calendar = Calendar.getInstance()
            datePicker.maxDate = calendar.timeInMillis

            calendar.set(1900, 0, 0)
            datePicker.minDate = calendar.timeInMillis + 24 * 3600000
        }
    }

    private fun showClearDateDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.clear_date_title)
        val msg = String.format(getString(R.string.clear_date_msg), getString(R.string.app_name))
        dialog.setMessage(msg)
        dialog.setNegativeButton(R.string.cancel) {
            dialog, _ ->
            dialog.dismiss()
        }
        dialog.setPositiveButton(R.string.ok) {
            dialog, _ ->
            dialog.dismiss()
            batteryView.clearBattery()
            showSetupDateButton()
            setMsgTextView()
            subTitleTextView.text = batteryView.getSubTitle()
        }
        dialog.show()
    }

    private fun showSetupDateButton() {
        subTitleTextView.visibility = View.INVISIBLE
        setupDateButton.visibility = View.VISIBLE
        changeDateButton.visibility = View.GONE
        clearDateButton.visibility = View.GONE
    }

    private fun hideSetupDateButton() {
        subTitleTextView.visibility = View.VISIBLE
        setupDateButton.visibility = View.GONE
        changeDateButton.visibility = View.VISIBLE
        clearDateButton.visibility = View.VISIBLE
    }
}
