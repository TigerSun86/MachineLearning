package clustering;

import java.util.ArrayList;
import java.util.List;

import clustering.AHClustering.Mode;

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
        List<Vector>ret2 =TfidfVector.articlesToVectors(arts);
        // List<CentralCluster> c = BisectingKmeans.cluster(ret2, 3, BisectingKmeans.WayToPick.LARGEST,10);
        //List<CentralCluster> c = Kmeans.cluster(ret2, 6);
/*        for (CentralCluster c2: c){

            System.out.println(c2);
        }*/
        ClusterTree c = AHClustering.cluster(ret2, Mode.CST);
        for (Cluster c2: c.getKCluster(6)){

            System.out.println(c2);
        }
        
        System.out.println("kmean");
        List<Vector> centroidsIn = new ArrayList<Vector> ();
        for (Cluster c2: c.getKCluster(6)){
            centroidsIn.add(c2.getCenter());
        }

       for (Cluster c2: Kmeans.cluster(ret2, centroidsIn)){

            System.out.println(c2);
        }
       ClusterList cl =  BisectingKmeans.cluster(ret2, 6, BisectingKmeans.WayToPick.LARGEST,10);
       System.out.println("entropy "+cl.entropy());
       System.out.println("fmeasure "+cl.fMeasure());
       System.out.println("similarity "+cl.overallSimilarity());
       System.out.println("silcoe "+cl.silhouetteCoefficient());
    }
}
