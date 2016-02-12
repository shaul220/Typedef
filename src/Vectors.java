import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Vectors {

	private BufferedReader _reader;
	private Map<String, double[]> map;
	private ProgressBar progress;
	private double[] average;
	
	private final int DISTANCE_POWER = 2;
	
	public Vectors()
	{
		progress = new ProgressBar();
	}
	
	public Map<String, double[]> getMap()
	{
		return map;
	}
	
	public void mapWords(String[] baseWords, String[] topics)
	{
		String[] words = unite(baseWords, topics);
		
		map = new HashMap<String,double[]>();
		int found = 0;
		
		progress.setCurrentProcess("Fetching Vectors");
		
		try {
			_reader = new BufferedReader(new FileReader("/Users/Shaul/Desktop/ויצמן/Weizmann Project v5/src/vectors.txt"));
			String line = _reader.readLine();
			
			while((line = _reader.readLine()) != null && found < words.length)
			{
				for(int i = 0; i<words.length; i++)
				{
					if(line.substring(0, line.indexOf(' ')).compareTo(words[i]) == 0)
					{
						String stringVector = line.substring(line.indexOf(' ') + 1, line.length());
						double[] vector = parseDouble(stringVector);
						found++;
						map.put(words[i], vector);
						double precentage = ((double)found/words.length)*100;
						progress.showProgress((int)precentage);
						//System.out.print(words[i] + "\n");
						break;
					}
				}
			}
			if(found <words.length)
				progress.done();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		calculateAverage(baseWords);
	}
	
	public void calculateAverage(String[] words)
	{
		average = new double[300];
		
		for(int i = 0; i<300; i++)
		{
			for(int j = 0; j<words.length; j++)
			{
				average[i] += map.get(words[j])[i];
			}
			average[i] /= words.length;
		}
	}
	
	public double distanceFromAverage(double[] vector)
	{
		return cosineDistance(vector, average);
		//return euclideanDistance(vector, average, DISTANCE_POWER);
	}
	
	public double averageDistance(String word, String[] words)
	{
		double sum = 0;
		double[] vector = map.get(word);
		for(int i = 0; i<words.length; i++)
		{
			if(words[i].compareTo(word) != 0)
				sum += cosineDistance(vector, map.get(words[i]));
				//sum += euclideanDistance(vector, map.get(words[i]), DISTANCE_POWER);
		}
		return sum/(double)words.length;
	}
	
	public double cosineDistance(double[] vector1, double[] vector2)
	{
		double multiply = 0;
		double normalMultiply1 = 0;
		double normalMultiply2 = 0;
		for(int i = 0; i<vector1.length; i++)
		{
			multiply += vector1[i]*vector2[i];
			normalMultiply1 += vector1[i]*vector1[i];
			normalMultiply2 += vector2[i]*vector2[i];
		}
		return (multiply)/(Math.sqrt(normalMultiply1)*Math.sqrt(normalMultiply2));
	}
	
	public double euclideanDistance(double[] vector1, double[] vector2, int power)
	{
		double sum = 0;
		for(int i = 0; i<vector1.length; i++)
		{
			sum += Math.pow(vector1[i]-vector2[i], power);
		}
		return Math.pow(sum, 1/(double)power);
	}
	
	public static double[] parseDouble(String vector)
	{
		String[] tmp = vector.split(" ");
		double[] _vector = new double[300];
		
		for(int i = 0; i<300; i++)
		{
			_vector[i] = Double.parseDouble(tmp[i]);
		}
		
		return _vector;
	}
	
	public String[] unite(String[] baseWords, String[] topics)
	{
		String[] words = new String[baseWords.length + topics.length];
		for(int i = 0; i<baseWords.length; i++)
		{
			words[i] = baseWords[i];
		}
		for(int i = baseWords.length; i<words.length; i++)
		{
			words[i] = topics[i-baseWords.length];
		}
		return words;
	}

	public double maxDistance(double[] vector, String[] words) 
	{
		double max = cosineDistance(vector, map.get(words[0]));
		int idx = 0;
		for(int i = 1; i<words.length; i++)
		{
			double curr = cosineDistance(vector, map.get(words[i]));
			if(curr < max)
			{
				idx = i;
				max = curr;
			}
		}
		return max;
	}
}
