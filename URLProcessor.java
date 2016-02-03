import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class URLProcessor {

	// 除去する記号
	private static final String SYMBOLS = "[、。()（）>＞<＜./!！?？|~=＝#$%&'\"”“※;:…+*@{}«•»「」『』〔〕【】・\\-＝^\\s　]";

	/**
	 * ニュース記事のURLリストから記事本文のリストに変換します。
	 * @param urls ニュース記事のURLリスト
	 * @return 記事本文のリスト
	 * @throws IOException 例外
	 */
	public static List<String> convertURLsToDocs(List<String> urls) throws IOException {
		List<String> documents = new ArrayList<String>();
		
		int i = 1;
		for (String url : urls) {
			Document article = Jsoup.connect(url).get();
			String articleBodyText = article.getElementsByClass("ynDetailText").text().replaceAll(SYMBOLS, ""); // yahooニュースの記事本文<p class="ynDetailText">を取得
			documents.add(articleBodyText);
			
			System.out.println(i); // 現在何件目を処理中か表示
			i++;
		}
		System.out.println("crawl done.");
		
		return documents;
	}
	
	/**
	 * Yahooニュースのカテゴリートップページの記事一覧から
	 * そのカテゴリーのニュース記事のURLリストを取得します
	 * @param category カテゴリーURL
	 * @param pageNumber 記事一覧の何ページ分の記事URLを取得するか。1ページあたり25件取得します。
	 * @return ニュース記事URLリスト
	 * @throws IOException 例外
	 */
	public static List<String> crawlArticleURLs(String category, int pageNumber) throws IOException {
    	List<String> articleURLs = new ArrayList<>();
    	
    	// 1ループあたり"約"25件の記事URLを取得する。なぜか件数が1〜2件ズレる。境界値の問題かもしれない
    	for (int i = 1; i <= pageNumber; i++) {
			
			// Yahooニュースのカテゴリートップページを取得
			Document topPage = Jsoup.connect("http://news.yahoo.co.jp/hl")
					.data("c", category)
					.data("p", String.valueOf(i))
					.get();

			// 新着記事リストから記事URLを抽出
			Element articleListUl = topPage.getElementsByClass("listBd").get(0);
			Elements Atags = articleListUl.getElementsByTag("a");
			articleURLs.addAll(Atags.stream().map(a -> a.attr("href")).collect(Collectors.toList()));
		}
    	
    	return articleURLs;
    }
}
