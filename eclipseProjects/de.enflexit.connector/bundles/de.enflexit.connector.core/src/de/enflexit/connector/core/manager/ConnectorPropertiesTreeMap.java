package de.enflexit.connector.core.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.enflexit.common.properties.Properties;

/**
 * This class is used to store connector properties to JSON files.
 * @author Nils Loose - SOFTEC - Paluno - University of Duisburg-Essen
 */
public class ConnectorPropertiesTreeMap extends TreeMap<String, Properties> {

	private static final long serialVersionUID = 8348119759217794256L;
	private static final String FILE_ENCODING = "UTF-8";
	
	/**
	 * Stores a ConnectorPropertiesTreeMap to a JSON file.
	 * @param jsonFile the json file
	 */
	public void storeToJsonFile(File jsonFile) {
		
		FileWriter fileWriter = null;
		
		try {
			fileWriter = new FileWriter(jsonFile);
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setPrettyPrinting();
			Gson gson = gsonBuilder.create();
			gson.toJson(this, fileWriter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { 
			if (fileWriter!=null) {
				try {
					fileWriter.close();
				} catch (IOException e) {
					System.err.println("[" + this.getClass().getSimpleName() + "] Error closing file writer!");
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Loads a ConnectorPropertiesTreeMap from a JSON file.
	 * @param jsonFile the json file
	 * @return the connector properties tree map
	 */
	public static ConnectorPropertiesTreeMap loadFromJsonFile(File jsonFile) {
		ConnectorPropertiesTreeMap loadedMap = null;
		
		if (jsonFile.exists()) {
			FileReader fileReader = null;
			try {
				fileReader = new FileReader(jsonFile);
				GsonBuilder gsonBuilder = new GsonBuilder();
				Gson gson = gsonBuilder.create();
				loadedMap = gson.fromJson(fileReader, ConnectorPropertiesTreeMap.class);
			} catch (FileNotFoundException e) {
				System.err.println("[" + ConnectorPropertiesTreeMap.class.getSimpleName() + "] Error loading JSON from " + jsonFile.getName());
				e.printStackTrace();
			} finally {
				if (fileReader!=null) {
					try {
						fileReader.close();
					} catch (IOException e) {
						System.err.println("[" + ConnectorPropertiesTreeMap.class.getSimpleName() + "] Error closing file reader!");
						e.printStackTrace();
					}
				}
			}
		}
		
		return loadedMap;
	}
	
	
	public boolean storeToXmlFile(File xmlFile) {
		boolean success = false;
		
		FileWriter fileWriter = null;
		try {
			
			JAXBContext context = JAXBContext.newInstance(this.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, FILE_ENCODING);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			fileWriter = new FileWriter(xmlFile);
			marshaller.marshal(this, fileWriter);
			
			success = true;
			
		} catch (Exception e) {
			System.err.println("[" + this.getClass().getSimpleName() + "] Error storing connector configuraiton to XML file!");
			e.printStackTrace();
		} finally {
			try {
				if (fileWriter!=null) {
					fileWriter.close();
				}
			} catch (IOException e) {
				System.err.println("[" + this.getClass().getSimpleName() + "] Error closing file writer!");
				e.printStackTrace();
			}
		}
		
		return success;
	}
	
	public static ConnectorPropertiesTreeMap loadFromXmlFile(File xmlFile) {
		ConnectorPropertiesTreeMap loadedConfig = null;
		
		if (xmlFile!=null && xmlFile.exists()==true) {
			FileReader fileReader = null;
			
			try {
				JAXBContext context = JAXBContext.newInstance(ConnectorPropertiesTreeMap.class);
				Unmarshaller unmarshaller = context.createUnmarshaller();
				
				fileReader = new FileReader(xmlFile);
				loadedConfig = (ConnectorPropertiesTreeMap) unmarshaller.unmarshal(fileReader);
				
			} catch (JAXBException e) {
				System.err.println("[" + ConnectorPropertiesTreeMap.class.getSimpleName() + "] Error reading connector configuraiton from XML file!");
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				System.err.println("[" + ConnectorPropertiesTreeMap.class.getSimpleName() + "] Connector configuration file not found!");
				e.printStackTrace();
			} finally {
				try {
					if (fileReader!=null) fileReader.close();
				} catch (IOException e) {
					System.err.println("[" + ConnectorPropertiesTreeMap.class.getSimpleName() + "] Error closing file writer!");
					e.printStackTrace();
				}
			}
		}
		
		return loadedConfig;
	}

}
