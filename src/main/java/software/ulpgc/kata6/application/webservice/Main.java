package software.ulpgc.kata6.application.webservice;

import io.javalin.Javalin;
import io.javalin.http.Context;
import software.ulpgc.kata6.architecture.model.Movie;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    private static final File database = new File("movies.db");
    public static void main(String[] args) {
        try {
            Connection connection = openConnection();
            DatabaseStore store = new DatabaseStore(connection);
            DatabaseRecorder recorder = new DatabaseRecorder(connection);

            Javalin app = Javalin.create();
            app.get("/", Main::index);
            app.get("/movies", context -> getMovies(context, store));
            app.get("/movie", context -> getMovie(context, store));
            app.post("/movie", context -> createMovie(context, recorder));
            app.delete("/movie", context -> deleteMovie(context, recorder));
            app.start(8080);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void index(Context context) {
        context.status(200);
        context.result("""
                Path options:
                
                get: "/" to get help
                get: "/movies" to get all movies
                get: "/movie?title=param" to get a specific movie
                post: "/movie?title=param&year=param&duration=param" to create a new movie
                delete: "/movie?title=param" to delete an existing movie
        """);
    }

    private static void getMovies(Context context, DatabaseStore store) {
        List<Movie> movies = store.movies().toList();

        if (movies.isEmpty()) {
            context.status(404);
            context.result("Movies not found");
        } else {
            context.status(200);
            context.json(movies);
        }
    }

    private static void getMovie(Context context, DatabaseStore store) {
        String title = context.queryParam("title");
        List<Movie> movies = store.movies().filter(movie -> movie.title().equals(title)).toList();

        if (movies.isEmpty()) {
            context.status(404);
            context.result("No movies called " + title + " were found");
        } else {
            context.status(200);
            context.json(movies);
        }

    }

    private static void createMovie(Context context, DatabaseRecorder recorder) {
        String title = context.queryParam("title");
        Integer year = context.queryParamAsClass("year", Integer.class).getOrDefault(-1);
        Integer duration = context.queryParamAsClass("duration", Integer.class).getOrDefault(-1);

        if (title == null) {
            context.status(404);
            context.result("A movie needs at least a title");
        } else {
            context.status(200);
            Movie movie = new Movie(title, year, duration);
            recorder.record(Stream.of(movie));
            context.json(movie);
        }

    }

    private static void deleteMovie(Context context, DatabaseRecorder recorder) {
        String title = context.queryParam("title");
        boolean result = recorder.delete(title);

        if (!result) {
            context.status(404);
            context.result("No movies called " + title + " were found to be deleted");
        } else {
            context.status(200);
            context.json("Movie " + title + " deleted");
        }

    }

    private static Connection openConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:movies.db");
        connection.setAutoCommit(false);
        return connection;
    }
}
