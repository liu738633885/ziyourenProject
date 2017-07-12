package me.yokeyword.indexablelistview.help;


import java.util.Comparator;

import me.yokeyword.indexablelistview.IndexEntity;

/**
 * Created by YoKeyword on 16/3/20.
 */
public class PinyinComparator<T extends IndexEntity> implements Comparator<T> {

    @Override
    public int compare(T lhs, T rhs) {
        return lhs.getFirstSpell().compareTo(rhs.getFirstSpell());
    }
}