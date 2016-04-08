package mainBot;

import org.jibble.pircbot.*;

public class MyBot extends PircBot {
    String wChan = "#_uncompetent_1449767316352";
    public MyBot(String name) {
        this.setName(name);
    }
    
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
    	if (message.equalsIgnoreCase("fuck")){
    		sendMessage("#uncompetent", "No, fuck you");
    	}
    }
    public void onUnknown(String line){
    	if (line.contains(".tmi.twitch.tv WHISPER competentbot ") == true) {
    		
    		int nameStart = line.indexOf("display-name=") +13; 
    		int nameEnd = line.indexOf(";", nameStart);
    		String senderName = line.substring(nameStart,nameEnd); //Get the username of the whisperer
    		int messageStart = line.indexOf("WHISPER competentbot :") + 22;
    		String senderMessage = line.substring(messageStart, line.length()); //Get the message sent in a whisper
    		
    		sendMessage(wChan, senderName.concat(": \"").concat(senderMessage).concat("\"")); //Post the sender and message in mod-only channel
    		//sendMessage(wChan, "/w ".concat(senderName).concat(" Thank you for helping me test this bot ").concat(senderName)); //respond to whisper
    	}
    }
}