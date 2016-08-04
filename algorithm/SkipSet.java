package algorithms.ds.skiplist;
/* The authors of this work have released all rights to it and placed it
 	in the public domain under the Creative Commons CC . waiver
   (http://creativecommons.org/publicdomain/zero/./).
   
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
   CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
  
  Retrieved from: http://en.literateprograms.org/Skip_list_(Java)?oldid=
 */
  
 class SkipNode<E extends Comparable<? super E>>
 {
      public final E value;
      public final SkipNode<E>[] forward; // array of pointers
  
      @SuppressWarnings("unchecked")
      public SkipNode(int level, E value) 
      {
          forward = new SkipNode[level + 1];
          this.value = value;
      }
  }
  
  public class SkipSet<E extends Comparable<? super E>>
  {
      public static final double P = 0.5;
      public static final int MAX_LEVEL = 6 ;
      public static int randomLevel() {
          int lvl = (int)(Math.log(1.-Math.random())/Math.log(1.-P));
          return Math.min(lvl, MAX_LEVEL);
      } 
  
      public final SkipNode<E> header = new SkipNode<E>(MAX_LEVEL, null);
      public int level = 0;
  
      public String toString()
      {
          StringBuilder sb = new StringBuilder();
          sb.append("{");
          SkipNode<E> x = header.forward[0];
          while (x != null) {
              sb.append(x.value);
              x = x.forward[0];
              if (x != null)
                  sb.append(",");
          }    
          sb.append("}");
          return sb.toString();
      }
      public boolean contains(E searchValue)
      {
    	  SkipNode<E> x = header;
          for (int i = level; i >= 0; i--) {
  	    while (x.forward[i] != null && x.forward[i].value.compareTo(searchValue) < 0) {
  	        x = x.forward[i];
  	    }
  	}
  	x = x.forward[0];
          return x != null && x.value.equals(searchValue);
      }
      @SuppressWarnings("unchecked")
      public void insert(E value)
      {
    	  SkipNode<E> x = header;	
    	  SkipNode<E>[] update = new SkipNode[MAX_LEVEL + 1];
  
          for (int i = level; i >= 0; i--) {
  	    while (x.forward[i] != null && x.forward[i].value.compareTo(value) < 0) {
  	        x = x.forward[i];
  	    }
  	    update[i] = x; 
  	}
  	x = x.forward[0];
  
          if (x == null || !x.value.equals(value)) {        
              int lvl = randomLevel();
        
              if (lvl > level) {
  	        for (int i = level + 1; i <= lvl; i++) {
  	            update[i] = header;
  	        }
  	        level = lvl;
  	    }
              x = new SkipNode<E>(lvl, value);
  	    for (int i = 0; i <= lvl; i++) {
  	        x.forward[i] = update[i].forward[i];
  	        update[i].forward[i] = x;
  	    }
          }
      }
      @SuppressWarnings("unchecked")
      public void delete(E value)
      {
    	  SkipNode<E> x = header;	
    	  SkipNode<E>[] update = new SkipNode[MAX_LEVEL + 1];
 
         for (int i = level; i >= 0; i--) {
 	    while (x.forward[i] != null && x.forward[i].value.compareTo(value) < 0) {
 	        x = x.forward[i];
 	    }
 	    update[i] = x; 
 	}
 	x = x.forward[0];
 
         if (x.value.equals(value)) {
             for (int i =0 ; i <= level; i++) {
 	        if (update[i].forward[i] != x)
 	            break;
 	        update[i].forward[i] = x.forward[i];
 	    }
             while (level > 0 && header.forward[level] == null) {
 	        level--;
 	    }
         }
     }
 
     public static void main(String[] args) {
 
         SkipSet<Integer> ss = new SkipSet<Integer>();
         System.out.println(ss);
 
         ss.insert(5);
         ss.insert(10);
         ss.insert(7);
         ss.insert(7);
         ss.insert(6);
         
         if (ss.contains(7)) {
             System.out.println(" is in the list");
         }
 
         System.out.println(ss);
 
         ss.delete(7);
         System.out.println(ss);
         
         if (!ss.contains(7)) {
             System.out.println(" has been deleted");
         }
     }
 }
