package com.wordpress.mariaanko.voicemaster;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import android.app.Activity;
import android.speech.tts.TextToSpeech;

public class Translator {
	private String YandexKey = "trnsl.1.1.20130630T095040Z.585df14183944045.6bd3edcc178f35bfc1993ce148b1d397234247a3";
	private TextToSpeech mTextToSpeech = null;
	private int supported;

	public String TranslateText(String sourceText, String sourceLanguageCode,
			String targetLanguageCode) throws IOException {
		String request = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=";
		request = request + YandexKey + "&lang=" + sourceLanguageCode + "-"
				+ targetLanguageCode + "&text" + sourceText;
		URL url;
		url = new URL(request);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setConnectTimeout(60000);
		connection.setUseCaches(false);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String translatedString;
		while ((translatedString = in.readLine()) != null) {
			System.out.println(translatedString);
		}

		return null;

	}

	public String GetTextOfSpeech(String pathOfSpeechFile, String language)
			throws IOException {
		File file = new File(pathOfSpeechFile);
		if (!file.canRead()) {
			return null;
		}

		int fileLength = (int) file.length();
		byte[] speechData = new byte[fileLength];
		FileInputStream fis = new FileInputStream(file);
		fis.read(speechData);

		String request = "https://www.google.com/speech-api/v1/recognize?"
				+ "xjerr=1&client=speech2text&lang=" + language
				+ "&maxresults=10";

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

		System.out.println("Done");

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String decodedString;
		while ((decodedString = in.readLine()) != null) {
			System.out.println(decodedString);
		}

		connection.disconnect();
		return null;
	}

	public int SpeakText(String text, final Locale language,
			Activity currentContext) {

		mTextToSpeech = new TextToSpeech(currentContext,
				new TextToSpeech.OnInitListener() {

					@Override
					public void onInit(int status) {
						// TODO Auto-generated method stub
						supported = mTextToSpeech.setLanguage(language);
					}
				});

		if ((supported != TextToSpeech.LANG_AVAILABLE)
				&& (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
			return supported;
		}

		mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
		return 0;
	}

}
