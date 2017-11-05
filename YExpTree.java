import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;
import java.util.Arrays;
import java.util.List;

public class YExpTree extends ExpressionTree {

   public static void main(String args[]) {
      YExpTree y = new YExpTree("5 + (x / y + z * 7) - 2");
      Utility.print(y);
      y = new YExpTree(Utility.getInput());
      Utility.print(y);
   }

   public String fullyParenthesised() {
      StringBuilder ans = new StringBuilder();
      fullyParenthesised((BNode<String>) root(), ans);
      return ans.toString();
   }
   //HELPER METHODS
   private void fullyParenthesised(BNode<String> p, StringBuilder ans) {
     if(p == null)
       return;
     if(isLeaf(p)) ans.append(p.getData());
     if( isInternal(p) || isRoot(p) ) {
       ans.append("(");
       fullyParenthesised(p.getLeft(), ans);
       ans.append(" " + p.getData() + " ");
       fullyParenthesised(p.getRight(), ans);
       ans.append(")");
     }
   }

   private int rank(String op){
     switch(op){
       case "*": case "/": return 3;
       case "+": case "-": return 2;
       case "(": return 0; case ")": return 1;
       default: return -1;
     }
   }

   private String[] toArray(String infix) {
     infix = infix.replaceAll("\\s+","");
     infix = infix.replace("(","(^").replace(")","^)").replace("+","^+^");
     infix = infix.replace("-","^-^").replace("*","^*^").replace("/","^/^");
     String term[] = infix.split("\\^+", -1);
     return term;
   }

   private ArrayList<String> toPrefix(String infix) {
     Stack<String> s = new Stack<>(); s.push(" ");
     String term[] = toArray(infix);
     ArrayList<String> prefix = new ArrayList<>();
     for(int i = term.length-1;i>=0;i--){
       while(term[i] == " ") i--;
       if(rank(term[i]) < 0) prefix.add(0,term[i]);
       if(rank(term[i]) == 0)
         while(!s.peek().equals(" ") && !s.empty()) prefix.add(0,s.pop().toString());
       if(rank(term[i]) == 1) s.push(" ");
       if(rank(term[i]) > 1 && !s.empty()){
         while(rank(term[i]) < rank(s.peek().toString())) prefix.add(0,s.pop().toString());
         s.push(term[i]);
       }
     }
     while(!s.empty()){
       if(s.peek().toString().equals(" ")) s.pop();
       else prefix.add(0,s.pop().toString());
     }
     return prefix;
   }

   private ArrayList<ArrayList<String>> cut(ArrayList<String> s, ArrayList<String> pre){
     String elem = pre.get(0);
     int count, inP, outP; count = inP = outP = 0;
     String find = s.get(count);
     for(int i = 0; i < s.size(); i++){
       find = s.get(i);
       if(find.equals("("))
         while( !find.equals(")") ) {
           i++;
           if( i == s.size()-1 ) find = ")";
           else find = s.get(i);
           if(find.equals(elem)) inP = i;
         }
       else if(find.equals(elem)) outP = i;
     }
     if(outP > 0) count = outP; else count = inP;
     ArrayList<ArrayList<String>> ans = new ArrayList<>();
     ArrayList<String> zero = new ArrayList<>();
     ArrayList<String> one = new ArrayList<>();
     for(int i = 0; i < count; i++) zero.add(s.get(i));
     for(int i = count+1; i<s.size(); i++) one.add(s.get(i));
     ans.add( zero ); ans.add( one );
     return ans;
   }

   private void makeTree(BNode<String> p, ArrayList<String> s) {
     if(s.equals(null)) return;
     String t = "";
     for(int i = 0; i<s.size(); i++) t = t + s.get(i);
     ArrayList<String> pre = toPrefix(t);
     String elem = pre.get(0);
     ArrayList<ArrayList<String>> kids = cut(s,pre);
     if (p.getLeft() != null){
       p.setRight(new BNode<String>(elem, p, null, null));
       super.size = super.size + 1;
       if( pre.size() > 1 ){
         makeTree(p.getRight(), kids.get(0));
         makeTree(p.getRight(), kids.get(1));
       }
     }
     else{
       BNode<String> l = new BNode<>( elem, p, null, null );
       p.setLeft(l);
       super.size = super.size + 1;
       if( pre.size() > 1 ){
         makeTree(p.getLeft(), kids.get(0));
         makeTree(p.getLeft(), kids.get(1));
       }
     }
   }
   //CONSTRUCTOR
   public YExpTree(String s) {
      super();
      ArrayList<String> pre = toPrefix(s);
      String elem = pre.get(0);
      BNode<String> rt = new BNode<String>( elem, null, null, null);
      super.root = rt; super.size = 1;
      String[] a = toArray(s);
      List<String> l = Arrays.<String>asList(a);
      ArrayList<String> al = new ArrayList<String>(l);
      ArrayList<ArrayList<String>> kids = cut(al,pre);
      makeTree(rt, kids.get(0));
      makeTree(rt, kids.get(1));
   }

}




class Utility {
   public static String getInput() {
      System.out.println("Enter an algebraic expression: ");
      Scanner s = new Scanner(System.in);
      String answer =  s.nextLine();
      s.close();
      return answer;
   }

   public static void print(ExpressionTree y) {
      System.out.println("Prefix: " + y.prefix());
      System.out.println("Postfix: " + y.postfix());
      System.out.println("Fully parenthesised: " + y.fullyParenthesised());
      System.out.println("-----------------");
   }
}

abstract class ExpressionTree extends BinaryTree<String> {
   public ExpressionTree() {
      super();
   }
   public abstract String fullyParenthesised();

   public final String postfix() {
      String ans = "";
      ArrayList<Node<String>> l = postOrder();
      for (Node<String> b:l) ans += b.getData() + " ";
      return ans;
   }

   public final String prefix() {
      String ans = "";
      ArrayList<Node<String>> l = preOrder();
      for (Node<String> b:l) ans += b.getData() + " ";
      return ans;
   }
}

// classes BinaryTree, BNode, Tree and Node exacctly as implemented in our course

class BinaryTree<T> extends Tree<T> {
   public BinaryTree() {
      super();
   }

   public void addRoot(T x) throws Exception {
      if (root != null) throw new Exception("The tree is not empty");
      root = new BNode<T>(x, null, null, null);
      size++;
   }

   public void addLeft(BNode<T> n, T x) throws Exception {
      if (n.getLeft() != null) throw new Exception("Already used");
      n.setLeft(new BNode<T>(x, n, null, null));
      size++;
   }

   public void addRight(BNode<T> n, T x) throws Exception {
      if (n.getRight() != null) throw new Exception("Already used");
      n.setRight(new BNode<T>(x, n, null, null));
      size++;
   }

   // returns the parent of the removed node
   public BNode<T> removeNode(BNode<T> n) {
      if (isLeaf(n)) {  // base case
         BNode<T> p = (BNode<T>) parent(n);
         if (p == null) root = null;
         else p.removeChild(n);
         size--;
         return p;
      }
      if (n.getLeft() != null) {
         BNode<T> m = rightmostLeftDescendant(n);
         n.setData(m.getData());
         return removeNode(m);   // recursively remove rightmost left descendant
      }
      // otherwise n does have a right child
      BNode<T> m = leftmostRightDescendant(n);
      n.setData(m.getData());
      return removeNode(m);
   }

   public BNode<T> leftmostRightDescendant(BNode<T> n) {
      BNode<T> m = n.getRight();
      while (m.getLeft() != null) m = m.getLeft();
      return m;
   }

   public BNode<T> rightmostLeftDescendant(BNode<T> n) {
      BNode<T> m = n.getLeft();
      while (m.getRight() != null) m = m.getRight();
      return m;
   }

   public ArrayList<BNode<T>> inOrder() {
      ArrayList<BNode<T>> answer = new ArrayList<BNode<T>>();
      inOrder((BNode<T>) root(), answer);
      return answer;
   }

   public void inOrder(BNode<T> n, ArrayList<BNode<T>> v) {
      if (n == null) return;
      inOrder(n.getLeft(), v);
      v.add(n);
      inOrder(n.getRight(), v);
   }

   public ArrayList<BNode<T>> flatOrder() {
      return inOrder();
   }
}

class BNode<T> extends Node<T> {
   BNode<T> left, right;

   public BNode(T d, BNode<T> p, BNode<T> l, BNode<T> r) {
      setData(d);
      setParent(p);
      left = l;
      right = r;
   }

   public void setLeft(BNode<T> n) {
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

class Tree<T> {
   protected Node<T> root;
   public int size;

   public Tree() {
      root = null;
      size = 0;
   }

   public Node<T> root() {
      return root;
   }

   public Node<T> parent(Node<T> v) {
      return v.getParent();
   }

   public Iterator<? extends Node<T>> children(Node<T> v) {
      return v.children();
   }

   public boolean isRoot(Node<T> v) {
      return v == root;
   }

   public boolean isInternal(Node<T> v) {
      return children(v).hasNext();
   }

   public boolean isLeaf(Node<T> v) {
      return !isInternal(v);
   }

   public int size() {
      return size;
   }

   public boolean empty() {
      return size == 0;
   }

   public void replace(Node<T> v, T t) {
      v.setData(t);
   }

   public int depth(Node<T> v) {
      if (isRoot(v))
         return 0;
      return 1 + depth(parent(v));
   }

   public int height(Node<T> v) {
      if (isLeaf(v))
         return 0;
      int maxChild = 0;
      Iterator<? extends Node<T>> c = children(v);
      while (c.hasNext()) {
         int hc = height((Node<T>) c.next());
         if (hc > maxChild)
            maxChild = hc;
      }
      return maxChild + 1;
   }

   public int height() {
      if (root == null)
         return -1;
      return height(root);
   }

   public ArrayList<Node<T>> preOrder() {
      ArrayList<Node<T>> answer = new ArrayList<>();
      preOrder(root(), answer);
      return answer;
   }

   public void preOrder(Node<T> n, ArrayList<Node<T>> v) {
      if (n == null)
         return;
      v.add(n);
      Iterator<? extends Node<T>> x = children(n);
      while (x.hasNext()) {
         Node<T> m = x.next();
         preOrder(m, v);
      }
   }

   public ArrayList<Node<T>> postOrder() {
      ArrayList<Node<T>> answer = new ArrayList<Node<T>>();
      postOrder(root(), answer);
      return answer;
   }

   public void postOrder(Node<T> n, ArrayList<Node<T>> v) {
      if (n == null)
         return;
      Iterator<? extends Node<T>> x = children(n);
      while (x.hasNext()) {
         Node<T> m = x.next();
         postOrder(m, v);
      }
      v.add(n);
   }

   public ArrayList<Node<T>> levelOrder() {
      ArrayList<Node<T>> waiting = new ArrayList<>();
      waiting.add(null);
      if (root() == null)
         return waiting;
      waiting.add(root());
      int done = 0;
      while (done < waiting.size()) {
         Node<T> oldNode = waiting.get(done++);
         if (oldNode == null) {
            if (done < waiting.size())
               waiting.add(null);
            continue;
         }
         Iterator<? extends Node<T>> c = children(oldNode);
         while (c.hasNext())
            waiting.add(c.next());
      }
      return waiting;
   }

   public ArrayList<? extends Node<T>> flatOrder() {
      return preOrder();
   }

   public String toString() {
      return treePrint(null);
   }

   public String treePrint(Node<T> cursor) {
      String answer = "  ";
      Iterator<Node<T>> lev = levelOrder().iterator();
      Iterator<? extends Node<T>> flat = flatOrder().iterator();
      lev.next(); // skip first new line
      while (lev.hasNext()) {
         Node<T> o = lev.next();
         if (o == null) {
            answer += "\n  ";
            flat = flatOrder().iterator();
         } else
            while (true) {
               boolean atCursor = false;
               Node<T> n = flat.next();
               if (n == cursor)
                  atCursor = true;
               String s = n.getData().toString();
               if (atCursor)
                  s = "* " + s + " *";
               if (n == o) {
                  answer += s + " ";
                  break;
               } else {
                  for (int i = 0; i < s.length(); i++)
                     answer += ' ';
                  answer += ' ';
               }
            }
      }
      return answer;
   }
}

abstract class Node<T> {
   protected Node<T> parent;
   protected T data;

   public abstract Iterator<? extends Node<T>> children();

   public void setParent(Node<T> n) {
      parent = n;
   }

   public void setData(T t) {
      data = t;
   }

   public Node<T> getParent() {
      return parent;
   }

   public T getData() {
      return data;
   }
}
