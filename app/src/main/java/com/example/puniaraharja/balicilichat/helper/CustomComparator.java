package com.example.puniaraharja.balicilichat.helper;


import com.example.puniaraharja.balicilichat.persistence.Chat;

import java.util.Comparator;

/**
 * Created by Punia Raharja on 10/22/2017.
 */

public class CustomComparator implements Comparator<Chat> {
    @Override
    public int compare(Chat o1, Chat o2) {
        return o1.getDate().compareTo(o2.getDate());
    }
}
