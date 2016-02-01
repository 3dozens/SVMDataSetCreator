import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
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

		List<String> articleURLs = new ArrayList<>();

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
			articleURLs.addAll(Atags.stream().map(a -> a.attr("href")).collect(Collectors.toList()));
			economyArticleNum = articleURLs.size(); // 件数がズレてしまうため、定数の記事数を使わずにsize()で実際の件数を数える
		}

		// スポーツニュースのURLを取得する
		// 1ループあたり"約"25件の記事URLを取得する
		for (int i = 1; i <= ARTICLE_NUMBER_PER_CATEGORY / 25; i++) {
			// Yahooニュースのスポーツトップページを取得
			Document sportsTopPage = Jsoup.connect("http://news.yahoo.co.jp/hl")
					.data("c", "spo")
					.data("p", String.valueOf(i))
					.get();

			// 新着記事リストから記事URLを抽出
			Element articleListUl = sportsTopPage.getElementsByClass("listBd").get(0);
			Elements Atags = articleListUl.getElementsByTag("a");
			articleURLs.addAll(Atags.stream().map(a -> a.attr("href")).collect(Collectors.toList()));
		}

		List<String> docs = URLProcessor.convertURLsToDocs(articleURLs);

		FeatureVectorGenerator generator = new FeatureVectorGenerator();
		Map<String, double[]> featureVectors = generator.generateTFIDFVectors(docs);

		File file = new File(FILENAME);
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

		SVMPrinterAndWriter.writeResultToFile(featureVectors, economyArticleNum, pw);

		pw.close();
	
    }
    
}
