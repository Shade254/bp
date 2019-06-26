package cz.matocmir.tours.utils;

import cz.matocmir.tours.model.TourGraph;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SerTourGraphWriter {
	private static final Logger log = Logger.getLogger(SerTourGraphWriter.class);
	private File outputFile;

	public SerTourGraphWriter(File outputFile) {
		this.outputFile = outputFile;
	}

	public boolean write(TourGraph tourGraph) {

		try {
			FileOutputStream fileOut =
					new FileOutputStream(outputFile);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(tourGraph);
			out.close();
			fileOut.close();
			System.out.print("Serialized data is saved in " + outputFile.getAbsolutePath());
			return true;
		} catch (IOException i) {
			i.printStackTrace();
		}

		return false;
	}
}
