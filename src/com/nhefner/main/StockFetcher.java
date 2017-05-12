package com.nhefner.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.ConfigurationException;

import com.fafe.core.properties.ConfigLoader;
import com.fafe.core.properties.ConfigProperty;

public class StockFetcher {

	private final String outputPath;
	private final String stockMarket;
	private final String symbolFile;
	private final ConfigLoader cl;

	public StockFetcher(String stockMarket) throws ConfigurationException {
		cl = new ConfigLoader();
		this.outputPath = (String) cl.getProperty(ConfigProperty.DATA_PATH) + stockMarket + "Yahoo//";
		this.stockMarket = stockMarket.toUpperCase();
		this.symbolFile = cl.getProperty(ConfigProperty.SYMBOL_PATH) + stockMarket + ".txt";
	}

	/*
	 * Returns a Stock Object that contains info about a specified stock.
	 * 
	 * @param symbol the company's stock symbol
	 * 
	 * @return a stock object containing info about the company's stock
	 * 
	 * @see Stock
	 */
	void getStock(String symbol) {
		String sym = symbol.toUpperCase();
		try {

			// Retrieve CSV File
			// URL yahoo = new URL("http://finance.yahoo.com/d/quotes.csv?s="+
			// symbol + "&f=l1vr2ejkghm3j3nc4s7pox");
			// String yahooUrl =
			// "http://real-chart.finance.yahoo.com/table.csv?s=FTSEMIB.MI&a=00&b=1&c=2000&d=08&e=8&f=2016&g=d&ignore=.csv";
			String yahooUrl;
			//if (stockMarket.equals("NYSE")) {
				yahooUrl = "https://chart.finance.yahoo.com/table.csv?s=" + sym;
			//} else {
			//	yahooUrl = "https://chart.finance.yahoo.com/table.csv?s=" + sym + "." + stockMarket;
			//}
			System.out.println(yahooUrl);
			URL yahoo = new URL(yahooUrl);
			URLConnection connection = yahoo.openConnection();
			InputStreamReader is = new InputStreamReader(connection.getInputStream());

			// File file = new File("YAHOO//" + sym + ".csv");
			File file = new File(outputPath + File.separator + sym + ".csv");

			FileWriter fw = new FileWriter(file);

			BufferedReader br = new BufferedReader(is);
			String line = null;
			while (true) {
				line = br.readLine();
				if (line == null)
					break;
				fw.write(line + "\r\n");
			}

			fw.close();
			is.close();
			// Parse CSV Into Array
			// Only split on commas that aren't in quotes
			// String[] stockinfo =
			// line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

			// Handle Our Data
			/*
			 * StockHelper sh = new StockHelper();
			 * 
			 * price = sh.handleDouble(stockinfo[0]); volume =
			 * sh.handleInt(stockinfo[1]); pe = sh.handleDouble(stockinfo[2]);
			 * eps = sh.handleDouble(stockinfo[3]); week52low =
			 * sh.handleDouble(stockinfo[4]); week52high =
			 * sh.handleDouble(stockinfo[5]); daylow =
			 * sh.handleDouble(stockinfo[6]); dayhigh =
			 * sh.handleDouble(stockinfo[7]); movingav50day =
			 * sh.handleDouble(stockinfo[8]); marketcap =
			 * sh.handleDouble(stockinfo[9]); name = stockinfo[10].replace("\"",
			 * ""); currency = stockinfo[11].replace("\"", ""); shortRatio =
			 * sh.handleDouble(stockinfo[12]); previousClose =
			 * sh.handleDouble(stockinfo[13]); open =
			 * sh.handleDouble(stockinfo[14]); exchange =
			 * stockinfo[15].replace("\"", "");
			 */
		} catch (IOException e) {
			Logger log = Logger.getLogger(StockFetcher.class.getName());
			log.log(Level.SEVERE, e.toString(), e);
			// return null;
		}

		// return new Stock(sym, price, volume, pe, eps, week52low, week52high,
		// daylow, dayhigh, movingav50day, marketcap, name,currency,
		// shortRatio,previousClose,open,exchange);

	}

	public void validateNewData(Date currentDate, List<String> upToDateSymbolList, List<String> outOfDateSymbolList)
			throws Exception {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(symbolFile)));
			String symbol = null;
			while ((symbol = br.readLine()) != null) {
				if (checkCurrentDate(symbol, currentDate)) {
					upToDateSymbolList.add(symbol);
				} else {
					outOfDateSymbolList.add(symbol);
				}
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}

	public static SimpleDateFormat sdfyyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");

	private boolean checkCurrentDate(String symbol, Date currentDate) throws Exception {
		boolean currentDateFound = false;
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new FileReader(new File(outputPath + File.separator + symbol.toUpperCase() + ".csv")));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.contains(sdfyyyyMMdd.format(currentDate))) {
					currentDateFound = true;
					break;
				}
			}
			return currentDateFound;
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}

	}

	public void getYahooData() throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(symbolFile)));
			String symbol = null;
			while ((symbol = br.readLine()) != null) {
				getStock(symbol);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null) {
				br.close();
			}
		}
		// sf.getStock("FTSEMIB");
		// sf.getStock("A2A");
		// sf.getStock("ATL");
		// sf.getStock("AZM");
		// sf.getStock("BMED");
		// sf.getStock("BMPS");
		// sf.getStock("BPE");
		// sf.getStock("PMI");
		// sf.getStock("BP");
		// sf.getStock("BRE");
		// sf.getStock("BZU");
		// sf.getStock("CPR");
		// sf.getStock("CNHI");
		// sf.getStock("ENEL");
		// sf.getStock("ENI");
		// sf.getStock("EXO");
		// sf.getStock("RACE");
		// sf.getStock("FCA");
		// sf.getStock("FBK");
		// sf.getStock("G");
		// sf.getStock("ISP");
		// // sf.getStock("IT");
		// sf.getStock("LDO");
		// sf.getStock("LUX");
		// sf.getStock("MS");
		// sf.getStock("MB");
		// sf.getStock("MONC");
		// sf.getStock("PST");
		// sf.getStock("PRY");
		// sf.getStock("REC");
		// sf.getStock("SPM");
		// sf.getStock("SFER");
		// sf.getStock("SRG");
		// sf.getStock("STM");
		// sf.getStock("TIT");
		// sf.getStock("TEN");
		// sf.getStock("TRN");
		// sf.getStock("UBI");
		// sf.getStock("UCG");
		// sf.getStock("UNI");
		// sf.getStock("US");
		// sf.getStock("YNAP");

	}

	public static void main(String[] args) {
		StockFetcher sf;
		try {
			sf = new StockFetcher("PA");
			sf.getYahooData();
			List<String> upToDateSymbolList = new LinkedList<String>();
			List<String> outOfDateSymbolList = new LinkedList<String>();
			sf.validateNewData(new Date(), upToDateSymbolList, outOfDateSymbolList);
			for(String stock : outOfDateSymbolList){
				System.out.println(stock);
			}
		} catch (ConfigurationException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
