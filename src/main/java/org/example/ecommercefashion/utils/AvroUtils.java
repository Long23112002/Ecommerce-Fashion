package org.example.ecommercefashion.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

public class AvroUtils {

  public static <T> byte[] serializeAvro(T data, Class<T> clazz) throws IOException {
    DatumWriter<T> writer = new SpecificDatumWriter<>(clazz);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Encoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
    writer.write(data, encoder);
    encoder.flush();
    outputStream.flush();
    return outputStream.toByteArray();
  }

  public static <T> T deserializeAvro(byte[] data, Class<T> clazz) throws IOException {
    DatumReader<T> reader = new SpecificDatumReader<>(clazz);
    return reader.read(
        null, DecoderFactory.get().binaryDecoder(new ByteArrayInputStream(data), null));
  }
}
