package clustering;

import java.util.ArrayList;
import java.util.List;

import util.Dbg;

public class Test {
    private static final String TOY = "http://cs.fit.edu/~pkc/classes/ml-internet/data/toy-topics.txt";
    private static final String TRAIN = "http://cs.fit.edu/~pkc/classes/ml-internet/data/news/news-topics.txt";
   
    public static void main(String[] args){
        Dbg.dbgSwitch = true;
        Dbg.defaultSwitch = true;
        List<List<Article>> ret = ArticleReader.read(TRAIN);
        List<Article> arts = new ArrayList<Article>();
        for(List<Article>a: ret){
            for(Article b: a){
                arts.add(b);
            }
        }
        List<List<Double>>ret2 =TfidfVector.articlesToVectors(arts);
        
        
    }
}
