package com.xang.laothing.Service;

import java.util.Random;

/**
 * Created by xang on 03/05/2017.
 */

public class RandomService {

    public static int getRandomNumber(){

        Random random =new Random();
        int higth = 99;
        int low = 10;

        return (random.nextInt(higth - low) + low);
    }


}
