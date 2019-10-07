import java.util.HashMap;

public class HashTest
{
    static HashMap<String, Integer> map = new HashMap<String, Integer> ();
    
    static void build (int N)
    {
        for (int i = 0; i < N; i++) {
            map.put ("String#" + i, i);
        }
    }
    
    static void test (String str)
    {
        int N = 10000000;
        int sum = 0;
        
        long t1 = System.currentTimeMillis ();
        for (int i = 0; i < N; i++) {
            Integer n = map.get (str);
            if (n != null) sum += n;
        }
        long t2 = System.currentTimeMillis ();
        
        System.out.println (str + ": time " + (t2-t1) + "; sum " + sum);
    }
    
    public static void main (String [] args)
    {
        build (1000000);
        test ("mouse");
        test ("String#532");
        test ("a quick brown fox jumps over the lazy dog");
        test ("aardvark polycyclic bitmap");
        test ("aaron bends nonconformity");
        test ("Zuma crackable stenographic");
        test ("public static void main (String [] args)");
    }
}
