package mainBot;

// Password for CompetentBot: oauth:m86itpzls8ghsllo6xwd5zsem7ylqw
public class MainClass {
	static String botname = "CompetentBot";
	static String mainchannel = "#uncompetent";
	static String whisperchannel = "#_uncompetent_1452927513061";
	static String oauth = "oauth:m86itpzls8ghsllo6xwd5zsem7ylqw";
	static String pointname = "pantaloons";
	
	public static void main(String[] args){
		OverBot competentBot = new OverBot (botname,mainchannel,whisperchannel,oauth,pointname);
		
	}
	
}