package com.training.btree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BTree {
    private final int order;
    private BTreeNode root;

    public BTree(int order) {
        this.order = order;
        this.root = null;
    }

    public void insert(int... keys) {
        for (int key : keys) {
            insert(key);
        }
    }

    public void delete(int... keys) {
        for (int key : keys) {
            delete(root, key);
            if (root != null && root.getKeysNumber() == 0) {
                if (root.getChildrenNumber() > 0) root = root.getChildren()[0];
                else root = null;
            }
        }
    }

    private void insert(int key) {
        if (root == null) {
            root = new BTreeNode(this.order, key);
            return;
        }

        if (root.getKeysNumber() == 2 * order - 1) {
            BTreeNode newRoot = new BTreeNode(order, root.getKeys()[order - 1]);
            newRoot.updateChildAt(0, root);
            splitChild(newRoot, 0);
            root = newRoot;
        }
        insertNonFull(root, key);
    }

    private void insertNonFull(BTreeNode node, int key) {
        while (node != null) {
            if (!node.isLeaf()) {
                int index = node.findIndex(key);

                if (index < node.getKeysNumber() && key == node.getKeys()[index]) return;

                if (node.getChildren()[index].getKeysNumber() == 2 * order - 1) {
                    node.addKeyAt(index, node.getChildren()[index].getKeys()[order - 1]);
                    splitChild(node, index);
                    Integer[] keys = node.getKeys();
                    if (key == keys[index]) return;
                    if (key > keys[index]) index++;
                }
                node = node.getChildren()[index];
            } else {
                node.addKey(key);
                return;
            }
        }
    }

    private void insertNonFullIteratively(BTreeNode node, int key) {
        while (node != null) {
            if (!node.isLeaf()) {
                int index = 0;
                for (; index < node.getKeysNumber(); index++) {
                    if (key == node.getKeys()[index]) return;
                    if (key < node.getKeys()[index]) break;
                }

                if (node.getChildren()[index].getKeysNumber() == 2 * order - 1) {
                    node.addKeyAt(index, node.getChildren()[index].getKeys()[order - 1]);
                    splitChild(node, index);
                    Integer[] keys = node.getKeys();
                    if (key == keys[index]) return;
                    if (key > keys[index]) index++;
                }
                node = node.getChildren()[index];
            } else {
                node.addKey(key);
                return;
            }
        }
    }

    private void insertNonFullRecursively(BTreeNode node, int key) {
        if (!node.isLeaf()) {
            int index = 0;
            for (; index < node.getKeysNumber(); index++) {
                if (key == node.getKeys()[index]) return;
                if (key < node.getKeys()[index]) break;
            }

            if (node.getChildren()[index].getKeysNumber() == 2 * order - 1) {
                node.addKeyAt(index, node.getChildren()[index].getKeys()[order - 1]);
                splitChild(node, index);
                Integer[] keys = node.getKeys();
                if (key == keys[index]) return;
                if (key > keys[index]) index++;
            }
            insertNonFull(node.getChildren()[index], key);
        } else {
            node.addKey(key);
        }
    }

    private void delete(BTreeNode node, int key) {
        if (node == null) return;
        if (!node.isLeaf()) {
            Integer[] nodeKeys = node.getKeys();
            BTreeNode[] nodeChildren = node.getChildren();
            int index;
            for (index = 0; index < node.getKeysNumber(); index++) {
                if (key == nodeKeys[index]) {
                    if (nodeChildren[index].getKeysNumber() > order - 1) {
                        inOrderPredecessor(node, index);
                        return;
                    }
                    if (nodeChildren[index + 1].getKeysNumber() > order - 1) {
                        inOrderSuccessor(node, index + 1);
                        return;
                    }
                }
                if (key <= nodeKeys[index]) break;
            }

            if (nodeChildren[index].getKeysNumber() == order - 1) {
                if (index < node.getKeysNumber() && nodeChildren[index + 1].getKeysNumber() > order - 1)
                    borrowKeyFromRight(node, index);
                else if (index > 0 && nodeChildren[index - 1].getKeysNumber() > order - 1)
                    borrowKeyFromLeft(node, index);
                else if (index < node.getKeysNumber())
                    mergeChildren(node, index + 1);
                else {
                    mergeChildren(node, index);
                    index--;
                }
            }
            delete(node.getChildren()[index], key);
        } else
            node.deleteKey(key);
    }

    private void inOrderSuccessor(BTreeNode node, int index) {
        BTreeNode successorRoot = node.getChildren()[index];
        //Find successor and delete it
        Integer successor = successorRoot.successor();
        //Delete successor
        delete(successorRoot, successor);
        //Replace old child with the new one
        node.updateChildAt(index, successorRoot);
        //replace the key to delete with the successor
        node.deleteKeyAt(index - 1);
        node.addKeyAt(index - 1, successor);
    }

    private void inOrderPredecessor(BTreeNode node, int index) {
        BTreeNode predecessorRoot = node.getChildren()[index];
        //Find predecessor and delete it
        Integer predecessor = predecessorRoot.predecessor();
        //Delete predecessor
        delete(predecessorRoot, predecessor);
        //Replace old child with the new one
        node.updateChildAt(index, predecessorRoot);
        //replace the key to delete with the predecessor
        node.deleteKeyAt(index);
        node.addKeyAt(index, predecessor);
    }

    private void mergeChildren(BTreeNode node, int index) {
        BTreeNode[] children = node.getChildren();
        BTreeNode right = children[index];
        BTreeNode left = children[index - 1];

        left.addKeyAt(left.getKeysNumber(), node.getKeys()[index - 1]);

        for (Integer key : right.getKeys())
            left.addKeyAt(left.getKeysNumber(), key);

        if (!right.isLeaf()) {
            for (BTreeNode child : right.getChildren())
                left.updateChildAt(left.getChildrenNumber(), child);
        }
        node.deleteKeyAt(index - 1);
        node.deleteChildAt(index);
        node.updateChildAt(index - 1, left);
    }

    private void borrowKeyFromRight(BTreeNode node, int index) {
        BTreeNode[] children = node.getChildren();
        BTreeNode target = children[index];
        BTreeNode right = children[index + 1];

        //Borrow the key from the right child
        Integer rightKey = right.getKeys()[0];
        right.deleteKeyAt(0);

        //assign the borrowed key to the parent node key
        Integer parentKey = node.getKeys()[index];
        node.deleteKeyAt(index);
        node.addKeyAt(index, rightKey);

        //add the old parent node key to the child keys at the end
        target.addKeyAt(target.getKeysNumber(), parentKey);

        //move right node child[0] to the left node
        if (!right.isLeaf()) {
            BTreeNode rightChild = right.getChildren()[0];
            right.deleteChildAt(0);
            target.updateChildAt(target.getChildrenNumber(), rightChild);
        }

        node.updateChildAt(index, target);
        node.updateChildAt(index + 1, right);
    }

    private void borrowKeyFromLeft(BTreeNode node, int index) {
        BTreeNode[] children = node.getChildren();
        BTreeNode target = children[index];
        BTreeNode left = children[index - 1];

        //Borrow the key from the left child
        Integer leftKey = left.getKeys()[left.getKeysNumber() - 1];
        left.deleteKeyAt(left.getKeysNumber() - 1);

        //assign the borrowed key to the parent node key
        Integer parentKey = node.getKeys()[index - 1];
        node.deleteKeyAt(index - 1);
        node.addKeyAt(index - 1, leftKey);

        //add the old parent node key to the child keys
        target.addKeyAt(0, parentKey);

        //move last left node child to the right node
        if (!left.isLeaf()) {
            BTreeNode rightChild = left.getChildren()[left.getChildrenNumber() - 1];
            left.deleteChildAt(left.getChildrenNumber() - 1);
            target.insertChildAt(0, rightChild);
        }

        node.updateChildAt(index, target);
        node.updateChildAt(index - 1, left);
    }

    private void splitChild(BTreeNode parent, int index) {
        BTreeNode child = parent.getChildren()[index];
        BTreeNode[] nodes = child.split();
        parent.updateChildAt(index, nodes[0]);
        parent.insertChildAt(index + 1, nodes[1]);
    }

    public boolean isBalanced() {
        if (root == null) return true;
        if (!isValidRoot()) return false;
        if (!root.isLeaf()) {
            Queue<BTreeNode> queue = new LinkedList<>();
            for (BTreeNode child : root.getChildren()) {
                if (child != null) queue.offer(child);
            }
            List<BTreeNode> levelNodes = new ArrayList<>();
            while (!queue.isEmpty()) {
                int queueSize = queue.size();
                for (int i = 0; i < queueSize; i++) {
                    BTreeNode current = queue.poll();
                    if (!current.isValid()) return false;
                    levelNodes.add(current);
                }

                for (int i = 1; i < levelNodes.size(); i++) {
                    BTreeNode current = levelNodes.get(i);
                    if (current.isLeaf() != levelNodes.get(0).isLeaf()) return false;
                    if (!current.isLeaf()) {
                        for (BTreeNode child : current.getChildren())
                            if (child != null) queue.offer(child);
                    }
                }
                levelNodes.clear();
            }
        }
        return true;
    }

    private boolean isValidRoot() {
        Integer[] keys = root.getKeys();
        int keysCount = 1;
        for (int i = 1; i < keys.length; i++) {
            if (keys[i] == null) break;
            if (keys[i] <= keys[i - 1]) return false;
            keysCount++;
        }
        if (keysCount != root.getKeysNumber() || keysCount < 1 || keysCount > 2 * order - 1)
            return false;

        if (!root.isLeaf()) {
            BTreeNode[] children = root.getChildren();
            int childrenCount = 0;
            for (BTreeNode child : children) {
                if (child == null) break;
                childrenCount++;
            }
            return childrenCount == root.getChildrenNumber() && keysCount == childrenCount - 1;
        }
        return true;
    }
}
