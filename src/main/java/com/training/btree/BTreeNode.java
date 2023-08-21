package com.training.btree;

import java.util.Arrays;

public class BTreeNode {
    private final Integer[] keys;
    private BTreeNode[] children;

    private int keysNumber = 0;
    private int childrenNumber = 0;

    public BTreeNode(int order, int key) {
        this.keys = new Integer[2 * order - 1];
        this.children = null;
        addKeyAt(0, key);
    }

    void addKey(Integer key) {
        if (key == null) return;

        int index;
        for (index = 0; index < keysNumber; index++) {
            if (key.compareTo(keys[index]) == 0) return;
            if (keys[index] > key) break;
        }

        addKeyAt(index, key);
    }

    void addKeyAt(int index, Integer key) {
        if (key == null || index > keysNumber || index < 0) return;

        for (int j = keysNumber; j > index; j--) {
            keys[j] = keys[j - 1];
        }
        keys[index] = key;

        keysNumber++;
    }

    public void deleteKey(Integer key) {
        if (key == null) return;

        int index;
        for (index = 0; index < keysNumber; index++) {
            if (key.compareTo(keys[index]) < 0) return;
            if (key.compareTo(keys[index]) == 0) break;
        }
        deleteKeyAt(index);
    }

    public void deleteKeyAt(int index) {
        if (index < 0 || index >= keysNumber || keys[index] == null) return;

        while (index < keysNumber - 1) {
            keys[index] = keys[index + 1];
            index++;
        }
        keys[index] = null;
        keysNumber--;
    }

    public void insertChildAt(int index, BTreeNode rightChild) {
        this.rightShiftChildren(index);
        this.updateChildAt(index, rightChild);
    }

    public void updateChildAt(int index, BTreeNode child) {
        if (this.children == null && index == 0) this.children = new BTreeNode[this.keys.length + 1];
        else if (this.children == null || index < 0 || index >= this.children.length || child == null) return;

        if (this.children[index] == null) this.childrenNumber++;
        this.children[index] = child;
    }

    public void deleteChildAt(int index) {
        if (this.children == null || index < 0 || index >= this.children.length || this.children[index] == null)
            return;

        while (index < childrenNumber - 1) {
            children[index] = children[index + 1];
            index++;
        }
        children[index] = null;
        this.childrenNumber--;
    }

    private void rightShiftChildren(int from) {
        for (int i = childrenNumber; i > from; i--) {
            children[i] = children[i - 1];
        }
        children[from] = null;
    }

    public Integer successor() {
        BTreeNode current = this;
        while (!current.isLeaf()) {
            current = current.getChildren()[0];
        }
        return current.getKeys()[0];
    }

    public Integer predecessor() {
        BTreeNode current = this;
        while (!current.isLeaf()) {
            current = current.children[current.getChildrenNumber() - 1];
        }
        return current.keys[current.getKeysNumber() - 1];
    }

    public BTreeNode[] split() {
        int length = keys.length;
        int medium = (length - 1) / 2;
        int rightStart = 1 + medium;

        BTreeNode rightNode = new BTreeNode((length + 1) / 2, keys[rightStart]);

        for (int index = 1 + rightStart; index < length; index++) {
            rightNode.addKeyAt(index - rightStart, keys[index]);
        }

        for (int index = length - 1; index >= medium; index--) {
            deleteKeyAt(index);
        }

        if (!this.isLeaf()) {
            for (int index = rightStart; index < length + 1; index++) {
                rightNode.updateChildAt(rightNode.getChildrenNumber(), children[index]);
            }
            for (int i = length; i > medium; i--) {
                deleteChildAt(i);
            }
        }

        return new BTreeNode[]{this, rightNode};
    }

    public boolean isValid() {
        int order = (keys.length + 1) / 2;

        if (!hasValidKeys(order)) return false;
        if (!isLeaf() && !hasValidChildren(order)) return false;
        return isLeaf() || keysNumber == childrenNumber - 1;
    }

    boolean isLeaf() {
        return this.childrenNumber == 0;
    }

    public int getKeysNumber() {
        return keysNumber;
    }

    public int getChildrenNumber() {
        return childrenNumber;
    }

    public Integer[] getKeys() {
        return Arrays.copyOf(this.keys, this.keysNumber);
    }

    public BTreeNode[] getChildren() {
        if (this.children == null) return null;
        return Arrays.copyOf(this.children, this.childrenNumber);
    }

    private boolean hasValidKeys(int order) {
        int keysCount = 1;
        for (int i = 1; i < keys.length; i++) {
            if (keys[i] == null) break;
            if (keys[i] <= keys[i - 1]) return false;
            if (!isLeaf() && (keys[i] <= children[i].predecessor() || keys[i] >= children[i + 1].successor()))
                return false;
            keysCount++;
        }
        return keysCount == keysNumber && keysNumber > order - 2 && keysNumber < 2 * order;
    }

    private boolean hasValidChildren(int order) {
        int childrenCount = 0;
        for (BTreeNode child : children) {
            if (child == null) break;
            childrenCount++;
        }
        return childrenCount == childrenNumber && childrenNumber > order - 1 && keysNumber < 2 * order + 1;
    }

    public int findIndex(int key) {
        int index = 0;
        for (; index < this.keysNumber; index++) {
            if (key <= keys[index]) return index;
        }
        return index;
    }
}
