package com.example.salestudioapplication;

import android.os.Build;

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

    public boolean getCurrentDayOfTheWeek() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        Date date = calendar.getTime();
        boolean weekend;

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY){
            weekend = false;
        }else{
            weekend = true;
        }
        return weekend;
    }
}
