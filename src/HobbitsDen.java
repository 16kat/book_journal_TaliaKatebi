import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class HobbitsDen extends Application {

    static class Book {
        private final String title;
        private final String author;
        private final String genre;
        private final String dateFinished;
        private final int rating;
        private final String notes;

        public Book(String title, String author, String genre, String dateFinished, int rating, String notes) {
            this.title = title;
            this.author = author;
            this.genre = genre;
            this.dateFinished = dateFinished;
            this.rating = rating;
            this.notes = notes;
        }

        @Override
        public String toString() {
            return "Title: " + title + "\nAuthor: " + author + "\nGenre: " + genre +
                   "\nDate Finished: " + dateFinished + "\nRating: " + rating +
                   "\nNotes: " + notes;
        }
    }

    static class BookJournal {
        private final List<Book> books = new ArrayList<>();

        public void addBook(Book book) {
            books.add(book);
        }

        public List<Book> getAllBooks() {
            return books;
        }
    }

    private final BookJournal journal = new BookJournal();
    private final VBox contentBox = new VBox(10);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("My Book Journal");

        // Main Layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setBackground(new Background(new BackgroundFill(Color.PALEGREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        // Top: Title
        VBox top = new VBox();
        Label titleLabel = new Label("Welcome to Book Journal CSI2300!!!");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        top.getChildren().add(titleLabel);
        top.setPadding(new Insets(10));
        top.setStyle("-fx-alignment: center;");
        root.setTop(top);

        // Center: Buttons
        VBox center = new VBox(20);
        center.setPadding(new Insets(10));
        center.setStyle("-fx-alignment: center;");

        Button addButton = new Button("Add Book");
        Button viewButton = new Button("View All Books");

        addButton.setPrefWidth(200);
        viewButton.setPrefWidth(200);

        center.getChildren().addAll(addButton, viewButton);
        root.setCenter(center);

        // Bottom: Dynamic Content Box
        contentBox.setPadding(new Insets(10));
        contentBox.setStyle("-fx-alignment: top-left; -fx-border-color: gray; -fx-border-width: 1px;");
        root.setBottom(contentBox);

        // Button Actions
        addButton.setOnAction(e -> showAddBookForm());
        viewButton.setOnAction(e -> showAllBooks());

        // Set Scene and Show Stage
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    private void showAddBookForm() {
        contentBox.getChildren().clear();

        Label titleLabel = new Label("Title:");
        TextField titleField = new TextField();

        Label authorLabel = new Label("Author:");
        TextField authorField = new TextField();

        Label genreLabel = new Label("Genre:");
        TextField genreField = new TextField();

        Label dateLabel = new Label("Date Finished (YYYY-MM-DD):");
        TextField dateField = new TextField();

        Label ratingLabel = new Label("Rating (1-5):");
        Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, 1);

        Label notesLabel = new Label("Notes:");
        TextArea notesArea = new TextArea();

        Button submitButton = new Button("Add Book");
        submitButton.setOnAction(e -> {
            String title = titleField.getText();
            String author = authorField.getText();
            String genre = genreField.getText();
            String dateFinished = dateField.getText();
            int rating = ratingSpinner.getValue();
            String notes = notesArea.getText();

            Book book = new Book(title, author, genre, dateFinished, rating, notes);
            journal.addBook(book);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Book added successfully!");
            alert.showAndWait();

            titleField.clear();
            authorField.clear();
            genreField.clear();
            dateField.clear();
            notesArea.clear();
        });

        contentBox.getChildren().addAll(titleLabel, titleField, authorLabel, authorField, genreLabel, genreField,
                dateLabel, dateField, ratingLabel, ratingSpinner, notesLabel, notesArea, submitButton);
    }

    private void showAllBooks() {
        contentBox.getChildren().clear();

        if (journal.getAllBooks().isEmpty()) {
            contentBox.getChildren().add(new Label("No books in your journal yet."));
            return;
        }

        for (Book book : journal.getAllBooks()) {
            Label bookLabel = new Label(book.toString());
            contentBox.getChildren().add(bookLabel);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
