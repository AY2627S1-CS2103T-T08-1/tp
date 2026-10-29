package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileUtilTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void writeToFile_existingFile_replacesContentWithoutLeavingTemporaryFile() throws IOException {
        Path file = temporaryFolder.resolve("data.json");
        Files.writeString(file, "old content that is longer than the new content");

        FileUtil.writeToFile(file, "new");

        assertEquals("new", FileUtil.readFromFile(file));
        assertEquals(List.of(file), listFolder());
    }

    @Test
    public void writeToFile_missingFile_createsFile() throws IOException {
        Path file = temporaryFolder.resolve("data.json");

        FileUtil.writeToFile(file, "new");

        assertEquals("new", FileUtil.readFromFile(file));
    }

    @Test
    public void writeToFile_fileOpenForReading_replacesContent() throws IOException {
        Path file = temporaryFolder.resolve("data.json");
        Files.writeString(file, "old");

        // on Windows, an open file cannot be replaced by another file, so the content is written straight to it
        try (InputStream openFile = Files.newInputStream(file)) {
            FileUtil.writeToFile(file, "new");
        }

        assertEquals("new", FileUtil.readFromFile(file));
        assertEquals(List.of(file), listFolder());
    }

    @Test
    public void writeToFile_readOnlyFile_throwsAccessDeniedExceptionAndKeepsContent() throws IOException {
        Path file = temporaryFolder.resolve("data.json");
        Files.writeString(file, "old");
        assertTrue(file.toFile().setWritable(false));

        try {
            assertThrows(AccessDeniedException.class, () -> FileUtil.writeToFile(file, "new"));
            assertEquals("old", FileUtil.readFromFile(file));
        } finally {
            file.toFile().setWritable(true);
        }
        assertEquals(List.of(file), listFolder());
    }

    @Test
    public void writeToFile_fileCannotBeReplaced_throwsIoExceptionWithoutLeavingTemporaryFile() throws IOException {
        // a folder that is not empty cannot be replaced by a file
        Path folder = Files.createDirectory(temporaryFolder.resolve("data.json"));
        Path fileInFolder = Files.writeString(folder.resolve("kept.txt"), "old");

        assertThrows(IOException.class, () -> FileUtil.writeToFile(folder, "new"));

        assertEquals("old", FileUtil.readFromFile(fileInFolder));
        assertEquals(List.of(folder), listFolder());
    }

    private List<Path> listFolder() throws IOException {
        try (Stream<Path> files = Files.list(temporaryFolder)) {
            return files.toList();
        }
    }
}
