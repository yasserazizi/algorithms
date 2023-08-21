package com.training.btree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;


public class BTreeInsertionPerformanceTest {

    private static int[] keys;

    @BeforeAll
    static void prepareData(){
        Random random = new Random();
        keys = new int[1_000_000];
        for(int i=0; i<keys.length; i++)
            keys[i] = random.nextInt(20_000_000);
    }


    @ParameterizedTest
    @ValueSource(ints = {50,100,150,200,250,300,350,400})
    public void insert(int order) {
        BTree bTree  = new BTree(order);
        bTree.insert(keys);
    }

}
