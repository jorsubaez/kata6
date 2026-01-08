package software.ulpgc.kata6.application.database;


public class Main {
    public static void main(String[] args) {
        Desktop.create(new RemoteStore(TsvMovieParser::from))
                .display()
                .setVisible(true);
    }

}