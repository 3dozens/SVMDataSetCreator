import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
		
		return documents;
	}
}
