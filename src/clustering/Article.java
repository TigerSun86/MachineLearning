package clustering;

import java.util.LinkedList;

public class Article extends LinkedList<String> {
    private static final long serialVersionUID = 1L;
    public final String id;

    public Article(String id) {
        this.id = id;
    }
}
