package com.pirimid.utils;

import java.text.DecimalFormat;
import java.util.Random;

public class Helper {

    private static final Random random = new Random();

    public static double generateNextPrice() {
        DecimalFormat decimalFormat = new DecimalFormat(".####");
        Double nextPrice = Double.valueOf(decimalFormat.format(random.nextDouble() + 1));
        return  nextPrice;
    }
}
