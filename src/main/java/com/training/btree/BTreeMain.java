package com.training.btree;

import java.util.Random;

public class BTreeMain {

    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(10000);
        BTree bTree  = new BTree(100);

        Random random = new Random();
        for(int i=0; i<1_000_000; i++)
            bTree.insert(random.nextInt(2_000_000));
    }
}
