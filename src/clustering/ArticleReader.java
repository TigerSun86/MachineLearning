package clustering;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ArticleReader {
    private static final String EXT = ".txt";

    public static List<List<Article>> read(final String topicFile) {
        BufferedReader br = null;
        try {
            final URL fileUrl = new URL(topicFile);
            br = new BufferedReader(new InputStreamReader(fileUrl.openStream()));
        } catch (MalformedURLException e) {
            System.err.println("Cannot find file " + topicFile);
            return null;
        } catch (IOException e) {
            System.err.println("Cannot find file " + topicFile);
            return null;
        }

        final ArrayList<String> names = new ArrayList<String>();
        try {
            boolean haveMore = true;
            while (haveMore) {
                final String name;
                name = br.readLine();
                if (name == null) {
                    haveMore = false;
                } else {
                    if (!name.isEmpty()) {
                        names.add(name);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error when reading " + topicFile);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (names.isEmpty()) {
            return null;
        }

        final List<List<Article>> ret = new ArrayList<List<Article>>();
        final String path = topicFile.substring(0,
                topicFile.lastIndexOf((int) '/') + 1);
        for (String name : names) {
            final String fn = path + name + EXT;
            final List<Article> arts = readFile(fn);
            if (arts != null) {
                ret.add(arts);
            }
        }
        return ret;
    }
    private static final String ARTICLE_SEPARATOR = "^(--[0-9A-Za-z]+--).*";
    
    private static List<Article> readFile(final String fn) {
        BufferedReader br = null;
        try {
            final URL fileUrl = new URL(fn);
            br = new BufferedReader(new InputStreamReader(fileUrl.openStream()));
        } catch (MalformedURLException e) {
            System.err.println("Cannot find file " + fn);
            return null;
        } catch (IOException e) {
            System.err.println("Cannot find file " + fn);
            return null;
        }

        final List<Article> arts = new ArrayList<Article>();
        try {
            boolean haveMore = true;
            Article art = new Article();
            while (haveMore) {
                final String line = br.readLine();
                if (line == null) { // End of file.
                    if (!art.isEmpty()) {
                        arts.add(art); // Store last article.
                    }
                    haveMore = false;
                } else if (line.matches(ARTICLE_SEPARATOR)) {
                    // New article.
                    if (!art.isEmpty()) {
                        arts.add(art); // Store last article.
                    }
                    art = new Article();
                } else {
                    List<String> strs = processTxt(line);
                    if (!strs.isEmpty()) {
                        art.addAll(strs);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error when reading " + fn);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return arts;
    }

    private static final HashSet<String> STOPWORDS = new HashSet<String> ();
    static {
        STOPWORDS.add("m");
        STOPWORDS.add("re");
        STOPWORDS.add("s");
    }
    
    private static List<String> processTxt(String line) {
        final String[] words = line.split("[\\p{Blank} | \\p{Punct}]");
        final List<String> ret = new ArrayList<String>();
        for (String word : words) {
            if (!word.isEmpty() && !word.equals("s")) {
                final String w = word.toLowerCase();
                if (!STOPWORDS.contains(w)){
                    // To prevent "John's" become "John" and "s".
                    ret.add(w);
                }
            }
        }
        return ret;
    }
}
