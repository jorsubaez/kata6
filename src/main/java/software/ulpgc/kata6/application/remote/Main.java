package software.ulpgc.kata6.application.remote;

import software.ulpgc.kata6.application.database.*;
import software.ulpgc.kata6.architecture.model.Movie;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:movies.db")) {
            connection.setAutoCommit(false);
            importIfNeededInto(connection);
            Desktop.create(new DatabaseStore(connection))
                    .display()
                    .setVisible(true);

        }
    }

    private static void importIfNeededInto(Connection connection) throws SQLException {
        if (new File("movies.db").length() > 0) return;
        Stream<Movie> movies = new RemoteStore(TsvMovieParser::from).movies();
        new DatabaseRecorder(connection).record(movies);
    }
}
