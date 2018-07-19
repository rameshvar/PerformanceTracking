/*
 * Created by rampande on 03/07/18.
 */
package com.performance.tracking;

import java.util.ArrayList;

public class InteractionHistory<E> extends ArrayList<E> {
    private static final int USER_JOURNEY_DEPTH = 5;

    @Override
    public boolean add(E e) {
        if (size() == USER_JOURNEY_DEPTH) {
            this.remove(0);
        }
        return super.add(e);
    }
}
