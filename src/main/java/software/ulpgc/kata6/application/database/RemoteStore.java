package software.ulpgc.kata6.application.database;

import software.ulpgc.kata6.architecture.io.Store;
import software.ulpgc.kata6.architecture.model.Movie;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class RemoteStore implements Store {
    private final Function<String, Movie> deserialize;

    public RemoteStore(Function<String, Movie> deserialize) {
        this.deserialize = deserialize;
    }

    @Override
    public Stream<Movie> movies() {
        try {
            return loadFrom(new URL("https://datasets.imdbws.com/title.basics.tsv.gz"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<Movie> loadFrom(URL url) throws IOException {
        return loadFrom(url.openConnection());
    }

    private Stream<Movie> loadFrom(URLConnection connection) throws IOException {
        return loadFrom(unzip(connection.getInputStream()));
    }

    private InputStream unzip(InputStream inputStream) throws IOException {
        return new GZIPInputStream(new BufferedInputStream(inputStream));
    }

    private Stream<Movie> loadFrom(InputStream inputStream) throws IOException {
        return loadFrom(toReader(inputStream));
    }

    private Stream<Movie> loadFrom(BufferedReader reader) throws IOException {
        return reader.lines().skip(1).map(deserialize);
    }

    private BufferedReader toReader(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }
}
