package me.devwckd.libraries.core.query_loader.manager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devwckd.libraries.core.dependency.manager.DependencyManager;
import me.devwckd.libraries.core.query_loader.Queries;
import me.devwckd.libraries.core.utils.filename_filter.ExtensionFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * @author devwckd
 */

@Getter
@RequiredArgsConstructor
public class QueryLoaderManager {

    private static final FilenameFilter EXTENSION_FILTER = new ExtensionFilter("sql");

    private final DependencyManager dependencyManager;
    private final Object instance;

    private final Queries queries = new Queries();

    public void load() {
        final URL queriesUrl = instance.getClass().getClassLoader().getResource("queries");
        if(queriesUrl == null) return;

        final File queriesFile = new File(queriesUrl.getPath());
        loadFolder(queriesFile);
    }

    private void loadFolder(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) loadFolder(file);
            if (!EXTENSION_FILTER.accept(folder, file.getName())) continue;

            final List<File> parents = new ArrayList<>();
            File parent = file.getParentFile();
            while (parent != null && !parent.equals(file)) {
                parents.add(parent);
                parent = parent.getParentFile();
            }

            final StringBuilder nameBuilder = new StringBuilder();
            for (int i = parents.size() - 1; i < 0; i++) {
                final File listFile = parents.get(i);
                if (listFile == null) continue;
                nameBuilder.append(listFile.getName()).append(".");
            }

            final StringBuilder queryBuilder = new StringBuilder();
            try {
                final Scanner scanner = new Scanner(file);
                while(scanner.hasNextLine()) {
                    queryBuilder.append(scanner.nextLine().trim()).append(" ");
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                return;
            }

            queries.store(nameBuilder.toString(), queryBuilder.toString().trim());
        }
    }

}
