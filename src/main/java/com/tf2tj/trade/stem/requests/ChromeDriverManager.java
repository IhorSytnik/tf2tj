package com.tf2tj.trade.stem.requests;

import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ChromeDriverManager {

    public static void updateDriver(String version, boolean followRedirects, long sleepMilliseconds) throws InterruptedException, IOException {
        HttpRequestBrowser chromedriverBrowser = new HttpRequestBrowser(followRedirects, sleepMilliseconds);

        Path downloadPath = Path.of("chromedriver-win64.zip");
        Path extractPath = Path.of("chromedriver.exe");
//        String latestStableRelease =
//                chromedriverBrowser.getSource(
//                        "https://googlechromelabs.github.io/chrome-for-testing/LATEST_RELEASE_STABLE");
        chromedriverBrowser.downloadGet(
                "https://storage.googleapis.com/chrome-for-testing-public/%s/win64/chromedriver-win64.zip"
                        .formatted(version),
                new HttpHeaders(),
                downloadPath);
        extractFile(
                downloadPath,
                "chromedriver-win64\\chromedriver.exe",
                extractPath);
    }

    private static void extractFile(Path zipFile, String fileName, Path outputFile) throws IOException {
        // Wrap the file system in a try-with-resources statement
        // to auto-close it when finished and prevent a memory leak
        try (FileSystem fileSystem = FileSystems.newFileSystem(zipFile, Map.of())) {
            Path fileToExtract = fileSystem.getPath(fileName);
            Files.copy(fileToExtract, outputFile, REPLACE_EXISTING);
        }
    }
}
