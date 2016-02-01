import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

public class SVMPrinterAndWriter {

	/**
	 * 特徴ベクトルのMapからドキュメントとTF-IDF、その他の記事とのcosine similarityを出力します
	 * @param featureVectors 特徴ベクトル
	 */
	public static void printResult(Map<String, double[]> featureVectors) {
		FeatureVectorGenerator generator = new FeatureVectorGenerator();

		for (Map.Entry<String, double[]> entry : featureVectors.entrySet()) {

			System.out.println("--- Document ---");
			System.out.println(entry.getKey());
			System.out.println();

			System.out.println("--- TF-IDF ---");

			double[] featureVector = entry.getValue();
			System.out.print("(");
			for (int i = 0; i < featureVector.length; i++) {
				System.out.print(String.format("%.2f", featureVector[i]));
				if (i != featureVector.length - 1) {
					System.out.print(", "); // 最後の単語出ない場合、カンマで区切る
				}
			}
			System.out.println(")");
			System.out.println();

			System.out.println("--- Cosine Similarity ---");

			for (Entry<String, double[]> calcTarget : featureVectors.entrySet()) {
				if (entry == calcTarget) continue;

				double cosSim = generator.calcCosineSimilarity(featureVector, calcTarget.getValue()); // Value = featureVector値
				System.out.println(calcTarget.getKey()); // Key = ドキュメント
				System.out.println("-> " + cosSim);
			}

			System.out.println("");
		}
	}

	/**
	 * 特徴ベクトルをSVMのデータセットの形式で出力します
	 * @param featureVectors 特徴ベクトル
	 * @param target ターゲット
	 */
	public static void printResultForSVM(Map<String, double[]> featureVectors, int target) {

		for (Map.Entry<String, double[]> entry : featureVectors.entrySet()) {

			System.out.print(target + " ");

			int count = 1;
			for (double featureVector : entry.getValue()) {
				if (featureVector == 0.0) continue;
				System.out.print(count + ":" + featureVector + " ");
				count++;
			}
			System.out.println();
		}
	}

	/**
	 * 特徴ベクトルをSVMのデータセットの形式でファイルに出力します
	 * @param featureVectors 特徴ベクトル
	 * @param targetBorder ターゲットの値が変わる境界
	 * @param pw PrintWriter
	 * @throws IOException 例外
	 */
	public static void writeResultToFile(Map<String, double[]> featureVectors, int targetBorder, PrintWriter pw)
			throws IOException {

		int target;

		// 1ドキュメントごとのループ
		int docCount = 1;
		for (Map.Entry<String, double[]> featureVector : featureVectors.entrySet()) {
			if (docCount <= targetBorder) target = 1; // ドキュメントがtargetBorder個目より下であれば、targetを1に設定する
			else target = -1;

			pw.print(target + " ");

			// 1ワード(次元)ごとのループ
			int dimension = 1;
			for (double vectorValue : featureVector.getValue()) {
				if (vectorValue != 0.0) pw.print(dimension + ":" + vectorValue + " ");
				dimension++;
			}

			pw.println();

			docCount++;
		}
	}
}
