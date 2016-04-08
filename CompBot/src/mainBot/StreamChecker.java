package mainBot;

import java.net.*;
import java.io.*;

public class StreamChecker {
	URL channel;
	
	public StreamChecker(String chan) {
		try {
			channel = new URL("https://api.twitch.tv/kraken/streams/".concat(chan.substring(1)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(channel);
	}
	public boolean amILive(){
		try{
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(channel.openStream()));
			String inputLine;
	        while ((inputLine = in.readLine()) != null)
	        	if (inputLine.contains("\"stream\":null")){
	        		in.close();
	        		return false;
	        	} else if (inputLine.contains("\"stream\":{\"_id\"")){
	        		in.close();
	        		return true;
	        	}
	        in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean isThisGuyLive(String otherchannel){
		try{
			URL theirchannel = new URL("https://api.twitch.tv/kraken/streams/".concat(otherchannel));
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(theirchannel.openStream()));
			String inputLine;
	        while ((inputLine = in.readLine()) != null)
	        	if (inputLine.contains("\"stream\":null")){
	        		in.close();
	        		return false;
	        	} else if (inputLine.contains("\"stream\":{\"_id\"")){
	        		in.close();
	        		return true;
	        	}
	        in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
