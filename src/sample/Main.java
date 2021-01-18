package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Основной класс приложения.
 */
public class Main extends Application {
    private FilesView leftView;
    private FilesView rightView;

    /**
     * Проверка, какая панель менеджера активна.
     * @return Активная панель.
     */
    private FilesView getCurrentView() {
        if (leftView.focusedNow()) {
            return leftView;
        } else if (rightView.focusedNow()) {
            return rightView;
        } else {
            return null;
        }
    }

    /**
     * Получение активной панели для определения, в какой директории создавать файл/каталог.
     * Отображение диалога для ввода имени создаваемого файла/каталога.
     * @param titleForDialog Название окна создаваемого диалога.
     * @return Абсолютный путь для создаваемого файла.
     */
    private Path checkViewAndGetPath(String titleForDialog) {
        FilesView currentView = getCurrentView();
        if (currentView == null) {
            return null;
        }
        Path currentDirectory = currentView.getDirectory();
        String filename = DialogsToShow.showInputDialog(titleForDialog);
        if (filename == null) {
            return null;
        }
        return currentDirectory.resolve(filename);
    }

    /**
     * Обновление обеих панелей менеджера.
     */
    private void refreshBothViews() {
        leftView.refresh();
        rightView.refresh();
    }

    /**
     * Создание файла.
     */
    private void createFile() {
        Path filepath = checkViewAndGetPath("Создание файла");
        if (filepath == null) {
            return;
        }
        try {
            Files.createFile(filepath);
            refreshBothViews();
        } catch (FileAlreadyExistsException e) {
            DialogsToShow.showError("Такой файл уже есть.");
        } catch (Exception e) {
            DialogsToShow.showError("Не удалось создать файл.");
        }
    }

    /**
     * Создание каталога.
     */
    private void createDirectory() {
        Path filepath = checkViewAndGetPath("Создание каталога");
        if (filepath == null) {
            return;
        }
        try {
            Files.createDirectory(filepath);
            refreshBothViews();
        } catch (FileAlreadyExistsException e) {
            DialogsToShow.showError("Такой каталог уже есть.");
        } catch (Exception e) {
            DialogsToShow.showError("Не удалось создать каталог.");
        }
    }

    /**
     * Копирование или перемещение файла/каталога.
     * @param shouldCopy Если данная переменная истина, то необходимо копировать, иначе переместить.
     */
    private void copyOrMove(Boolean shouldCopy) {
        Path source;
        Path target;
        if (leftView.isFocused()) {
            source = leftView.getSelection();
            target = rightView.getDirectory();
        } else if (rightView.isFocused()) {
            source = rightView.getSelection();
            target = leftView.getDirectory();
        } else {
            return;
        }
        if (source == null) {
            return;
        }
        File sourceFile = source.toFile();
        try {
            if (shouldCopy) {
                if (sourceFile.isFile()) {
                    Files.copy(source, target.resolve(source.getFileName()));
                } else {
                    FileUtils.copyDirectoryToDirectory(sourceFile, target.toFile());
                }
            } else {
                if (sourceFile.isFile()) {
                    Files.move(source, target.resolve(source.getFileName()));
                } else {
                    FileUtils.moveToDirectory(sourceFile, target.toFile(), false);
                }
            }
            refreshBothViews();
        } catch (FileAlreadyExistsException e) {
            DialogsToShow.showError("Такой файл/каталог уже есть.");
        } catch (Exception e)  {
            DialogsToShow.showError("Ошибка.");
        }
    }

    /**
     * Удаление файла/каталога.
     */
    private void delete() {
        FilesView currentView = getCurrentView();
        if (currentView == null) {
            return;
        }
        Path pathForDeletion = getCurrentView().getSelection();
        if (pathForDeletion == null) {
            return;
        }
        boolean deletionConfirmed = DialogsToShow.showDeleteConfirmationDialog(pathForDeletion.toString());
        try {
            if (deletionConfirmed) {
                FileUtils.forceDelete(pathForDeletion.toFile());
            }
            refreshBothViews();
        } catch (Exception e) {
            DialogsToShow.showError("Ошибка.");
        }
    }

    /**
     * Инициализация основной области приложения - создание двух панелей для работы с файлами.
     * @return Основная область с панелями.
     */
    private HBox makeMainBox() {
        HBox mainBox = new HBox();
        File[] fsRoots = File.listRoots();
        leftView = new FilesView(fsRoots[0].getPath());
        rightView = new FilesView(fsRoots[0].getPath());
        VBox leftBox = new VBox(leftView.getCurrentDirView(), leftView);
        VBox rightBox = new VBox(rightView.getCurrentDirView(), rightView);
        leftView.setFocusTraversable(true);
        VBox.setVgrow(leftView, Priority.ALWAYS);
        VBox.setVgrow(rightView, Priority.ALWAYS);
        HBox.setHgrow(leftBox, Priority.ALWAYS);
        HBox.setHgrow(rightBox, Priority.ALWAYS);
        mainBox.getChildren().addAll(leftBox, rightBox);
        VBox.setVgrow(mainBox, Priority.ALWAYS);

        return mainBox;
    }

    /**
     * Инициализация области с кнопками для создания, копирования, перемещения, удаления файла или каталога.
     * @return Область с кнопками.
     */
    private HBox makeButtonsBox() {
        HBox buttonsBox = new HBox();
        Button[] buttons = new Button[5];
        buttons[0] = new Button("Создать файл");
        buttons[0].setOnAction(event -> createFile());
        buttons[1] = new Button("Создать каталог");
        buttons[1].setOnAction(event -> createDirectory());
        buttons[2] = new Button("Копировать");
        buttons[2].setOnAction(event -> copyOrMove(true));
        buttons[3] = new Button("Переместить");
        buttons[3].setOnAction(event -> copyOrMove(false));
        buttons[4] = new Button("Удалить");
        buttons[4].setOnAction(event -> delete());
        for (Button button : buttons) {
            button.setFocusTraversable(false);
        }
        buttonsBox.getChildren().addAll(buttons);
        buttonsBox.setAlignment(Pos.CENTER);

        return buttonsBox;
    }

    /**
     * Создание основного окна приложения.
     * @param primaryStage Основное окно.
     */
    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox();

        HBox mainBox = makeMainBox();
        HBox buttonsBox = makeButtonsBox();

        root.getChildren().addAll(mainBox, buttonsBox);

        primaryStage.setTitle("FileManager_Exam");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.show();
    }

    /**
     * Функция для запуска приложения.
     * @param args Параметры запуска.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
