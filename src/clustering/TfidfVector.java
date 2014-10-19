package clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import util.Dbg;

public class TfidfVector {
    public static final String MODULE = "TFIDF";
    public static final boolean DBG = true;

    public static List<List<Double>> articlesToVectors(List<Article> arts) {
        // Count document frequence.
        final HashMap<String, Integer> wordToIdx = new HashMap<String, Integer>();
        final ArrayList<String> idxToWord = new ArrayList<String>();
        final ArrayList<Integer> idxToDf = new ArrayList<Integer>();
        for (Article art : arts) {
            final HashSet<String> wordsInThisArt = new HashSet<String>();
            for (String word : art) {
                Integer idx = wordToIdx.get(word);
                if (idx == null) { // A new word hasn't occurred in anywhere.
                    wordToIdx.put(word, wordToIdx.size());
                    idxToWord.add(word);
                    idxToDf.add(1);
                    wordsInThisArt.add(word);
                } else {
                    if (!wordsInThisArt.contains(word)) {
                        // A word has occurred in other article before, but
                        // hasn't occurred in this one.
                        idxToDf.set(idx, idxToDf.get(idx) + 1);
                        wordsInThisArt.add(word);
                    }
                }
            }
        }
        final HashMap<Integer, Double> idfCache = new HashMap<Integer, Double>();
        final double logD = Math.log(arts.size());

        // Build vectors for all articles. Vector size (number of total words)
        // is map.size().
        List<List<Double>> vectors = new ArrayList<List<Double>>();
        for (Article art : arts) {
            final List<Double> vec = new ArrayList<Double>();
            for (int i = 0; i < wordToIdx.size(); i++) {
                vec.add(0.0); // Initialize all elements to 0.
            }
            // Count term frequency.
            for (String word : art) {
                final int index = wordToIdx.get(word);
                vec.set(index, vec.get(index) + 1);
            }

            String dbgstr = "";
            for (int i = 0; i < Math.min(3, art.size()); i++) {
                dbgstr += art.get(i) + " ";
            }

            Dbg.print(DBG, MODULE, "Article: " + dbgstr + "...");

            // Calculate and store tfidf.
            double norm = 0;
            for (int i = 0; i < wordToIdx.size(); i++) {
                if (vec.get(i) != 0) {
                    final Integer df = idxToDf.get(i);
                    Double idf = idfCache.get(df);
                    if (idf == null) {
                        idf = logD - Math.log(df);
                        idfCache.put(df, idf);
                    }
                    final double tf = vec.get(i);
                    final double tfidf = tf * idf;
                    vec.set(i, tfidf);
                    norm += tfidf * tfidf;
                    Dbg.print(DBG, MODULE, String.format(
                            "%s, tf %.0f, df %d, tfidf %.2f", idxToWord.get(i),
                            tf, df, tfidf));

                }
            }

            // Normalize vector.
            norm = Math.sqrt(norm);
            for (int i = 0; i < wordToIdx.size(); i++) {
                if (vec.get(i) != 0) {
                    vec.set(i, vec.get(i) / norm);
                }
            }

            vectors.add(vec);
        }
        Dbg.print(DBG, MODULE, String.format("%d articles, %d distinct words",
                arts.size(), wordToIdx.size()));
        return vectors;
    }
}
