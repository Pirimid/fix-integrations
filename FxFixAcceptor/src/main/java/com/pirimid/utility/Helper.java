package com.pirimid.utility;

public class Helper {

    public static double generatePrice() {
        return generateNextPrice(Math.random() * 1000);
    }

    public static double generateNextPrice(Double price) {
        Double nextPrice = price;
        nextPrice += Math.random();
        return  nextPrice;
    }
}
