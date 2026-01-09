package software.ulpgc.kata6.application.webservice;

import software.ulpgc.kata6.architecture.io.Store;
import software.ulpgc.kata6.architecture.model.Movie;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class DatabaseStore implements Store {
    private final Connection connection;

    public DatabaseStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Stream<Movie> movies() {
        List<Movie> movies = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet query = statement.executeQuery("SELECT * FROM movies");
            while (query.next()) {
                movies.add(readFrom(query));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return movies.stream();
    }


    private Movie readFrom(ResultSet rs) throws SQLException {
        return new Movie(
                rs.getString(1),
                rs.getInt(2),
                rs.getInt(3)
        );
    }

}
