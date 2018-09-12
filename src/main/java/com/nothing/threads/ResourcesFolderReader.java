package com.nothing.threads;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.nothing.Main;

public class ResourcesFolderReader implements Runnable {

	@Override
	public void run() {
		FileSystem fs = FileSystems.getDefault();
		WatchService ws = null;
		try {
			ws = fs.newWatchService();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		Path pTemp = Paths.get("urls");
		try {
			pTemp.register(ws, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			WatchKey k = null;
			try {
				k = ws.take();
			}
			catch (InterruptedException e) {
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
