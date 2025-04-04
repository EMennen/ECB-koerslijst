import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
// import java.io.File;
import java.io.FileWriter;
// import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

/* Deze Java-class leest de XML, haalt de wisselkoersen op, 
    en schrijft ze naar een CSV-bestand met de kolommen Date, Currency, en Rate. */

public class XMLToCSVConverter {
    public static void main(String[] args) {
        // String xmlFilePath = "C:\\temp\\eurofxref-daily.xml";
        String csvFilePath = "C:\\temp\\exchange_rates.csv";
        String xmlString = readECB();
        try {
            // File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xmlString));
            Document document = builder.parse(is);
            
            NodeList cubes = document.getElementsByTagName("Cube");
            if (cubes.getLength() > 1) {
                Element dateCube = (Element) cubes.item(1);
                String date = dateCube.getAttribute("time");
                
                FileWriter csvWriter = new FileWriter(csvFilePath);
                csvWriter.append("Date,Currency,Rate\n");
                
                NodeList rates = dateCube.getElementsByTagName("Cube");
                for (int i = 0; i < rates.getLength(); i++) {
                    Element rateElement = (Element) rates.item(i);
                    String currency = rateElement.getAttribute("currency");
                    String rate = rateElement.getAttribute("rate");
                    
                    csvWriter.append(date).append(",")
                            .append(currency).append(",")
                            .append(rate).append("\n");
                }
                csvWriter.flush();
                csvWriter.close();
                
                System.out.println("CSV-bestand gegenereerd: " + csvFilePath);
            } else {
                System.out.println("Geen wisselkoersgegevens gevonden in XML.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readECB(){
        try {
            URL url = new URI("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            
            int responsecode = conn.getResponseCode();
            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } else {
                String inline = "";
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }
                scanner.close();
                return inline;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
