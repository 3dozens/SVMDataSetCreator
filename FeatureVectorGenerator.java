import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.reduls.igo.Morpheme;
import net.reduls.igo.Tagger;

public class FeatureVectorGenerator {

	public Map<String, double[]> generateTFIDFVectors(List<String> documents) {

		// List<word>
		List<String> words = new ArrayList<String>();

		// Map<document, Map<word, tf>>
		Map<String, Map<String, Integer>> tfMap = new HashMap<String, Map<String, Integer>>();

		// Map<word, df>
		Map<String, Integer> dfMap = new HashMap<String, Integer>();

		for (String document : documents) {

			List<String> parseResult = parse(document);

			Map<String, Integer> docTFMap = new HashMap<String, Integer>();
			for (String word : parseResult) {

				// �P�ꖈ��TF���v�Z
				if (docTFMap.containsKey(word)) {
					int tf = docTFMap.get(word);
					docTFMap.put(word, tf + 1);
				} else {
					docTFMap.put(word, 1);
				}

				// �P��ꗗ�ɒP���ǉ�
				if (!words.contains(word)) {
					words.add(word);
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

		Map<String, double[]> featureVectors = new HashMap<String, double[]>();
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
			double[] featureVector = new double[words.size()];
			for (int i = 0; i < words.size(); i++) {
				String word = words.get(i);
				if (docTFIDFMap.containsKey(word)) {
					featureVector[i] = docTFIDFMap.get(word);
				} else {
					featureVector[i] = 0;
				}
			}
			featureVectors.put(document, featureVector);
		}

		// �P��ꗗ��\�� comment outed by Hayasaka
		System.out.println("=== word list ===");
		System.out.print("(");
		for (int i = 0; i < words.size(); i++) {
			System.out.print((i + 1) + ":" + words.get(i));
			if (i != words.size() - 1) {
				System.out.print(", ");
			}
		}
		System.out.println(")");
		System.out.println(""); 

		return featureVectors;
	}

	private List<String> parse(String str) {

		// ��͊�̐���
		Tagger tagger = null;
		try {
			tagger = new Tagger("ipadic");
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Morpheme> list = tagger.parse(str);

		List<String> result = new ArrayList<String>();
		for (Morpheme morpheme : list) {
			result.add(morpheme.surface);
		}

		return result;
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
		
		double result = 0.0;
		for(double value : vector) {
			result += Math.pow(value, 2);
		}
		result = Math.sqrt(result);
		
		return result;
	}
}
