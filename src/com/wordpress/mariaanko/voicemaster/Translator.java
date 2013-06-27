package com.wordpress.mariaanko.voicemaster;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Translator {
	public String GetTextOfSpeech(String pathOfSpeechFile, String language) throws IOException
	{	    
	    File file = new File(pathOfSpeechFile);
	    if (!file.canRead())
	    {
	    	return null;
	    }
	    
	    int fileLength = (int)file.length();
	    byte [] speechData = new byte[fileLength];
	    FileInputStream fis = new FileInputStream(file);
	    fis.read(speechData);
	    
		String request = "https://www.google.com/"+
                "speech-api/v1/recognize?"+
                "xjerr=1&client=speech2text&lang=" + language + "&maxresults=10";
		
		URL url;
		url = new URL(request);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"audio/x-flac; rate=16000");
		connection.setRequestProperty("User-Agent", "speech2text");
		connection.setConnectTimeout(60000);
		connection.setUseCaches(false);
	       
		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.write(speechData);
		wr.flush();
		wr.close();
		connection.disconnect();

		System.out.println("Done");

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String decodedString;
		while ((decodedString = in.readLine()) != null) {
			System.out.println(decodedString);
		}
		return null;
	} 

}
