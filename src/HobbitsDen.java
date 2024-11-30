import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HobbitsDen extends Application {

    static class Book implements Serializable {
        private String title;
        private String author;
        private String genre;
        private String dateFinished;
        private int rating;
        private String notes;
    
        public Book(String title, String author, String genre, String dateFinished, int rating, String notes) {
            this.title = title;
            this.author = author;
            this.genre = genre;
            this.dateFinished = dateFinished;
            this.rating = rating;
            this.notes = notes;
        }
    
        // Getters
        public String getTitle() {
            return title;
        }
    
        public String getAuthor() {
            return author;
        }
    
        public String getGenre() {
            return genre;
        }
    
        public String getDateFinished() {
            return dateFinished;
        }
    
        public int getRating() {
            return rating;
        }
    
        public String getNotes() {
            return notes;
        }
    
        // Setters
        public void setTitle(String title) {
            this.title = title;
        }
    
        public void setAuthor(String author) {
            this.author = author;
        }
    
        public void setGenre(String genre) {
            this.genre = genre;
        }
    
        public void setDateFinished(String dateFinished) {
            this.dateFinished = dateFinished;
        }
    
        public void setRating(int rating) {
            this.rating = rating;
        }
    
        public void setNotes(String notes) {
            this.notes = notes;
        }
    
        @Override
        public String toString() {
            return "Title: " + title + "\nAuthor: " + author + "\nGenre: " + genre +
                   "\nDate Finished: " + dateFinished + "\nRating: " + rating +
                   "\nNotes: " + notes;
        }
    
        public String getBriefDescription() {
            return title + " by " + author;
        }
    }
    
    static class BookJournal implements Serializable {
        private final Map<String, List<Book>> userBooks = new HashMap<>();

        public void addBook(String username, Book book) {
            userBooks.computeIfAbsent(username, k -> new ArrayList<>()).add(book);
        }

        public List<Book> getBooks(String username) {
            return userBooks.getOrDefault(username, new ArrayList<>());
        }

        public void saveToFile(String filename) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
                out.writeObject(userBooks);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void loadFromFile(String filename) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
                Object obj = in.readObject();
                if (obj instanceof Map) {
                    userBooks.putAll((Map<String, List<Book>>) obj);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("No previous data found.");
            }
        }
    }

    private final BookJournal journal = new BookJournal();
    private String currentUser;
    private final VBox contentBox = new VBox(10);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hobbit's Den");

        journal.loadFromFile("book_journal.dat");

        // Prompt for username
        TextInputDialog usernameDialog = new TextInputDialog();
        usernameDialog.setTitle("Welcome to Hobbit's Den");
        usernameDialog.setHeaderText("Enter Your Username");
        usernameDialog.setContentText("Username:");
        currentUser = usernameDialog.showAndWait().orElse("Guest");

        // Main Layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Set background image
        Image backgroundImage = new Image("file:library_background.jpg");
        BackgroundImage bgImage = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(100, 100, true, true, false, true)
        );
        root.setBackground(new Background(bgImage));

        // Top: Titles
        VBox top = new VBox(5);
        Label mainTitle = new Label("Hobbit's Den");
        mainTitle.setFont(javafx.scene.text.Font.font("Verdana", javafx.scene.text.FontWeight.BOLD, 60));
        mainTitle.setStyle("-fx-font-family: 'Lucida Calligraphy'; -fx-font-size: 60px; -fx-font-weight: bold;");
        Label subTitle = new Label("Welcome, " + currentUser + "!");
        subTitle.setFont(javafx.scene.text.Font.font("Garamond", javafx.scene.text.FontWeight.NORMAL, 32));
        top.getChildren().addAll(mainTitle, subTitle);
        top.setPadding(new Insets(10));
        top.setStyle("-fx-alignment: center;");
        root.setTop(top);

        // Center: Buttons
        VBox center = new VBox(20);
        center.setPadding(new Insets(10));
        center.setStyle("-fx-alignment: center;");

        Button addButton = new Button("Add Book");
        Button viewButton = new Button("View My Books");
        Button searchButton = new Button("Search Books");
        Button exitButton = new Button("Exit");

        addButton.setPrefWidth(200);
        viewButton.setPrefWidth(200);
        searchButton.setPrefWidth(200);
        exitButton.setPrefWidth(200);

        center.getChildren().addAll(addButton, viewButton, searchButton, exitButton);
        root.setCenter(center);

        // Bottom: Dynamic Content Box
        contentBox.setPadding(new Insets(10));
        contentBox.setStyle("-fx-border-color: gray; -fx-border-width: 1px; -fx-background-color: rgba(255, 255, 255, 0.8);");
        root.setBottom(contentBox);

        // Button Actions
        addButton.setOnAction(e -> showAddBookForm());
        viewButton.setOnAction(e -> showAllBooks());
        searchButton.setOnAction(e -> showSearchBooks());
        exitButton.setOnAction(e -> {
            journal.saveToFile("book_journal.dat");
            primaryStage.close();
        });

        // Apply the styles.css file
        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        // Set Scene and Show Stage
        primaryStage.setScene(scene);
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
            journal.addBook(currentUser, book);

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
    
        List<Book> userBooks = journal.getBooks(currentUser);
        if (userBooks.isEmpty()) {
            contentBox.getChildren().add(new Label("No books in your journal yet."));
            return;
        }
    
        // Create a VBox to hold all the book entries
        VBox bookList = new VBox(10);
        bookList.setPadding(new Insets(10));
    
        for (Book book : userBooks) {
            // HBox for each book entry
            HBox bookEntry = new HBox(20); // Space between book title and buttons
            bookEntry.setStyle("-fx-alignment: center-left;"); // Align book title to the left
    
            // Book title label
            Label bookLabel = new Label(book.getBriefDescription());
            bookLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
    
            // Create action buttons (Details, Edit, and Delete)
            Button detailsButton = new Button("View Details");
            Button editButton = new Button("Edit");
            Button deleteButton = new Button("Delete");
    
            // Add buttons to an HBox and align them to the left
            HBox actionsBox = new HBox(10, detailsButton, editButton, deleteButton);
    
            // Action for View Details button
            detailsButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, book.toString());
                alert.setHeaderText("Book Details");
                alert.showAndWait();
            });
    
            // Action for Edit button
            editButton.setOnAction(e -> showEditBookForm(book));
    
            // Action for Delete button
            deleteButton.setOnAction(e -> {
                // Confirmation dialog before deletion
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to delete this book?");
                confirmationAlert.setHeaderText("Confirm Deletion");
                confirmationAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        userBooks.remove(book); // Remove the book from the list
                        journal.saveToFile("book_journal.dat"); // Save changes
                        showAllBooks(); // Refresh the book list
                    }
                });
            });
    
            // Add book title and action buttons to the HBox
            bookEntry.getChildren().addAll(bookLabel, actionsBox);
    
            // Add this book entry HBox to the VBox
            bookList.getChildren().add(bookEntry);
        }
    
        // Add the book list (VBox) to the contentBox
        contentBox.getChildren().add(bookList);
    }

    private void showEditBookForm(Book book) {
        contentBox.getChildren().clear();
    
        Label titleLabel = new Label("Title:");
        TextField titleField = new TextField(book.getTitle());
    
        Label authorLabel = new Label("Author:");
        TextField authorField = new TextField(book.getAuthor());
    
        Label genreLabel = new Label("Genre:");
        TextField genreField = new TextField(book.getGenre());
    
        Label dateLabel = new Label("Date Finished (YYYY-MM-DD):");
        TextField dateField = new TextField(book.getDateFinished());
    
        Label ratingLabel = new Label("Rating (1-5):");
        Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, book.getRating());
    
        Label notesLabel = new Label("Notes:");
        TextArea notesArea = new TextArea(book.getNotes());
    
        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> {
            book.setTitle(titleField.getText());
            book.setAuthor(authorField.getText());
            book.setGenre(genreField.getText());
            book.setDateFinished(dateField.getText());
            book.setRating(ratingSpinner.getValue());
            book.setNotes(notesArea.getText());
    
            journal.saveToFile("book_journal.dat");
    
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Book updated successfully!");
            alert.showAndWait();
            showAllBooks(); // Refresh the book list
        });
    
        contentBox.getChildren().addAll(titleLabel, titleField, authorLabel, authorField, genreLabel, genreField,
                dateLabel, dateField, ratingLabel, ratingSpinner, notesLabel, notesArea, saveButton);
    }
    

    private void showSearchBooks() {
        contentBox.getChildren().clear();

        Label searchLabel = new Label("Search by Title:");
        TextField searchField = new TextField();
        Button searchButton = new Button("Search");

        searchButton.setOnAction(e -> {
            String searchTerm = searchField.getText().toLowerCase();
            List<Book> userBooks = journal.getBooks(currentUser);
            List<Book> searchResults = new ArrayList<>();

            for (Book book : userBooks) {
                if (book.getTitle().toLowerCase().contains(searchTerm)) {
                    searchResults.add(book);
                }
            }

            contentBox.getChildren().clear();
            if (searchResults.isEmpty()) {
                contentBox.getChildren().add(new Label("No books found with that title."));
            } else {
                for (Book book : searchResults) {
                    HBox bookEntry = new HBox(10);
                    Label bookLabel = new Label(book.getBriefDescription());
                    Button detailsButton = new Button("View Details");

                    // Details button action
                    detailsButton.setOnAction(e2 -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, book.toString());
                        alert.setHeaderText("Book Details");
                        alert.showAndWait();
                    });

                    bookEntry.getChildren().addAll(bookLabel, detailsButton);
                    contentBox.getChildren().add(bookEntry);
                }
            }
        });

        contentBox.getChildren().addAll(searchLabel, searchField, searchButton);
    }

    

    public static void main(String[] args) {
        launch(args);
    }
}
