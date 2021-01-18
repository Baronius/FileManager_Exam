package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Класс, реализующий панель для работы с файловой системой.
 */
public class FilesView extends javafx.scene.control.ListView<String> {
    private File currentDirectory;
    private TextField currentDirView;
    private ObservableList<String> filesList;

    /**
     * Получение списка файлов текущего открытого каталога.
     * @return Отсортированный список файлов/каталогов, содержащихся в текущем открытом каталоге.
     */
    private String[] getCurrentFilesList() {
        File[] listOfFiles = currentDirectory.listFiles(file -> !file.isHidden());
        if (listOfFiles == null) {
            listOfFiles = new File[0];
        }
        Arrays.sort(listOfFiles, (fileA, fileB) -> {
            if ((fileA.isDirectory() && fileB.isDirectory()) || (fileA.isFile() && fileB.isFile())) {
                return fileA.compareTo(fileB);
            }
            return fileA.isDirectory() ? -1 : 1;
        });

        String[] currentFilesList = new String[listOfFiles.length];
        for (int i = 0; i < currentFilesList.length; ++i) {
            currentFilesList[i] = listOfFiles[i].getName();
        }

        return currentFilesList;
    }

    /**
     * Отображение в панели списка файлов текущего открытого каталога.
     * @param list Список файлов текущего открытого каталога.
     */
    private void showList(String[] list) {
        filesList.clear();
        if (currentDirectory.toString().length() > 3) {
            filesList.add("..");
        }
        if (list != null) {
            filesList.addAll(list);
        }
    }

    /**
     * Обновление списка отображаемых файлов с учетом текущего каталога.
     */
    public void refresh() {
        currentDirView.setText(currentDirectory.getPath());
        showList(getCurrentFilesList());
    }

    /**
     * Проверка наличия фокуса на панели.
     * @return Если истина, то фокус на этой панели.
     */
    public Boolean focusedNow() {
        return isFocused() || currentDirView.isFocused();
    }

    /**
     * Получение компонента для отображения текущего открытого каталога.
     * @return Компонент, отобрадающий абсолютный путь текущего открытого каталога.
     */
    public TextField getCurrentDirView() {
        return currentDirView;
    }

    /**
     * Получение абсолютного пути текущего открытого каталога.
     * @return Абсолютный путь текущего открытого каталога.
     */
    public Path getDirectory() {
        return currentDirectory.toPath();
    }

    /**
     * Получение текущего выделенного файла в открытом каталоге.
     * @return Текущий выделенный файл в открытом каталоге.
     */
    public Path getSelection() {
        String item = getSelectionModel().getSelectedItem();
        System.out.println(item);
        return item != null && item != ".." ? currentDirectory.toPath().resolve(getSelectionModel().getSelectedItem()) : null;
    }

    /**
     * Возврат к родительскому каталогу.
     */
    private void back() {
        File parentDirectory = currentDirectory.getParentFile();
        if (parentDirectory == null) {
            return;
        }
        currentDirectory = parentDirectory;
        refresh();
    }

    /**
     * Открытие выбранного каталога или открытие файла при помощи средств операционной системы.
     */
    private void navigate() {
        File selectedItem = new File(currentDirectory.toPath() + File.separator + getSelectionModel().getSelectedItem());
        if (selectedItem.isDirectory()) {
            currentDirectory = selectedItem;
            refresh();
        } else {
            try {
                Desktop.getDesktop().open(selectedItem);
            } catch (Exception e) {
                DialogsToShow.showException(e);
            }
        }
    }

    /**
     * Создание панели для работы с файловой системой.
     * @param path Путь открываемого по умолчанию каталога при запуске приложения.
     */
    public FilesView(String path) {
        super();
        currentDirectory = new File(path);
        filesList = FXCollections.observableArrayList();
        setItems(filesList);
        currentDirView = new TextField();
        currentDirView.setEditable(false);

        setOnMouseClicked(mouse -> {
            if (mouse.getButton().equals(MouseButton.PRIMARY) && mouse.getClickCount() == 2) {
                String filename = getSelectionModel().getSelectedItem();
                if (filename == "..") {
                    back();
                } else {
                    navigate();
                }
            }
        });
        refresh();
    }
}
