package me.devwckd.libraries.core.utils.filename_filter;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author devwckd
 */
@RequiredArgsConstructor
public class ExtensionFilter implements FilenameFilter {

    private final String extension;

    @Override
    public boolean accept(File dir, String name) {
        return name.length() >= extension.length() + 2 && name.endsWith("." + extension);
    }

}
