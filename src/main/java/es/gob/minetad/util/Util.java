package es.gob.minetad.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Util {
	
	public static String getVectorString(List<Double> shape) {
		String strShape=null;
		for (Double d:shape) {
			if (strShape==null) {
				strShape=d+"";
			}else {
			strShape+=","+d;
			}
		}
		return strShape;
	}

	public static List<Double> getVectorFromString(String topic_vector) {

		String[] topics = topic_vector.split(",");
		Double[] vector = new Double[topics.length];
		for (int i = 0; i < topics.length; i++) {
			vector[i] = Double.parseDouble(topics[i]);
		}
		return Arrays.asList(vector);
	}
  
	public static List<Double> getVectorFromString(String topic_vector, float multiplication_factor, int size,
			float epsylon) {

		String[] topics = topic_vector.split(" ");
		Double[] vector = new Double[size];
		Arrays.fill(vector, ((double) 1 / size));
		for (int i = 0; i < topics.length; i++) {
			String idd = topics[i].substring(topics[i].lastIndexOf("_") + 1, topics[i].indexOf("|"));
			int id = Integer.valueOf(idd);
			int freq = Integer.valueOf(StringUtils.substringAfter(topics[i], "|"));
			Double score = Double.valueOf(freq) / Double.valueOf(multiplication_factor);
			vector[id] = score;
		}
		return Arrays.asList(vector);
	}

	public static List<Double> cleanZerosDocTopicVector(List<Double> docTopicValues) {
		// List<Double> docTopicVector = new ArrayList<>(numTopics);

		int numTopics = docTopicValues.size();
		Double[] docTopicVector = new Double[numTopics];
		// find zero
		double min = 1d;
		for (int i = 0; i < numTopics; i++) {
			if (docTopicValues.get(i) < min) {
				min = docTopicValues.get(i);
			}
		}

		if (min > 0.01d || min == 0d) {
			return docTopicValues;
		}

		// clean zero and get rest
		int num_zeros = 0;
		for (int i = 0; i < numTopics; i++) {
			if (docTopicValues.get(i) > min) {
				docTopicVector[i] = docTopicValues.get(i);
			} else {
				docTopicVector[i] = 0d;
				num_zeros++;
			}
		}

		// complete rest
		float rest = 1;
		for (int i = 0; i < numTopics; i++) {
			rest -= docTopicVector[i];
		}
		rest = rest / (float) (numTopics - num_zeros);
		for (int i = 0; i < numTopics; i++) {
			if (docTopicValues.get(i) > min) {
				docTopicVector[i] = docTopicVector[i] + rest;
			}
		}

		return Arrays.asList(docTopicVector);
	}

	public static String getQuery(String vectorString, int norma) {
		String queryString = "";
		String queryStringBoost = "";
		String[] str = vectorString.split(" ");
		for (int i = 0; i < str.length; i++) {
			queryString += "sumTotalTermFreq(listaBO:" + str[i] + ") ";
			queryStringBoost += "listaBO:" + str[i].split("\\|")[0] + "^"
					+ (norma - Integer.parseInt(str[i].split("\\|")[1])) + " ";
		}
		return queryString.trim() + " " + queryStringBoost;
	}

	public static String getVectorString(List<Double> topic_vector, String prefix) {
		String result = "";
		float multiplication_factor = Double.valueOf(1 * Math.pow(10, String.valueOf(topic_vector.size()).length() + 1))
				.floatValue();
		;
		for (int i = 0; i < topic_vector.size(); i++) {
			if ((int) (topic_vector.get(i) * multiplication_factor) > 0) {
				result += prefix + "_" + i + "|" + (int) (topic_vector.get(i) * multiplication_factor) + " ";
			}
		}
		return result;
	}

	public static Double[] getVectorFromText(String textQuery, String model) {

		String url = "http://librairy.linkeddata.es/" + model + "/shape";
		String json = "{\"text\":\"" + textQuery + "\"}";
		Double[] vector = null;
		String respuesta = null;
		//System.out.println(url);
		//System.out.println(json);
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			con.setRequestMethod("GET");
			OutputStream os = con.getOutputStream();
			os.write(json.getBytes("UTF-8"));
			os.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			String res = response.toString();
			respuesta = res.substring(res.indexOf("[") + 1, res.indexOf("]"));
			String[] vectorS = respuesta.split(",");
			vector = new Double[vectorS.length];
			for (int i = 0; i < vectorS.length; i++) {
				vector[i] = Double.parseDouble(vectorS[i]);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return vector;

	}

	

}
