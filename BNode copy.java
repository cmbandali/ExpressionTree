import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;
import java.util.Arrays;
import java.util.List;

class BNode<T> extends Node<T> {
   BNode<T> left, right;

   public BNode(T d, BNode<T> p, BNode<T> l, BNode<T> r) {
      setData(d);
      setParent(p);
      left = l;
      right = r;
   }

   public void setLeft(BNode<T> n) {
     System.out.println("settingg...");
      left = n;
   }

   public void setRight(BNode<T> n) {
      right = n;
   }

   public BNode<T> getLeft() {
      return left;
   }

   public BNode<T> getRight() {
      return right;
   }

   public Iterator<BNode<T>> children() {
      ArrayList<BNode<T>> v = new ArrayList<>();
      if (left != null) v.add(left);
      if (right != null) v.add(right);
      return v.iterator();
   }

   public void removeChild(BNode<T> n) {
      if (getLeft() == n) setLeft(null);
      else setRight(null);
   }

   public String toString() {  // for testing and debugging
      return "Node " + data;
   }
}
