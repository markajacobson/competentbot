package mainBot;

import org.jibble.pircbot.*;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.System.*;
import java.util.Properties;
import java.io.*;

public class OverBot {
	OverBot.ChannelBot chanbot;
	OverBot.WhisperBot whispbot;
	StreamChecker streamcheck;
	String name;
	String mainChan;
	String whisperChan;
	String pointsName;
	Properties users;
	OutputStream output;
	InputStream input;
	PointTimer minutetimer;
	int pointsperminute = 1;
	
	class PointTimer extends TimerTask{
	
		
		public PointTimer(){
		}
		
		public void run() {
			System.out.println("Giving points to all viewers");
			if (OverBot.this.checkLive()){
				System.out.println("Channel Live. Giving full points");
				for (User u : chanbot.getUsers(mainChan)){
					System.out.print(u.getNick().concat(" "));
					int userpoints = Integer.parseInt(users.getProperty(u.getNick().concat(".points"), "0"));
					users.setProperty(u.getNick().concat(".points"), String.valueOf(userpoints + 6*pointsperminute));
					double userhours = Double.parseDouble(users.getProperty(u.getNick().concat(".hours"), "0"));
					users.setProperty(u.getNick().concat(".hours"), String.valueOf(userhours + (1.0/10)));
					double userhealth = Double.parseDouble(users.getProperty(u.getNick().concat(".health"), "-999.0"));
					if (!(userhealth == -999.0)){
						if (userhealth > 90){
							userhealth = 90;
						}
						users.setProperty(u.getNick().concat(".health"), String.valueOf(userhealth + 10.0));
					}
				}
				System.out.println(" ");
				
			}
			else {
				System.out.println("Channel offline. Giving fewer points.");
				for (User u : chanbot.getUsers(mainChan)){
					System.out.print(u.getNick().concat(" "));
					int userpoints = Integer.parseInt(users.getProperty(u.getNick().concat(".points"), "0"));
					users.setProperty(u.getNick().concat(".points"), String.valueOf(userpoints + pointsperminute));
					double userhealth = Double.parseDouble(users.getProperty(u.getNick().concat(".health"), "-999.0"));
					if (!(userhealth == -999.0)){
						if (userhealth > 99.5){
							userhealth = 99.5;
						}
						users.setProperty(u.getNick().concat(".health"), String.valueOf(userhealth + 0.5));
					}
				}
				System.out.println(" ");
			}
			OverBot.this.saveUsers();
		}
	}

//PointTimer code ends here
	
	class ChannelBot extends PircBot {
		String myChan;
		
		public ChannelBot(String name, String chan) {
	        this.setName(name);
	        this.myChan = chan;
	    }
	    
	    public void onUnknown(String line){
	    	if (line.contains(" PRIVMSG ") == true) {
	    		OverBot.this.handleMessage(line, false);
	    	}
	    }
	    
	}
	
//ChannelBot code ends here
	
	class WhisperBot extends PircBot {
	    String myChan;
	    
	    public WhisperBot(String name, String chan) {
	        this.setName(name);
	        this.myChan = chan;
	    }
	    
	    public void onUnknown(String line){
	    	if (line.contains(".tmi.twitch.tv WHISPER ") == true) {
	    		System.out.println("Whisper recieved");
	    		OverBot.this.handleMessage(line, true);
	    	} 
	    	else if (line.contains(" PRIVMSG ") == true) {
	    		OverBot.this.handleMessage(line, false);
	    	}
	    	
	    }
	}

//WhisperBot code ends here	
	
	//Functions for OverBot
	public void handleMessage(String line, boolean whispered){
		String msgChannel = "";
		String senderName = "";
		String senderMessage = "";
		boolean senderIsMod = false;
		System.out.println("Got me a message");
		if (whispered){
			System.out.println("It was whispered");
			int nameStart = line.indexOf("display-name=") +13; 
			int nameEnd = line.indexOf(";", nameStart);
			senderName = line.substring(nameStart,nameEnd); //Get the username of the whisperer
			int messageStart = line.indexOf("WHISPER ") + 10 + name.length() ;
			senderMessage = line.substring(messageStart, line.length()); //Get the message sent in a whisper
			senderMessage = senderMessage.trim();
		}
		else {
			int nameStart = line.indexOf("display-name=") +13;
			int nameEnd = line.indexOf(";", nameStart);
			senderName = line.substring(nameStart,nameEnd); //Get the username of the poster
			int messageStart = line.indexOf("PRIVMSG ") + 10 + name.length() ;
			senderMessage = line.substring(messageStart, line.length()); //Get the message posted in channel
			senderMessage = senderMessage.trim();
			int modstart = line.indexOf("mod=")+4;
			if (line.substring(modstart,modstart+1).equals("1") || senderName.equalsIgnoreCase(mainChan.substring(1))){
				senderIsMod = true;
			}
		}
		if (whispered == true || senderMessage.startsWith("!")){
			String command1[];
			if (whispered){
				command1 = senderMessage.split(" ", 2);
			}
			else {
				command1 = senderMessage.substring(1).split(" ", 2);
			}
			System.out.println(command1[0]);
			switch (command1[0].toLowerCase()){
				case "resetusers":
					if (senderIsMod){
						System.out.println("Creating user file");
						createUserFile();
					}
					break;
					
				case "getusers":
					if (senderIsMod) {
						System.out.println("Printing user list");
						printUserList();
					}
					break;
				
				case "givepoints":
					if (senderIsMod) {
						System.out.println("Giving points");
						minutetimer.run();
					}
					break;
				
				default:
					break;
			}
		}
		
	}
	
	
	public boolean checkLive(){
		return streamcheck.amILive();
	}
	
	public boolean checkLive(String checkchan){
		return streamcheck.isThisGuyLive(checkchan);
	}
	
	public void post(String message){ //Channelbot will post to the main channel
		this.chanbot.sendMessage(mainChan,message);
	}
	
	public void whisper(String message, String target){
		this.whispbot.sendMessage(whisperChan, "/w ".concat(target).concat(" ").concat(message));
	}
	
	public void loadUsers(){
		input = null;

		try {

			input = new FileInputStream(mainChan.concat(".users"));
			users.load(input);

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	public void saveUsers(){
		output = null;
		try {

			output = new FileOutputStream(mainChan.concat(".users"));
			users.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
	
	public void createUserFile(){
		output = null;
		try {

			output = new FileOutputStream(mainChan.concat(".users"));
			users = new Properties();
			users.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
	
	public void printUserList(){
		for (User u : chanbot.getUsers(mainChan)){
			System.out.println(u.getNick());
		}
	}
	
	public OverBot(String botname, String channel, String whisperchannel, String oauth, String point) {
		this.name = botname;
		this.mainChan = channel;
		this.whisperChan = whisperchannel;
		this.pointsName = point;
		this.users = new Properties();
		this.input = null;
		this.minutetimer = new PointTimer();
		
		File f = new File(mainChan.concat(".users"));
		if(f.exists() && !f.isDirectory()) { 
		    loadUsers();
		}
		else {
			createUserFile();
		}
		f = null;
		
	    this.chanbot = new OverBot.ChannelBot(name, mainChan);
		try{
			chanbot.setVerbose(true);
			chanbot.connect("irc.twitch.tv", 6667, oauth);
			chanbot.joinChannel(mainChan);
			chanbot.sendRawLine("CAP REQ :twitch.tv/commands");
			chanbot.sendRawLine("CAP REQ :twitch.tv/tags");
			chanbot.sendRawLine("CAP REQ :twitch.tv/membership");
			System.out.println("ChannelBot Connected!");
		}
		catch (Exception e){e.printStackTrace();}


	    this.whispbot = new OverBot.WhisperBot(name, whisperChan);
	    try{
			whispbot.setVerbose(true);
			whispbot.connect("192.16.64.212", 443, "oauth:m86itpzls8ghsllo6xwd5zsem7ylqw");
			whispbot.joinChannel(whisperChan);
			whispbot.sendRawLine("CAP REQ :twitch.tv/commands");
			whispbot.sendRawLine("CAP REQ :twitch.tv/tags");
			whispbot.sendRawLine("CAP REQ :twitch.tv/membership");
			System.out.println("WhisperBot Connected!");
		}
		catch (Exception e){
			e.printStackTrace();
			System.out.println("WhisperBot failed to connect: Whispered commands will not be functional.");
		}
	    //whispbot.sendMessage(whisperChan, name.concat(" connected."));
	    
	    streamcheck = new StreamChecker(mainChan);
	    Timer sixminutes = new Timer();
	    sixminutes.schedule(minutetimer, 0, 60000);
	}
}