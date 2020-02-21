package com.example.salestudioapplication;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SalaStudio {
    public int id;
    public String name;
    public int totalSeat;
    public int freeSeat;
    public int openHour;
    public int closeHour;

    public SalaStudio(int id, String name, int totalSeat, int freeSeat, int openHour, int closeHour) {
        this.id = id;
        this.name = name;
        this.totalSeat = totalSeat;
        this.freeSeat = freeSeat;
        this.openHour = openHour;
        this.closeHour = closeHour;
    }

    public SalaStudio(){

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getCurrentHour(){
        LocalTime time = LocalTime.now();
        int ora = time.getHour();
        return ora;
    }

    public String getCurrentDayOfTheWeek() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Date date = calendar.getTime();
        String dayToRet = "";

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        switch(dayOfWeek){

            case 2:
                dayToRet = "Monday";
                break;
            case 3:
                dayToRet = "Tuesday";
                break;
            case 4:
                dayToRet = "Wednesday";
                break;
            case 5:
                dayToRet = "Thursday";
                break;
            case 6:
                dayToRet = "Friday";
                break;
            case 7:
                dayToRet = "Saturday";
                break;
            case 8:
                dayToRet = "Sunday";
                break;
        }
        return dayToRet;
    }
}
