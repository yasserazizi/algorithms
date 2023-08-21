package com.training.btree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BTreeTest {

    @Test
    public void addKeyToBNode(){
        BTreeNode bNode = new BTreeNode(3,1);
        Assertions.assertTrue(bNode.isLeaf());
        Assertions.assertEquals(1, bNode.getKeys()[0]);
    }

    @Test
    public void addMaxKeysToBNode(){
        BTreeNode bNode = new BTreeNode(5,1);
        int[] keys = new int[]{5,2,-2};
        for (int key: keys)
            bNode.addKey(key);

        int[] expectedKeys = new int[]{-2,1,2,5};
        for (int i=0; i<expectedKeys.length; i++)
            Assertions.assertEquals(expectedKeys[i],bNode.getKeys()[i]);
        Assertions.assertEquals(4,bNode.getKeysNumber());
        Assertions.assertTrue(bNode.isLeaf());
    }

    @Test
    public void addKeysToBNode(){
        BTreeNode bNode = new BTreeNode(5,1);
        int[] keys = new int[]{5,0};
        for (int key: keys)
            bNode.addKey(key);

        int[] expectedKeys = new int[]{0,1,5};
        for (int i=0; i<expectedKeys.length; i++)
            Assertions.assertEquals(expectedKeys[i],bNode.getKeys()[i]);
        Assertions.assertEquals(3,bNode.getKeysNumber());
        Assertions.assertTrue(bNode.isLeaf());
    }

    @Test
    public void insert() {

        Random random = new Random();
        int[] keys = new int[1_000_000];
        for(int i=0; i<1_000_000; i++)
           keys[i] = random.nextInt(2_000_000);

        BTree bTree  = new BTree(7);
        bTree.insert(keys);

        Assertions.assertTrue(bTree.isBalanced());
    }

    @Test
    public void delete(){
        Random random = new Random();

        int[] keys = new int[10_000_000];
        for(int i=0; i<keys.length; i++)
            keys[i] = random.nextInt(20_000_000);

        int[] toDelete = new int[keys.length];
        for(int i=0; i< toDelete.length; i++){
            toDelete[i] = keys[random.nextInt(keys.length)];
        }

        BTree bTree  = new BTree(7);
        bTree.insert(keys);

        bTree.delete(toDelete);
        Assertions.assertTrue(bTree.isBalanced());
    }


}
