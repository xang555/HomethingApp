package com.xang.laothing.Service;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by xang on 03/05/2017.
 */

public class RandomService {

    public static int getRandomNumber(){

        Random random =new Random();
        Calendar calendar = Calendar.getInstance();
        int second = calendar.get(Calendar.SECOND);
        int higth = 1000;
        int low = 100;
        int randomnumber = (random.nextInt(higth - low) + low) - second;
        return (randomnumber);
    }


}
