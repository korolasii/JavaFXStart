package com.example.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import java.util.List;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Main extends Application {

    private final String filePath = "E:\\Timofei\\demo\\src\\main\\java\\com\\example\\demo\\notes.json";

    @Override
    public void start(Stage primaryStage) {
        TextArea notesTextArea = new TextArea();
        notesTextArea.setPromptText("Введите заметку...");

        Button saveButton = new Button("Сохранить");
        Button openButton = new Button("Открыть мои заметки");
        NotesManager notesManager = new NotesManager(filePath);

        Stage notesStage = new Stage(); // Объявляем переменную notesStage здесь один раз
        VBox notesLayout = new VBox(10);
        Scene notesScene = new Scene(notesLayout, 300, 250);
        notesStage.setTitle("Мои заметки");
        notesStage.setScene(notesScene);

        saveButton.setOnAction(e -> {
            String note = notesTextArea.getText();
            List<String> notes = notesManager.loadNotes();
            notes.add(note);
            notesManager.saveNotes(notes);
            notesTextArea.clear();
        });

        openButton.setOnAction(e -> {
            List<String> loadedNotes = notesManager.loadNotes();

            notesLayout.getChildren().clear();

            for (int i = 0; i < loadedNotes.size(); i++) {
                HBox noteBox = new HBox(10);
                String note = loadedNotes.get(i);
                Label noteLabel = new Label((i + 1) + ". " + note);

                Button deleteButton = new Button("Удалить");
                int currentIndex = i;
                deleteButton.setOnAction(event -> {
                    loadedNotes.remove(currentIndex);
                    notesManager.saveNotes(loadedNotes);
                    openButton.fire();
                });

                noteBox.getChildren().addAll(noteLabel, deleteButton);
                notesLayout.getChildren().add(noteBox);
            }

            notesStage.show();
        });

        openButton.setOnAction(e -> {
            List<String> loadedNotes = notesManager.loadNotes();

            notesLayout.getChildren().clear();

            for (int i = 0; i < loadedNotes.size(); i++) {
                HBox noteBox = new HBox(10);
                String note = loadedNotes.get(i);
                Label noteLabel = new Label((i + 1) + ". " + note);

                Button editButton = new Button("Редактировать");

                final int index = i; // Создаем локальную final переменную
                editButton.setOnAction(event -> {
                    TextInputDialog dialog = new TextInputDialog(note);
                    dialog.setTitle("Редактировать заметку");
                    dialog.setHeaderText("Введите текст для заметки:");
                    dialog.setContentText("Заметка:");

                    Optional<String> result = dialog.showAndWait();
                    result.ifPresent(editedNote -> {
                        loadedNotes.set(index, editedNote);
                        notesManager.saveNotes(loadedNotes);
                        openButton.fire();
                    });
                });

                Button deleteButton = new Button("Удалить");
                deleteButton.setOnAction(event -> {
                    loadedNotes.remove(index);
                    notesManager.saveNotes(loadedNotes);
                    openButton.fire();
                });

                noteBox.getChildren().addAll(noteLabel, editButton, deleteButton);
                notesLayout.getChildren().add(noteBox);
            }

            notesStage.show();
        });

        VBox root = new VBox(10);
        root.getChildren().addAll(notesTextArea, saveButton, openButton);

        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("Заметки");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class NotesManager {

    private final String filePath;
    private final Gson gson;

    public NotesManager(String filePath) {
        this.filePath = filePath;
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void saveNotes(List<String> notes) {
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(notes, writer);
            System.out.println("Заметки сохранены в файл: " + filePath);
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении заметок: " + e.getMessage());
        }
    }

    public List<String> loadNotes() {
        try (Reader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<ArrayList<String>>(){}.getType();
            List<String> loadedNotes = gson.fromJson(reader, listType);
            return loadedNotes != null ? loadedNotes : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке заметок: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}
