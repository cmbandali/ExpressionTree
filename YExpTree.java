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
