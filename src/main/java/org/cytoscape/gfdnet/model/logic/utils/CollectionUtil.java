package org.cytoscape.gfdnet.model.logic.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Norberto Díaz-Díaz
 */
public class CollectionUtil {
    public static <T> T search(SortedSet<T> set, T element){
        T o=null;
        List l=new ArrayList(set);
        int index= Collections.binarySearch(l, element);
        if(index>=0) {
            o=(T)l.get(index);
        }
        return o;
    }
}