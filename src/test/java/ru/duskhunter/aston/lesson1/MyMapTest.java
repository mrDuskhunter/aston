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

    3. test MyMap.remove
    3.1. return value
    3.1. change size -1
    3.2. return value when multiply elements in bucket change size -1
    3.3. return null when element in bucket no equals no change size
    3.4. return null when there is no element with that key no change size
     */

    @Test
    public void mustMapSizePlusOne() {
        MyMap<Integer, Integer> myMap = new MyMapImpl<>();
        myMap.put(1, 1);
        assertEquals(1, myMap.size());
    }

    @Test
    public void mustMapSizePlusEleven() {
        MyMap<Integer, Integer> myMap = new MyMapImpl<>();
        for (int i = 0; i < 11; i++) {
            myMap.put(i * 10, 1);
        }
        assertEquals(11, myMap.size());
    }

    @Test
    public void mustRewriteElementForKey() {
        MyMap<Integer, Integer> myMap = new MyMapImpl<>();
        myMap.put(1, 1);
        myMap.put(1, 2);
        assertEquals(1, myMap.size());
    }

    @Test
    public void mustNoRewriteChangeNextForBucket() {
        MyMap<String, Integer> myMap = new MyMapImpl<>();
        String s1 = "Aa";
        String s2 = "BB"; // одинаковый hashCode 2112
        myMap.put(s1, 2);
        myMap.put(s2, 3);
        assertEquals(2, myMap.size());
        assertEquals(2, myMap.get("Aa"));
        assertEquals(3, myMap.get("BB"));
    }

    @Test
    public void mustRewriteElementForKeyAndCreateLinkNextAsPrevElement() {
        MyMap<String, Integer> myMap = new MyMapImpl<>();
        String s1 = "Aa";
        String s2 = "BB"; // одинаковый hashCode 2112
        String s3 = "Aa";
        myMap.put(s1, 2);
        myMap.put(s2, 3);
        myMap.put(s3, 4);
        assertEquals(2, myMap.size());
        assertEquals(3, myMap.get("BB"));
        assertEquals(4, myMap.get("Aa"));
    }

    @Test
    public void mustGetValueWhenOnlyOneCorrectElementInBucket() {
        MyMap<Integer, Integer> myMap = new MyMapImpl<>();
        myMap.put(1, 13);
        assertEquals(13, myMap.get(1));
    }

    @Test
    public void mustGetValueWhenMultipleElementsInBucket() {
        MyMap<String, Integer> myMap = new MyMapImpl<>();
        String s1 = "Aa";
        String s2 = "BB";
        String s3 = "Cc";
        myMap.put(s1, 2);
        myMap.put(s2, 3);
        myMap.put(s3, 4);
        assertEquals(2, myMap.get("Aa"));
        assertEquals(3, myMap.get("BB"));
        assertEquals(4, myMap.get("Cc"));
    }

    @Test
    public void mustGetNullWhenNoCorrectElementInBucket() {
        MyMap<String, Integer> myMap = new MyMapImpl<>();
        String s1 = "Aa";
        String s2 = "Cc";
        myMap.put(s1, 2);
        myMap.put(s2, 3);
        assertNull(myMap.get("BB"));
    }

    @Test
    public void mustGetNullWhenNoThisKey() {
        MyMap<Integer, Integer> myMap = new MyMapImpl<>();
        myMap.put(1, 13);
        assertNull(myMap.get(33));
    }

    @Test
    public void mustMapSizeMinusElement() {
        MyMap<Integer, Integer> myMap = new MyMapImpl<>();
        for (int i = 0; i < 11; i++) {
            myMap.put(i * 10, 1);
        }
        assertEquals(11, myMap.size());
        assertEquals(myMap.get(10), 1);

        assertEquals(1, myMap.remove(10));
        assertEquals(10, myMap.size());
    }

    @Test
    public void mustRemoveAndGetValueWhenMultipleElementsInBucket() {
        MyMap<String, Integer> myMap = new MyMapImpl<>();
        String s1 = "Aa";
        String s2 = "BB";
        String s3 = "Cc";
        myMap.put(s1, 2);
        myMap.put(s2, 3);
        myMap.put(s3, 4);
        assertEquals(2, myMap.get("Aa"));
        assertEquals(3, myMap.get("BB"));
        assertEquals(4, myMap.get("Cc"));
        assertEquals(3, myMap.size());

        assertEquals(3, myMap.remove("BB"));
        assertEquals(2, myMap.size());
        assertEquals(2, myMap.get("Aa"));
        assertEquals(4, myMap.get("Cc"));
        assertNull(myMap.get("BB"));
    }


    @Test
    public void mustGetNullAndNoChangeSize() {
        MyMap<String, Integer> myMap = new MyMapImpl<>();
        String s1 = "Aa";
        String s2 = "BB";
        String s3 = "Cc";
        myMap.put(s1, 2);
        myMap.put(s2, 3);
        myMap.put(s3, 4);
        assertEquals(3, myMap.size());

        assertNull(myMap.remove("Cb"));
        assertEquals(3, myMap.size());
        assertEquals(2, myMap.get("Aa"));
        assertEquals(3, myMap.get("BB"));
        assertEquals(4, myMap.get("Cc"));
    }
}