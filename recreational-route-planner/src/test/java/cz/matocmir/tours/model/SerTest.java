package cz.matocmir.tours.model;

import cz.matocmir.tours.utils.KryoTourGraphReader;
import cz.matocmir.tours.utils.KryoTourGraphWriter;
import cz.matocmir.tours.utils.SerTourGraphReader;
import cz.matocmir.tours.utils.SerTourGraphWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class SerTest {
	private TourGraph orig;

	@Before
	public void setUp() throws Exception {
		long milis = System.currentTimeMillis();
		orig = TourGraph.graphFromCSV("./src/main/resources/prague_min.csv", 2065);
		System.out.println("CSV loading took " + (System.currentTimeMillis() - milis));
	}

	@Test
	public void serialize() {
		long milis = System.currentTimeMillis();
		KryoTourGraphWriter writer = new KryoTourGraphWriter(new File("./test1.kryo"));
		writer.write(orig);
		System.out.println("Kryo saving took " + (System.currentTimeMillis() - milis));

		milis = System.currentTimeMillis();
		KryoTourGraphReader reader = new KryoTourGraphReader(new File("./test1.kryo"));
		TourGraph newer = reader.read();
		System.out.println("Kryo loading took " + (System.currentTimeMillis() - milis));
	}

	@Test
	public void serialize2() {
		long milis = System.currentTimeMillis();
		SerTourGraphWriter writer = new SerTourGraphWriter(new File("./test2.kryo"));
		writer.write(orig);
		System.out.println("Ser saving took " + (System.currentTimeMillis() - milis));

		milis = System.currentTimeMillis();
		SerTourGraphReader reader = new SerTourGraphReader(new File("./test2.kryo"));
		TourGraph newer = reader.read();
		System.out.println("Ser loading took " + (System.currentTimeMillis() - milis));
	}
}
