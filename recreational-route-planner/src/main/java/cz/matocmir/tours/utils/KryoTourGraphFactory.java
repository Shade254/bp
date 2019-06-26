package cz.matocmir.tours.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.umotional.basestructures.GPSLocation;
import cz.matocmir.tours.model.TourEdge;
import cz.matocmir.tours.model.TourGraph;
import cz.matocmir.tours.model.TourNode;
import jersey.repackaged.com.google.common.collect.HashBasedTable;

import java.util.UUID;

public class KryoTourGraphFactory {

	public static Kryo get() {
		Kryo kryo = new Kryo();

		kryo.register(SerializableTourGraph.class);
		kryo.register(TourGraph.class);
		kryo.register(TourEdge.class);
		kryo.register(TourNode.class);
		kryo.register(GPSLocation.class);
		kryo.register(HashBasedTable.class, new JavaSerializer());
		kryo.register(UUID.class, new UUIDSerializer());

		return kryo;
	}

	private static class UUIDSerializer extends Serializer<UUID> {

		UUIDSerializer() {
			super(false, true);
		}

		@Override
		public void write(final Kryo kryo, final Output output, final UUID uuid) {
			output.writeLong(uuid.getMostSignificantBits());
			output.writeLong(uuid.getLeastSignificantBits());
		}

		@Override
		public UUID read(Kryo kryo, Input input, Class<UUID> aClass) {
			return new UUID(input.readLong(), input.readLong());
		}
	}

}