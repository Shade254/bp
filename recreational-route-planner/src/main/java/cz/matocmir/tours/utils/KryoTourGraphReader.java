package cz.matocmir.tours.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import cz.matocmir.tours.model.TourGraph;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class KryoTourGraphReader{

	private static final Logger log = Logger.getLogger(KryoTourGraphReader.class);
	private File inputFile;

	public KryoTourGraphReader(File inputFile) {
		this.inputFile = inputFile;
	}

	public TourGraph read() {
		try {
			Kryo kryo = KryoTourGraphFactory.get();

			Input input = new Input(new FileInputStream(inputFile));
			SerializableTourGraph serializableTourGraph = kryo.readObject(input, SerializableTourGraph.class);

			input.close();

			return new TourGraph(serializableTourGraph);

		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}
}
