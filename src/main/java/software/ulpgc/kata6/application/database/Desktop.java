package software.ulpgc.kata6.application.database;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import software.ulpgc.kata6.architecture.io.Store;
import software.ulpgc.kata6.architecture.model.Movie;
import software.ulpgc.kata6.architecture.viewmodel.Histogram;
import software.ulpgc.kata6.architecture.viewmodel.HistogramBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Stream;

public class Desktop extends JFrame {

    private Desktop() throws HeadlessException {
        this.setTitle("Histogram");
        this.setResizable(false);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
    }

    public static Desktop create(Store store) {
        return new Desktop();
    }

    public Desktop display() {
        this.getContentPane().add(chartPanelWith(histogram()));
        return this;
    }

    private ChartPanel chartPanelWith(Histogram histogram) {
        return new ChartPanel(chartWith(histogram));
    }

    private JFreeChart chartWith(Histogram histogram) {
        return ChartFactory.createHistogram(
                histogram.title(),
                histogram.x(),
                histogram.y(),
                datasetWith(histogram)
        );
    }

    private XYSeriesCollection datasetWith(Histogram histogram) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesIn(histogram));
        return dataset;
    }

    private XYSeries seriesIn(Histogram histogram) {
        XYSeries series = new XYSeries(histogram.legend());
        for (int bin: histogram) series.add(bin, histogram.count(bin));
        return series;
    }

    private static Histogram histogram() {
        return HistogramBuilder
                .with(movies())
                .title("Movies per year")
                .x("Year")
                .y("Count")
                .legend("Movies")
                .use(Movie::year);
    }

    private static Stream<Movie> movies() {
        return new RemoteStore(TsvMovieParser::from)
                .movies()
                .limit(1000);
    }
}
