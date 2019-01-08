package com.djinggamedia.datapengungsi.appcs.app;

import com.djinggamedia.datapengungsi.appcs.persistence.Chat;
import com.djinggamedia.datapengungsi.appcs.persistence.Message;

import java.util.Comparator;

/**
 * Created by Punia Raharja on 10/22/2017.
 */

public class CustomComparator implements Comparator<Chat> {
    @Override
    public int compare(Chat o1, Chat o2) {
        return o1.getDateDate().compareTo(o2.getDateDate());
    }
}
