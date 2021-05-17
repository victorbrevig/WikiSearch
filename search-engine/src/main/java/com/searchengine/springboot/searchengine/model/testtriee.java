package com.searchengine.springboot.searchengine.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class testtriee {
      
    BSTnode root;
    
    // sorted array after X
    Integer[] X;
    // sorted array after Y
    Integer[] Y;
    // size of tree
    int n;
    HashTable<Integer, String> idToString;
    HashTable<Integer, String> idToRevString;
    BinarySearchReverseLexicographical bs; 
    
    
    testtriee(Integer[] X, Integer[] Y, HashTable<Integer, String> idToString, HashTable<Integer, String> idToRevString) {
    	this.X = X;
    	this.Y = Y;
    	this.idToString = idToString;
    	this.idToRevString = idToRevString;
    	this.n = X.length;
    	
    	bs = new BinarySearchReverseLexicographical(idToRevString);
    	
        root = constructBST(X, Y, 0, n - 1, null, false);
    	
    }
    
    BSTnode constructBST(Integer[] X, Integer[] Y, int start, int end, BSTnode parentNode, boolean fromLeft) {
    	// takes sorted array as input
    	
        if (start > end) {
            return null;
        }
  
        // middle element as root
        int middle = (start + end) / 2;
        //System.out.println(X.length);
        
        System.out.println(Arrays.toString(X));
        System.out.println(X[middle]);
        
        
        BSTnode node = new BSTnode(X[middle]);
  
        
        
        
        HashSet<Integer> subTreeXvals = new HashSet<Integer>();;
        
        for(int i=start; i <= middle-1; i++) {
        	int xVal = X[i];
        	subTreeXvals.add(xVal);
        }
        for(int i=middle+1; i <= end; i++) {
        	int xVal = X[i];
        	subTreeXvals.add(xVal);
        }
        
        Integer[] newY = new Integer[subTreeXvals.size()];
        
        int indexCount = -1;
        
        
        if(parentNode == null) {
        	for(int i=0; i<Y.length; i++) {
            	if(subTreeXvals.contains(Y[i])) {
            		indexCount++;
            		newY[indexCount] = Y[i];
            	}
            }
        }
        else if(fromLeft) {
        	// if we came from left, construct bridges to right child of parent
        	parentNode.rightYbridges = new int[parentNode.Yarray.length];
        	
        	for(int i=0; i<parentNode.Yarray.length; i++) {
        		if(subTreeXvals.contains(parentNode.Yarray[i])) {
            		indexCount++;
            		newY[indexCount] = parentNode.Yarray[i];
            	}
        		parentNode.rightYbridges[i] = indexCount == -1 ? 0 : indexCount;
            	
            }
        }
        else {
        	// if we came from right, construct bridges to left child of parent
        	parentNode.leftYbridges = new int[parentNode.Yarray.length];

        	for(int i=0; i<parentNode.Yarray.length; i++) {
        		if(subTreeXvals.contains(parentNode.Yarray[i])) {
            		indexCount++;
            		newY[indexCount] = parentNode.Yarray[i];
            	}
        		parentNode.leftYbridges[i] = indexCount == -1 ? 0 : indexCount;
        		
            	
            }
        	
        	
        }
        

        subTreeXvals = null;
        
        node.Yarray = newY;
        
        // left subtree
        node.left = constructBST(X, newY, start, middle - 1, node, false);
  
        // right subtree
        node.right = constructBST(X, newY, middle + 1, end, node, true);
          
        return node;
    }
    
    
    public Pair<ArrayList<Integer>,ArrayList<YarrayResult>> rangeSearch2D(String x1, String x2, String y1, String y2) {
    	Pair<ArrayList<Integer>,ArrayList<YarrayResult>> res = new Pair(new ArrayList<Integer>(),new ArrayList<YarrayResult>());
    	
    	String y1Rev = IndexTools.revString(y1);
    	String y2Rev = IndexTools.revString(y2);
    	
    	// check if 1D problem
    	if(x1.equals(x2)) {
    		
    	}
    	
    	
    	
    	BSTnode splitNode = findSplitNode(root, x1, x2);
    	
    	// binary search to find 
    	int y1pos = bs.binarySearchLeftBound(splitNode.Yarray, y1Rev, 0, splitNode.Yarray.length-1);
    	int y2pos = bs.binarySearchRightBound(splitNode.Yarray, y2Rev, 0, splitNode.Yarray.length-1);

		Pair<ArrayList<Integer>,ArrayList<YarrayResult>> left = collectNodesLeft(splitNode.left, new Pair(new ArrayList<Integer>(),new ArrayList<YarrayResult>()), x1, splitNode.leftYbridges[y1pos], splitNode.leftYbridges[y2pos], y1Rev, y2Rev);
		Pair<ArrayList<Integer>,ArrayList<YarrayResult>> right = collectNodesRight(splitNode.right, new Pair(new ArrayList<Integer>(),new ArrayList<YarrayResult>()), x2, splitNode.rightYbridges[y1pos], splitNode.rightYbridges[y2pos], y1Rev, y2Rev);
    	
		if(isInYrange(splitNode.data, y1Rev, y2Rev)) {
			res.first.add(splitNode.data);
		}
    	res.first.addAll(left.first);
    	res.first.addAll(right.first);
    	
    	
    	res.second.addAll(left.second);
    	res.second.addAll(right.second);
    	
    	
    	return res;
    }
    

	private Pair<ArrayList<Integer>,ArrayList<YarrayResult>> collectNodesLeft(BSTnode node, Pair<ArrayList<Integer>,ArrayList<YarrayResult>> acc, String x1, int y1Pos, int y2Pos, String y1Rev, String y2Rev) {
		String nodeStr = idToString.search(node.data);
    	// base case - node found
    	if(nodeStr.equals(x1)) {
    		// check if single node is in y-range
    		if(isInYrange(node.data, y1Rev, y2Rev)) {
    			acc.first.add(node.data);
    		}
    		
    		if(node.right != null) {
    			// add right sub-tree
    			YarrayResult subRes = new YarrayResult(node.right.Yarray, node.rightYbridges[y1Pos], node.rightYbridges[y2Pos]);
    			acc.first.add(node.right.data);
    			acc.second.add(subRes);
    		}
    		return acc;
    	}
    	else {
    		int compareRes = x1.compareTo(nodeStr);
    		if(compareRes < 0) {
    			// going left
    			if(isInYrange(node.data, y1Rev, y2Rev)) {
        			acc.first.add(node.data);
        		}
    			if(node.right != null) {
        			// add right sub-tree
    				YarrayResult subRes = new YarrayResult(node.right.Yarray, node.rightYbridges[y1Pos], node.rightYbridges[y2Pos]);
    				acc.first.add(node.right.data);
        			acc.second.add(subRes);
        		}
    			return collectNodesLeft(node.left, acc, x1, node.leftYbridges[y1Pos], node.leftYbridges[y2Pos], y1Rev, y2Rev);
    		}
    		else {
    			// going right
    			return collectNodesLeft(node.right, acc, x1, node.rightYbridges[y1Pos], node.rightYbridges[y2Pos], y1Rev, y2Rev);
    		}
    	}
	}

    
    
	
	
    
	private Pair<ArrayList<Integer>,ArrayList<YarrayResult>> collectNodesRight(BSTnode node, Pair<ArrayList<Integer>,ArrayList<YarrayResult>> acc, String x2, int y1Pos, int y2Pos, String y1Rev, String y2Rev) {
		String nodeStr = idToString.search(node.data);
    	// base case - node found
    	if(nodeStr.equals(x2)) {
    		if(isInYrange(node.data, y1Rev, y2Rev)) {
    			acc.first.add(node.data);
    		}
    		if(node.left != null) {
    			// add left sub-tree
    			YarrayResult subRes = new YarrayResult(node.left.Yarray, node.leftYbridges[y1Pos], node.leftYbridges[y2Pos]);
    			acc.first.add(node.left.data);
    			acc.second.add(subRes);
    		}
    		return acc;
    	}
    	else {
    		int compareRes = x2.compareTo(nodeStr);

    		if(compareRes > 0) {
    			// going right
    			if(isInYrange(node.data, y1Rev, y2Rev)) {
        			acc.first.add(node.data);
        		}
    			if(node.left != null) {
        			// add left sub-tree
    				YarrayResult subRes = new YarrayResult(node.left.Yarray, node.leftYbridges[y1Pos], node.leftYbridges[y2Pos]);
    				acc.first.add(node.left.data);
        			acc.second.add(subRes);
        		}
    			return collectNodesRight(node.right, acc, x2, node.rightYbridges[y1Pos], node.rightYbridges[y2Pos], y1Rev, y2Rev);
    		}
    		else {
    			// going left
    			return collectNodesRight(node.left, acc, x2, node.leftYbridges[y1Pos], node.leftYbridges[y2Pos], y1Rev, y2Rev);
    		}
    	}
	}
    
    
    
    
	BSTnode findSplitNode(BSTnode node, String x1, String x2) {

    	String nodeStr = idToString.search(node.data);
    	
    	int x1Comp = x1.compareTo(nodeStr);
    	int x2Comp = x2.compareTo(nodeStr);
    
    	// if they have same sign (i.e. both to the left or both to the right
    	if(x1Comp*x2Comp > 0) {
    		if(x1Comp < 0) {
    			return findSplitNode(node.left, x1, x2);
    		}
    		else {
    			// the case x1==x2 is checked before this
    			return findSplitNode(node.right, x1, x2);
    		}
    	}
    	else {
    		return node;
    	}
    	
    }
	
	 /* A utility function to print preorder traversal of BST */
    void preOrder(BSTnode node) {
        if (node == null) {
            return;
        }
        System.out.print(node.data + " size: " + node.Yarray.length + " ");
        preOrder(node.left);
        preOrder(node.right);
    }
    
    
	private boolean isInYrange(int strId, String y1Rev, String y2Rev) {
		String revString = idToRevString.search(strId);
		
		return revString.compareTo(y1Rev) >= 0 && revString.compareTo(y2Rev) <= 0;
	}
	
	
	public ArrayList<Integer> getExplicitResult(Pair<ArrayList<Integer>, ArrayList<YarrayResult>> implicitRes, String y1) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		
		for(int i : implicitRes.first) {
			res.add(i);
		}
		
		for(YarrayResult yArrayInfo : implicitRes.second) {
			Integer[] currentArray = yArrayInfo.Yarray;
			if(currentArray.length == 0) {
				continue;
			}
			int startOffset = 0;
			if(!idToRevString.search(currentArray[yArrayInfo.startPos]).equals(IndexTools.revString(y1))) {
				startOffset = 1;
			}
			for(int i=yArrayInfo.startPos+startOffset; i < yArrayInfo.endPos; i++) {
				res.add(currentArray[i]);
			}
		}
		return res;
	}
	
  
    public static void main(String[] args) {
    	Integer[] X = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8};
    	Integer[] Y = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8};
    	
    	
    	
    	HashTable<Integer, String> idToWord = new HashTable(LinkedList.class);
    	idToWord.insert(1,"pleasant");
    	idToWord.insert(2,"apparatus");
    	idToWord.insert(3,"distinct");
    	idToWord.insert(4,"apathy");
    	idToWord.insert(5,"transparent");
    	idToWord.insert(6,"liberty");
    	idToWord.insert(7,"of");
    	idToWord.insert(8,"confine");
    	
    	HashTable<Integer, String> idToRevWord = new HashTable(LinkedList.class);
    	idToRevWord.insert(1,IndexTools.revString("pleasant"));
    	idToRevWord.insert(2,IndexTools.revString("apparatus"));
    	idToRevWord.insert(3,IndexTools.revString("distinct"));
    	idToRevWord.insert(4,IndexTools.revString("apathy"));
    	idToRevWord.insert(5,IndexTools.revString("transparent"));
    	idToRevWord.insert(6,IndexTools.revString("liberty"));
    	idToRevWord.insert(7,IndexTools.revString("of"));
    	idToRevWord.insert(8,IndexTools.revString("confine"));
    	

    	Arrays.sort(X, Comparator.comparing(x -> idToWord.search(x)));
    	
    	Arrays.sort(Y, Comparator.comparing(y -> idToRevWord.search(y)));
    	
    	/*
    	System.out.println(Arrays.toString(X));
    	System.out.println(X.length);
    	System.out.println(Arrays.toString(Y));
    	System.out.println(Y.length);
        */
    	
    	
    	testtriee tree = new testtriee(X, Y, idToWord, idToRevWord);
    	
    	/*
    	BSTnode n = tree.findSplitNode(tree.root, "confine", "transparent");
    	System.out.println(n.data);
    	*/
    	
    	System.out.println("PREORDER");
    	tree.preOrder(tree.root);
    	
        /*
    	System.out.println(Arrays.toString(tree.root.Yarray));
    	System.out.println("LEFT");
    	System.out.println(Arrays.toString(tree.root.leftYbridges));
    	System.out.println(Arrays.toString(tree.root.left.Yarray));
    	System.out.println("RIGHT");
    	System.out.println(Arrays.toString(tree.root.rightYbridges));
    	System.out.println(Arrays.toString(tree.root.right.Yarray));
    	*/
    	//Pair<ArrayList<Integer>, ArrayList<YarrayResult>> res = tree.rangeSearch2D("confine", "transparent", "of", "liberty");
    	// "apathy", "transparent", "confine", "liberty"
    	/*
    	for(Integer i : res.first) {
    		System.out.println(i);
    	}
    	
    	for(YarrayResult y : res.second) {
    		System.out.println(Arrays.toString(y.Yarray));
    		System.out.println(y.startPos);
    		System.out.println(y.endPos);
    	}
    	*/
    	/*
    	ArrayList<Integer> explicitRes = tree.getExplicitResult(res, "confine");
    	for(Integer i : explicitRes) {
    		System.out.println("RES: " + i);
    	}
    	*/
    	
    	
        //System.out.println("Preorder traversal of constructed BST");
        //tree.preOrder(tree.root);
        
        
        //BSTnode node = tree.findSplitNode(tree.root, "confine", "pleasant");
        //System.out.println(" ");
        //System.out.println(node.data);
    	/*
    	Pair<ArrayList<BSTnode>> res = tree.rangeSearch1D("confine", "pleasant");
    	
    	for(BSTnode n : res.first) {
    		System.out.println("Single nodes: " + n.data);
    	}
    	for(BSTnode n : res.second) {
    		System.out.println("Root nodes: " + n.data);
    	}
    	*/
    	
    	//int y1pos = Arrays.binarySearch(Y, 1, Comparator.comparing(y -> IndexTools.revString(idToWord.search(y))));
    	//System.out.println("y1 POS: " + y1pos);
    	
    	/*
    	Integer[] testY = new Integer[]{7, 2, 1, 5, 4, 6};
    	BinarySearchReverseLexicographical bs = new BinarySearchReverseLexicographical(idToRevWord);
    	int pos = bs.binarySearchLeftBound(testY, IndexTools.revString("confine"), 0, testY.length-1);
    	System.out.println("POS : " + pos);
    	//System.out.println(Arrays.toString(testY));
    	*/
    	
    	
    }
}


