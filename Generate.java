import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public class Generate
{
    static int [] powers31 = new int [100];
    
    static {
        powers31 [0] = 1;
        for (int i = 1; i < powers31.length; i++) {
            powers31 [i] = powers31 [i-1] * 31;
        }
    }

    static final int SET_SIZE = 1024 * 1024;
    
    static void iterate (String words[], int [] hashes,
                    String [][] words_by_length, int [][] hashes2_by_length, int [] factors_by_length, IntHashSet [] hashset, BitSet allbits)
    {
        long t0 = System.currentTimeMillis ();

        int nlens = words_by_length.length;
        for (int i = 0; i < words.length; i++) {
            int h0 = hashes [i];
            for (int len2 = 2; len2 < nlens; len2 ++) {
                int [] hashes2 = hashes2_by_length [len2];
                if (hashes2 == null) continue;
                int factor2 = factors_by_length [len2];
                int h00 = h0 * factor2;
                
                for (int j = 0; j < hashes2.length; j++) {
                    int h1 = h00 + hashes2 [j];

                    for (int len3 = 2; len3 < nlens; len3 ++) {
                        int [] hashes3 = hashes2_by_length [len3];
                        if (hashes3 == null) continue;
                        IntHashSet all_hashes = hashset [len3];
                        int factor3 = factors_by_length [len3];
                        int h10 = h1 * factor3;
  
                        int value = -h10;
                        if (allbits.get (value & (SET_SIZE-1)) && all_hashes.contains (value)) {
                            for (int k = 0; k < hashes3.length; k++) {
                                if (hashes3 [k] == value)
                                    System.out.println (words [i] + " " + words_by_length [len2][j] + " " + words_by_length [len3][k]);
                            }
                        }
                    }
                }
            }
        }
        System.out.println ("Total time: " + (System.currentTimeMillis () - t0));
    }
    
    public static void main (String [] args) throws Exception
    {
        ArrayList<String> word_vector = new ArrayList<String> ();
  
        LineNumberReader r = new LineNumberReader (new FileReader ("corncob_lowercase.txt"));
        while (true) {
            String s = r.readLine ();
            if (s == null) break;
            word_vector.add (s);
        }
        String [] words = word_vector.toArray (new String [word_vector.size ()]);
        System.out.println ("Words read: " + words.length);
    
        // hash values of words
        int [] hashes = new int [words.length];

        // hash values of words with space in front
        int [] hashes2 = new int [words.length];
        
        // to be multiplied by the current hash before adding this word
        int [] factors = new int [words.length];
        
        int max_length = 0;
        
        for (int i = 0; i < words.length; i++) {
            String word = words [i];
            hashes [i] = word.hashCode ();
            factors [i] = powers31 [word.length ()];
            hashes2 [i] = ' ' * factors [i] + hashes [i];
            factors [i] *= 31;
            if (word.length () > max_length) max_length = word.length ();
        }
        
        System.out.println ("max length: " + max_length);

        HashMap<Integer, ArrayList<String>> word_list_by_length = new HashMap<> ();
        for (String s : words) {
            int len = s.length ();
            ArrayList<String> a = word_list_by_length.get (len);
            if (a == null) {
                a = new ArrayList<String> ();
                word_list_by_length.put (len, a);
            }
            a.add (s);
        }
        
        String [][] words_by_length = new String [max_length+1][];
        int [][] hashes2_by_length = new int [max_length+1][];
        int [] factors_by_length = new int [max_length+1];
        
        IntHashSet [] all_hashes_by_length = new IntHashSet [max_length+1];
        
        // very rough preliminary check
        BitSet allbits = new BitSet (SET_SIZE);
        
        for (int word_length = 0; word_length <= max_length; word_length++) {
            factors_by_length [word_length] = powers31 [word_length+1];

            ArrayList<String> a = word_list_by_length.get (word_length);
            if (a == null) continue;
            final String [] words_of_this_length = a.toArray (new String [a.size ()]);

            final int space_hash = ' ' * powers31 [word_length];
            words_by_length [word_length] = words_of_this_length;
            hashes2_by_length [word_length] = new int [words_by_length [word_length].length];
            all_hashes_by_length [word_length] = new IntHashSet ();
            
            for (int j = 0; j < words_of_this_length.length; j++) {
                int hash = words_of_this_length[j].hashCode () + space_hash;
                hashes2_by_length [word_length][j] = hash;
                all_hashes_by_length [word_length].add (hash);
                allbits.set (hash & (SET_SIZE-1));
            }
        }
        
        iterate (words, hashes, words_by_length, hashes2_by_length, factors_by_length, all_hashes_by_length, allbits);
    }
}
