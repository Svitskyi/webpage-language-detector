package com.nothing;


import com.nothing.threads.LanguageParserWorker;
import com.nothing.threads.ResourcesFolderReader;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static java.io.File.separator;

public class Main {

    public static LinkedBlockingQueue<Object> queue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws InterruptedException {
        ResourcesFolderReader resourcesFolderReader = new ResourcesFolderReader();
        new Thread(resourcesFolderReader).start();
        Language[] languages = Language.values();
        final ExecutorService executorService = Executors.newFixedThreadPool(languages.length);
        final ExecutorCompletionService<Boolean> completionService = new ExecutorCompletionService<>(executorService);
        while (true) {

            Object take = queue.take();
            String fileName = take.toString();
            System.out.println(queue.size());
            for (int i = 0; i < languages.length; i++) {
                File file = new File(System.getProperty("user.dir") + separator + "src" + separator + "main" + separator + "java" + separator + "resources" + separator + fileName);
                try {
                    List<String> strings = IOUtils.readLines(new FileInputStream(file), StandardCharsets.UTF_8);
                    int finalI = i;
                    strings.forEach(s -> completionService.submit(new LanguageParserWorker(String.format(s, languages[finalI].name().toLowerCase()), languages[finalI])));
                    queue.remove(take);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
