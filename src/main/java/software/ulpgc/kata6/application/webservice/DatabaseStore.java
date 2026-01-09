package software.ulpgc.kata6.application.webservice;

import software.ulpgc.kata6.architecture.io.Store;
import software.ulpgc.kata6.architecture.model.Movie;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Stream;

public class DatabaseStore implements Store {
    private final Connection connection;

    public DatabaseStore(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Stream<Movie> movies() {
        try {
            return moviesIn(query());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<Movie> moviesIn(ResultSet query) {
        return Stream.generate(()->movieIn(query)).takeWhile(Objects::nonNull);
    }

    private Movie movieIn(ResultSet rs) {
        try {
            return rs.next() ? readFrom(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Movie readFrom(ResultSet rs) throws SQLException {
        return new Movie(
                rs.getString(1),
                rs.getInt(2),
                rs.getInt(3)
        );
    }

    private ResultSet query() throws SQLException {
        return connection.createStatement().executeQuery("SELECT * FROM movies");
    }
}
