import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SVMDataSetFromHTML {
	
	private static final String FILENAME = "/Users/thunders/research/assignments/SVM_Light/myOwnProduct/DataSet.txt";
	private static final int ARTICLE_NUMBER_PER_CATEGORY = 500;
//	private static final int CATEGORY_NUM = 2;
  
    public final static void main(String[] args) throws Exception {

		List<String> articleURLStrs = new ArrayList<>(); // Stringの記事URLリスト。あとでURLオブジェクトに変換する

		// 経済ニュースのURLを取得する
		// 1ループあたり"約"25件の記事URLを取得する。なぜか件数が1〜2件ズレる。境界値の問題かもしれない
		int economyArticleNum = 0; // 件数がズレてしまうため、定数の記事数を使わずにsize()で実際の件数を数える
		for (int i = 1; i <= ARTICLE_NUMBER_PER_CATEGORY / 25; i++) {
			// Yahooニュースの経済トップページを取得
			Document economyTopPage = Jsoup.connect("http://news.yahoo.co.jp/hl")
					.data("c", "bus_all")
					.data("p", String.valueOf(i))
					.get();

			// 新着記事リストから記事URLを抽出
			Element articleListUl = economyTopPage.getElementsByClass("listBd").get(0);
			Elements Atags = articleListUl.getElementsByTag("a");
			articleURLStrs.addAll(Atags.stream().map(a -> a.attr("href")).collect(Collectors.toList()));
			economyArticleNum = articleURLStrs.size();
		}

		// スポーツニュースのURLを取得する
		// 1ループあたり25件の記事URLを取得する
		for (int i = 1; i <= ARTICLE_NUMBER_PER_CATEGORY / 25; i++) {
			// Yahooニュースのスポーツトップページを取得
			Document sportsTopPage = Jsoup.connect("http://news.yahoo.co.jp/hl")
					.data("c", "spo")
					.data("p", String.valueOf(i))
					.get();

			// 新着記事リストから記事URLを抽出
			Element articleListUl = sportsTopPage.getElementsByClass("listBd").get(0);
			Elements Atags = articleListUl.getElementsByTag("a");
			articleURLStrs.addAll(Atags.stream().map(a -> a.attr("href")).collect(Collectors.toList()));
		}

		// StringのURLからURLオブジェクトに変換
		// FIXME 無関係な下位問題？
		List<URL> articleURLs = new ArrayList<>();
		for (String articleURLStr : articleURLStrs) {
			articleURLs.add(new URL(articleURLStr));
		}

		List<String> docs = URLProcessor.convertURLsToDocs(articleURLs);

		FeatureVectorGenerator generator = new FeatureVectorGenerator();
		Map<String, double[]> featureVectors = generator.generateTFIDFVectors(docs);

		File file = new File(FILENAME);
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

		SVMPrinterAndWriter.writeResultToFile(featureVectors, economyArticleNum, pw);

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
	  economyURLs.add(new URL("http://headlines.yahoo.co.jp/hl?a=20160131-00000002-awire-bus_all"));
	  economyURLs.add(new URL("http://headlines.yahoo.co.jp/hl?a=20151130-00000048-zdn_n-sci"));
	  economyURLs.add(new URL("http://headlines.yahoo.co.jp/hl?a=20151130-00000048-zdn_n-sci"));
	  economyURLs.add(new URL("http://headlines.yahoo.co.jp/hl?a=20151130-00000048-zdn_n-sci"));
	  economyURLs.add(new URL("http://headlines.yahoo.co.jp/hl?a=20151130-00000048-zdn_n-sci"));
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
 * Training SetとTest Dataは一度に一緒に作って、それをわける！単語とfeature vectorのインデックスの紐付けがなくなってしまう！ Done
 * FIXME クロールするの、Jsoup使えばええやん…なんで生URLをnewしてんねん…
*/

