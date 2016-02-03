import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SVMDataSetFromHTML {
	
	private static final String FILENAME = "/Users/thunders/research/assignments/SVM_Light/myOwnProduct/DataSet.txt";
	private static final int ARTICLE_NUMBER_PER_CATEGORY = 500;
	private static final int CRAWL_PAGE_NUMBER_PER_CATEGORY = ARTICLE_NUMBER_PER_CATEGORY / 25; // クロールするページ数
//	private static final int CATEGORY_NUM = 2;
  
    /**
     * Yahooニュースの記事からSVMLightのデータセットの形式で
     * FeatureVector値をファイルに出力します
     */
    public final static void main(String[] args) throws Exception {

		List<String> articleURLs = new ArrayList<>();

		// 経済ニュースのURLを取得する
		List<String> economyArticleURLs = URLProcessor.crawlArticleURLs("bus_all", CRAWL_PAGE_NUMBER_PER_CATEGORY);
		int economyArticleNum = economyArticleURLs.size(); // 件数がズレてしまうため、定数の記事数を使わずにsize()で実際の件数を数える
		articleURLs.addAll(economyArticleURLs);

		// スポーツニュースのURLを取得する
		List<String> sportsArticleURLs = URLProcessor.crawlArticleURLs("spo", CRAWL_PAGE_NUMBER_PER_CATEGORY);
		articleURLs.addAll(sportsArticleURLs);

		List<String> docs = URLProcessor.convertURLsToDocs(articleURLs);

		FeatureVectorGenerator generator = new FeatureVectorGenerator();
		Map<String, double[]> featureVectors = generator.generateTFIDFVectors(docs);

		File file = new File(FILENAME);
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

		SVMPrinterAndWriter.writeResultToFile(featureVectors, economyArticleNum, pw);

		pw.close();
	
    }
}
