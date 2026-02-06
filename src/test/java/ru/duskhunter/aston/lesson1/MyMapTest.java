package ru.duskhunter.aston.lesson1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyMapTest {

    /*
    1. test MyMap.size
    1.1. add first element -> size == 1
    1.2. add 11 element with different key -> size == 11
    1.3. add 2 element with same key -> size == 1
    1.4. add 2 element ==hash but !equals -> size 2
    1.4. add 3 element when ==hash but 2 element !equals and 2 element equals -> size 2, element 1 has link "next".
         Element 1 rewrite. New element create link "next" like a prevElement

    2. test MyMap.get
    2.1. return value)
    2.2. return value when multiply elements in bucket
    2.3. return null when element in bucket no equals
    2.4. return null when there is no element with that key
     */

    @Test
    public void mustMapSizePlusOne() {
        MyMap<Integer, Integer> myMap = new MyMap<>();
        myMap.put(1, 1);
        assertEquals(1, myMap.size());
    }

    @Test
    public void mustMapSizePlusEleven() {
        MyMap<Integer, Integer> myMap = new MyMap<>();
        for (int i = 0; i < 11; i++) {
            myMap.put(i * 10, 1);
        }
        assertEquals(11, myMap.size());
    }

    @Test
    public void mustRewriteElementForKey() {
        MyMap<Integer, Integer> myMap = new MyMap<>();
        myMap.put(1, 1);
        myMap.put(1, 2);
        assertEquals(1, myMap.size());
    }

    @Test
    public void mustNoRewriteChangeNextForBucket() {
        MyMap<String, Integer> myMap = new MyMap<>();
        String s1 = "Aa";
        String s2 = "BB"; // одинаковый hashCode 2112
        myMap.put(s1, 2);
        myMap.put(s2, 3);
        assertEquals(2, myMap.size());
    }

    @Test
    public void mustRewriteElementForKeyAndCreateLinkNextAsPrevElement() {
        MyMap<String, Integer> myMap = new MyMap<>();
        String s1 = "Aa";
        String s2 = "BB"; // одинаковый hashCode 2112
        String s3 = "Aa";
        myMap.put(s1, 2);
        myMap.put(s2, 3);
        myMap.put(s3, 4);
        assertEquals(2, myMap.size());
    }

    @Test
    public void mustGetValueWhenOnlyOneCorrectElementInBucket() {
        MyMap<Integer, Integer> myMap = new MyMap<>();
        myMap.put(1, 13);
        assertEquals(13, myMap.get(1));
    }

    @Test
    public void mustGetValueWhenMultipleElementsInBucket() {
        MyMap<String, Integer> myMap = new MyMap<>();
        String s1 = "Aa";
        String s2 = "BB";
        String s3 = "Cc";
        myMap.put(s1, 2);
        myMap.put(s2, 3);
        myMap.put(s3, 4);
        assertEquals(3, myMap.get("BB"));
    }

    @Test
    public void mustGetNullWhenNoCorrectElementInBucket() {
        MyMap<String, Integer> myMap = new MyMap<>();
        String s1 = "Aa";
        String s2 = "Cc";
        myMap.put(s1, 2);
        myMap.put(s2, 3);
        assertNull(myMap.get("BB"));
    }

    @Test
    public void mustGetNullWhenNoThisKey() {
        MyMap<Integer, Integer> myMap = new MyMap<>();
        myMap.put(1, 13);
        assertNull(myMap.get(33));
    }
}