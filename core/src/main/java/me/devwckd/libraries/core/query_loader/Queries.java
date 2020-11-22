package me.devwckd.libraries.core.query_loader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author devwckd
 */
public class Queries {

    private final Map<String, String> queryMap = new HashMap<>();

    public void store(String name, String query) {
        queryMap.put(name.toLowerCase(), query);
    }

    public String get(String name) {
        return queryMap.get(name);
    }

}
