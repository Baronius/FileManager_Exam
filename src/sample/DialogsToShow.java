package sample;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

/**
 * Класс, реализующий различные диалоговые окна в виде функций.
 */
public class DialogsToShow {
    /**
     * Отображение ошибки.
     * @param errorMessage Сообщение с ошибкой.
     */
    public static void showError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    /**
     * Отображение исключения.
     * @param e Исключение.
     */
    public static void showException(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(e.toString());
        alert.showAndWait();
    }

    /**
     * Отображение диалога для ввода названия создаваемого файла/каталога.
     * @param title Название диалогового окна.
     * @return Название создаваемого файла/каталога.
     */
    public static String showInputDialog(String title) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle(title);
        dialog.setHeaderText("Введите название.");
        Optional<String> text = dialog.showAndWait();
        String result = text.isPresent() ? text.get() : null;

        return result;
    }

    /**
     * Отображение диалога для подтверждения удаления файла/каталога.
     * @param filename Имя удаляемого файла/каталога.
     * @return Результат с решением пользователя (если истина, то файл/каталог будет удален).
     */
    public static boolean showDeleteConfirmationDialog(String filename) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление файла");
        alert.setHeaderText("Подтвердите свое действие.");
        alert.setContentText("Вы хотите удалить " + filename + "?");
        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;
    }
}
