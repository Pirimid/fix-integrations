package com.pirimid.utils;

import java.util.concurrent.ThreadLocalRandom;

public class Helper {

    public static int generateNextRequestId() {
        return ThreadLocalRandom.current().nextInt(1, 101);
    }
}
