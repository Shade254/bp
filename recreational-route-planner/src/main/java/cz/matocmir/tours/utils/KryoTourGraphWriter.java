package cz.matocmir.tours.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import cz.matocmir.tours.model.TourGraph;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class KryoTourGraphWriter {
	private static final Logger log = Logger.getLogger(KryoTourGraphWriter.class);
	private File outputFile;

	public KryoTourGraphWriter(File outputFile) {
		this.outputFile = outputFile;
	}

	public boolean write(TourGraph tourGraph) {

		Kryo kryo = KryoTourGraphFactory.get();

		try {
			Output output = new Output(new FileOutputStream(outputFile));
			kryo.writeObject(output, tourGraph.getSerializableTnsGraph());
			output.close();

			return true;
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		}

		return true;
	}
}
