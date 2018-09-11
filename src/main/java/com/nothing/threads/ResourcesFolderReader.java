package com.nothing.threads;

import com.nothing.Main;

import java.io.IOException;
import java.nio.file.*;

import static com.sun.nio.file.ExtendedWatchEventModifier.FILE_TREE;
import static java.io.File.separator;
import static java.nio.file.StandardWatchEventKinds.*;

public class ResourcesFolderReader implements Runnable {

    @Override
    public void run() {
        FileSystem fs = FileSystems.getDefault();
        WatchService ws = null;
        try {
            ws = fs.newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path pTemp = Paths.get(System.getProperty("user.dir") + separator + "src" + separator + "main" + separator + "java" + separator + "resources");
        try {
            pTemp.register(ws, new WatchEvent.Kind[]{ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE}, FILE_TREE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            WatchKey k = null;
            try {
                k = ws.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (WatchEvent<?> e : k.pollEvents()) {
                Object c = e.context();
                System.out.printf("%s %d %s %s\n", e.kind(), e.count(), e.context(), c);
                if (e.kind() == ENTRY_CREATE || e.kind() == ENTRY_MODIFY) {
                    Main.queue.add(c);
                }
            }
            k.reset();
        }
    }
}
