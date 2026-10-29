package seedu.address.commons.util;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Writes and reads files
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";
    private static final String BYTE_ORDER_MARK = "\uFEFF";

    /**
     * Creates a file if it does not exist along with its missing parent directories.
     * @throws IOException if the file or directory cannot be created.
     */
    public static void createIfMissing(Path file) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        createParentDirsOfFile(file);

        Files.createFile(file);
    }

    /**
     * Creates parent directories of file if it has a parent directory
     */
    private static void createParentDirsOfFile(Path file) throws IOException {
        Path parentDir = file.getParent();

        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Assumes file exists
     */
    public static String readFromFile(Path file) throws IOException {
        String content = new String(Files.readAllBytes(file), CHARSET);
        // some editors, such as Notepad, start a UTF-8 file with an invisible byte order mark
        return content.startsWith(BYTE_ORDER_MARK) ? content.substring(BYTE_ORDER_MARK.length()) : content;
    }

    /**
     * Writes given string to a file.
     * Will create the file if it does not exist yet.
     * The string is first written to a temporary file in the same folder, which then replaces {@code file},
     * so a write that fails, e.g. because the disk is full, leaves the old content of {@code file} unchanged.
     *
     * @throws AccessDeniedException if {@code file} exists but cannot be written to, e.g. because it is read-only.
     */
    public static void writeToFile(Path file, String content) throws IOException {
        if (Files.exists(file) && !Files.isWritable(file)) {
            // some systems allow replacing a read-only file, but its content must not change
            throw new AccessDeniedException(file.toString());
        }
        byte[] bytes = content.getBytes(CHARSET);
        Path tempFile = Files.createTempFile(file.toAbsolutePath().getParent(), file.getFileName().toString(), ".tmp");
        try {
            Files.write(tempFile, bytes);
            replaceFile(file, tempFile, bytes);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * Replaces {@code file} with {@code tempFile} in one step. If that is not possible, e.g. because another program
     * is reading {@code file} on Windows, writes {@code bytes} straight to {@code file} instead.
     */
    private static void replaceFile(Path file, Path tempFile, byte[] bytes) throws IOException {
        try {
            Files.move(tempFile, file, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            Files.write(file, bytes);
        }
    }

}
