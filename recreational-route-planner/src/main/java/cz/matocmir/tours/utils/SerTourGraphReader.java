package cz.matocmir.tours.utils;

import cz.matocmir.tours.model.TourGraph;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class SerTourGraphReader {

	private static final Logger log = Logger.getLogger(SerTourGraphReader.class);
	private File inputFile;

	public SerTourGraphReader(File inputFile) {
		this.inputFile = inputFile;
	}

	public TourGraph read() {
		TourGraph e = null;
		try {
			FileInputStream fileIn = new FileInputStream(inputFile);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			e = (TourGraph) in.readObject();
			in.close();
			fileIn.close();
			return e;
		} catch (IOException i) {
			i.printStackTrace();
			return null;
		} catch (ClassNotFoundException c) {
			System.out.println("Graph class not found");
			c.printStackTrace();
			return null;
		}
	}
}
