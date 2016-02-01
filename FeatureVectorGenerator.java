import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.reduls.igo.Morpheme;
import net.reduls.igo.Tagger;

public class FeatureVectorGenerator {

	public Map<String, double[]> generateTFIDFVectors(List<String> documents) {

		// List<word>
		List<String> wordList = new ArrayList<String>();

		// Map<document, Map<word, tf>>
		Map<String, Map<String, Integer>> tfMap = new HashMap<>();

		// Map<word, df>
		Map<String, Integer> dfMap = new HashMap<>();

		for (String document : documents) {

			List<String> parsedWords = parse(document);

			Map<String, Integer> docTFMap = new HashMap<>();
			for (String word : parsedWords) {

				// �P�ꖈ��TF���v�Z
				if (docTFMap.containsKey(word)) {
					int tf = docTFMap.get(word);
					docTFMap.put(word, tf + 1);
				} else {
					docTFMap.put(word, 1);
				}

				// �P��ꗗ�ɒP���ǉ�
				if (!wordList.contains(word)) {
					wordList.add(word);
				}
			}

			tfMap.put(document, docTFMap);

			// DF���v�Z
			for (String word : docTFMap.keySet()) {
				if (dfMap.containsKey(word)) {
					int df = dfMap.get(word);
					dfMap.put(word, df + 1);
				} else {
					dfMap.put(word, 1);
				}
			}
		}

		Map<String, double[]> featureVectors = new LinkedHashMap<String, double[]>();
		for (String document : documents) {

			// Map<word, tfidf>
			Map<String, Double> docTFIDFMap = new HashMap<String, Double>();

			// �P�ꖈ��TF-IDF�����߂�
			Map<String, Integer> docTFMap = tfMap.get(document);
			for (String word : docTFMap.keySet()) {

				double tf = (double) docTFMap.get(word);
				double df = (double) dfMap.get(word);
				double N = (double) documents.size();

				double tfidf = tf * Math.log(N / df);

				docTFIDFMap.put(word, tfidf);
			}

			// �����x�N�g���𐶐�
			double[] featureVector = new double[wordList.size()];
			for (int i = 0; i < wordList.size(); i++) {
				String word = wordList.get(i);
				if (docTFIDFMap.containsKey(word)) {
					featureVector[i] = docTFIDFMap.get(word);
				} else {
					featureVector[i] = 0;
				}
			}
			featureVectors.put(document, featureVector);
		}

		// �P��ꗗ��\�� comment outed by Hayasaka
//		System.out.println("=== word list ===");
//		System.out.print("(");
//		for (int i = 0; i < words.size(); i++) {
//			System.out.print((i + 1) + ":" + words.get(i));
//			if (i != words.size() - 1) {
//				System.out.print(", ");
//			}
//		}
//		System.out.println(")");
//		System.out.println(""); 

		return featureVectors;
	}

	/**
	 * 渡された文字列を形態素解析し、副詞、助詞、助動詞、数でない
	 * 単語のリストを返します
	 * @param str 対象の文字列
	 * @return 単語リスト
	 */
	private List<String> parse(String str) {

		Tagger tagger = null;
		try {
			tagger = new Tagger("ipadic");
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Morpheme> morphemes = tagger.parse(str);
		List<String> words = morphemes.stream()
			.filter(m -> !m.feature.contains("副詞") && !m.feature.contains("助詞") 
				&& !m.feature.contains("助動詞") && !m.feature.contains("数"))
			.map(m -> m.surface)
			.collect(Collectors.toList());

		return words;
	}
	
	public double calcCosineSimilarity(double[] vector1, double[] vector2) {
		
		return calcInnerProduct(vector1, vector2) / 
				(calcMagnitude(vector1) * calcMagnitude(vector2));
	}
	
	private double calcInnerProduct(double[] vector1, double[] vector2) {
		
		double result = 0.0;
		for(int i = 0; i < vector1.length; i++) {
			result += vector1[i] * vector2[i];
		}
		
		return result;
	}
	
	/**
	 * ベクトルの大きさを求めます
	 */
	private double calcMagnitude(double[] vector) {
		
		double magnitudeBeforeSqrt = Arrays.stream(vector)
			.map(v -> Math.pow(v, 2))
			.sum();
		
		double magnitude = Math.sqrt(magnitudeBeforeSqrt);
		
		return magnitude;
	}
}
