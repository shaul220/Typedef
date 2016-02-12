import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Subject {

	private Wikipedia wikipedia;
	private Vectors vectors;
	
	private final int TOPICS_AMOUNT = 10;
	
	private double freqWeight;
	private double vectWeight;
	
	private Map<String, double[]> map;
	
	private String[] words;
	
	public Subject(String[] words)
	{
		wikipedia = new Wikipedia();
		vectors = new Vectors();
		map = new HashMap<String,double[]>();
		
		this.words = words;
		
		ArrayList<String> topicsList = wikipedia.getFrequentWords(words, TOPICS_AMOUNT);
		String[] topics = new String[topicsList.size()];
		topicsList.toArray(topics);
		
		/*
		for(int i = 0; i < topics.length; i++)
		{
			System.out.print(topics[i] +  "\t" + wikipedia.getMap().get(topics[i]) + "\n");
		}
		*/
		
		vectors.mapWords(words, topics);
		
		mapWords(words);
	}
	
	public void mapWords(String[] words)
	{
		Map<String, Integer> wikiMap = wikipedia.getMap();
		Map<String, double[]> vectMap = vectors.getMap();
		for(Entry<String, Integer> entry:wikiMap.entrySet())
		{
			String word = entry.getKey();
			double freq = entry.getValue();
			
			if(vectMap.get(word) != null)
			{
				double distance = vectors.distanceFromAverage(vectMap.get(word));
				//double distance = vectors.averageDistance(word, words);
				double maxDistance = vectors.maxDistance(vectMap.get(word), words);
				double distribution = getDistribution(word);
				double[] values = {freq, distance, maxDistance, distribution};
				map.put(word, values);
			}
		}
		
		calculateWeights();
	}
	
	public int getDistribution(String word)
	{
		double counter = 0;
		String[][] freqWords = wikipedia.getDocFreqWords();
		for(int i = 0; i<freqWords.length; i++)
		{
			if(wikipedia.contains(freqWords[i], word))
			{
				counter++;
			}
		}
		double precent = (counter/freqWords.length)*100;
		return (int)precent;
	}
	
	public void calculateWeights()
	{
		freqWeight = calculateFreqWeight();
		vectWeight = calculateVectWeight();	
	}
	
	public double calculateVectWeight()
	{
		double average = averageDistance();
		double sum = 0;
		int counter = 0;
		
		for(Entry<String, double[]> entry:map.entrySet())
		{
			sum += Math.pow((entry.getValue()[1]-average), 2);
			counter++;
		}
		
		sum *= (1/((double)counter-1));
		return Math.sqrt(sum);
	}
	
	public double calculateFreqWeight()
	{
		double average = averageFrequency();
		double sum = 0;
		int counter = 0;
		
		for(Entry<String, double[]> entry:map.entrySet())
		{
			sum += Math.pow((entry.getValue()[0]-average), 2);
			counter++;
		}
		
		sum *= (1/((double)counter-1));
		return Math.sqrt(sum);
	}
	
	public double averageDistance() 
	{
		double average = 0;
		int counter = 0;
		for(Entry<String, double[]> entry:map.entrySet())
		{
			average += entry.getValue()[1];
			counter++;
		}
		return average/counter;
	}
	
	public double averageFrequency()
	{
		double average = 0;
		int counter = 0;
		for(Entry<String, double[]> entry:map.entrySet())
		{
			average += entry.getValue()[0];
			counter++;
		}
		return average/counter;
	}
	
	public void log()
	{
		for(Entry<String, double[]> entry:map.entrySet())
		{
			if(entry.getValue()[0] > 0){
				String word = entry.getKey();
				double freq = entry.getValue()[0];
				double dist = entry.getValue()[1];
				double maxDist = entry.getValue()[2];
				double distribution = entry.getValue()[3];
				System.out.print(word + "\t" + freq + "\t" + dist + "\t" + maxDist + "\t" + (int)distribution + "%\n");
			}
				
		}
	}
	
}
