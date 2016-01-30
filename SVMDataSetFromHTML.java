import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SVMDataSetFromHTML {
	
  private static final String FILENAME = "/Users/thunders/research/assignments/SVM_Light/myOwnProduct/DataSet.txt";
  
  public final static void main(String[] args) throws Exception {
	
	List<URL> urls = new ArrayList<>();
	urls.addAll(createEconomyURLList());
	urls.addAll(createSportsURLList());
	
	List<String> docs = URLProcessor.convertURLsToDocs(urls);
	
	FeatureVectorGenerator generator = new FeatureVectorGenerator();
	Map<String, double[]> featureVectors = generator.generateTFIDFVectors(docs);
	
	File file = new File(FILENAME);
	PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
	
	SVMPrinterAndWriter.writeResultToFile(featureVectors, 2, pw);
	
	pw.close();
	
  }
  
  /**
   * 経済のニュース記事のURLリストを返します
   * @return 経済ニュース記事のURLリスト
   * @throws MalformedURLException 例外
   */
  private static List<URL> createEconomyURLList() throws MalformedURLException {
	  List<URL> economyURLs = new ArrayList<>();
	  
	  economyURLs.add(new URL("http://headlines.yahoo.co.jp/hl?a=20151201-00000005-tospoweb-ent"));
	  economyURLs.add(new URL("http://headlines.yahoo.co.jp/hl?a=20151130-00000048-zdn_n-sci"));
	  
	  return economyURLs;
  }
  
  /**
   * スポーツのニュース記事のURLリストを返します
   * @return スポーツニュース記事のURLリスト
   * @throws MalformedURLException 例外
   */
  private static List<URL> createSportsURLList() throws MalformedURLException {
	  List<URL> sportsURLs = new ArrayList<>();
	  
	  sportsURLs.add(new URL("http://headlines.yahoo.co.jp/hl?a=20160127-00000062-dal-base"));
	  sportsURLs.add(new URL("http://headlines.yahoo.co.jp/hl?a=20160127-00000213-ism-base"));
	  sportsURLs.add(new URL("http://headlines.yahoo.co.jp/hl?a=20160127-00000093-sph-base"));
	  sportsURLs.add(new URL("http://headlines.yahoo.co.jp/hl?a=20160127-00000092-sph-base"));
	  sportsURLs.add(new URL("http://headlines.yahoo.co.jp/hl?a=20160127-00010008-fullcount-base"));
	  
	  return sportsURLs;
  }
  
  
}

/* TODO: スポーツと経済のニュースを500個ずつ計1000個取ってきて、それぞれ250個ずつをTraining Setとし、残りをTest Dataとして、SVMで機械学習する
 * 手順: 
 * 1．Training Setを作るプログラムを作る
 * 2．word listを全体で統一するために、FeatureVectorは一つにまとめる！
 * 3. targetの指定の方法を考える！
 * 
 * Training SetとTest Dataは一度に一緒に作って、それをわける！単語とfeature vectorのインデックスの紐付けがなくなってしまう！
*/

