import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
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
	public static List<String> convertURLsToDocs(List<URL> urls) throws IOException {
		List<String> documents = new ArrayList<String>();
		int i = 1;
		for (URL url : urls) {
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			documents.add(extractText(br));
			br.close();
			System.out.println(i);
			i++;
		}
		return documents;
	}

	/**
	 * Yahooニュース記事のHTMLが入ったBufferedReaderから記事本文のテキストを抽出します
	 * @param br Yahooニュース記事のHTMLが入ったBufferedReader
	 * @return 記事本文
	 * @throws IOException 例外
	 */
	private static String extractText(BufferedReader br) throws IOException {
		StringBuilder sb = new StringBuilder();

		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		Elements articleBodyElems = Jsoup.parse(sb.toString()).getElementsByClass("ynDetailText"); // yahooニュースの記事本文<p class="ynDetailText">を取得
		String articleBodyText = articleBodyElems.text().replaceAll(SYMBOLS, ""); // Elementsから中身の文章を取得し、記号を削除

		return articleBodyText;
	}
}
