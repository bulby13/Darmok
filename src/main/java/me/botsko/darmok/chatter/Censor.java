package me.botsko.darmok.chatter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Censor {
	
	/**
	 * 
	 */
	protected HashMap<String,String> leet = new HashMap<String,String>();
	
	/**
	 * 
	 */
	private final List<String> rejectWords;
	
	/**
	 * 
	 */
	private final List<String> censorWords;
	
	
	/**
	 * 
	 * @param plugin
	 * @throws SQLException 
	 */
	public Censor( List<String> rejectWords, List<String> censorWords ){
		
		this.rejectWords = rejectWords;
		this.censorWords = censorWords;
		
		// Build some basic "leetspeak" conversion
		leet.put("1", "l");
		leet.put("1", "i");
		leet.put("2", "z");
		leet.put("2", "r");
		leet.put("3", "e");
		leet.put("4", "h");
		leet.put("4", "a");
		leet.put("5", "s");
		leet.put("6", "g");
		leet.put("7", "l");
		leet.put("8", "a");
		leet.put("9", "p");
		leet.put("9", "g");
		leet.put("0", "o");
		leet.put("13", "b");
		leet.put("44", "m");
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public boolean isFakeCensor( String msg, String fake ){
		return msg.contains(fake);
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String filterCaps( String msg, int minLength, int capsPercent ){
		if( msg.length() < minLength ){
			return msg;
		}
		if( capsPercentage(msg) > capsPercent ){
			return msg.toLowerCase();
		}
		return msg;
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	protected double capsPercentage(String msg){
		int count = 0;
		for (char ch : msg.toCharArray()){
			if (Character.isUpperCase(ch)){
				count++;
			}
		}
		return (count > 0 ? (( (double)count / (double)msg.length()) * 100) : 100);
	}

	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	protected boolean containsSuspectedProfanity( String msg ){
		
		// ensure lower case
		String _tmp = msg.toLowerCase();
		
		// replace all invalid characters
		_tmp = _tmp.replaceAll("[^a-z0-9]", "");
		
		// get possible leet versions
		List<String> variations = convertLeetSpeak(_tmp);
		
		for(String variation : variations){
			// scan for illegal words
			for(String w : rejectWords){
				if(variation.matches("\b"+w+"\b")){
					return true;
				}
			}
		}
	
		return false;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String replaceCensoredWords( String msg ){
		for(String w : censorWords){
			msg = msg.replaceAll("(?i)\b"+w+"\b", "*****");
		}
		return msg;
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	protected List<String> convertLeetSpeak( String msg ){
		
		// Begin list of all variations, including original
		List<String> _variations = new ArrayList<String>();
		_variations.add(msg);
		
		for (Entry<String, String> entry : leet.entrySet()){
			if(msg.contains( entry.getKey() )){
				// entry.getValue()
				_variations.add( msg.replace(entry.getKey(), entry.getValue()) );
			}
		}
		
		return _variations;
		
	}
}
