package me.devwckd.libraries.core.manager;

import lombok.Getter;
import me.devwckd.libraries.core.common.Queries;
import org.reflections8.Reflections;
import org.reflections8.scanners.ResourcesScanner;
import org.reflections8.util.ConfigurationBuilder;

import java.io.InputStream;
import java.util.Set;

/**
 * @author devwckd
 */

@Getter
public class QueryLoaderManager {

    private final Object instance;
    private final Queries queries;

    public QueryLoaderManager(Object instance) {
        this.instance = instance;
        this.queries = new Queries();
    }

    public void init() {
        final Reflections reflections = new Reflections(
          new ConfigurationBuilder()
            .forPackages(instance.getClass().getPackage().getName())
            .addScanners(new ResourcesScanner())
        );

        final Set<String> resourceNames = reflections.getResources(s -> s.endsWith(".sql"));

        for (String resourceName : resourceNames) {

            try (final InputStream inputStream = instance.getClass().getClassLoader().getResourceAsStream(resourceName)) {
                if (inputStream == null)
                    return;
                final StringBuilder stringBuffer = new StringBuilder();

                int c;
                while ((c = inputStream.read()) != -1) {
                    stringBuffer.append((char) c);
                }

                queries.store(
                  resourceName
                    .replace("queries/", "")
                    .replace(".sql", "")
                    .replace("/", ".")
                    .trim(),
                  stringBuffer.toString()
                );
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

    }
}

