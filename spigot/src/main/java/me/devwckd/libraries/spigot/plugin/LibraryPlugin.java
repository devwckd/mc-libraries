package me.devwckd.libraries.spigot.plugin;

import lombok.Getter;
import me.devwckd.libraries.core.adapter.entity.adapter.Adapter;
import me.devwckd.libraries.core.adapter.manager.AdapterManager;
import me.devwckd.libraries.core.dependency.manager.DependencyManager;
import me.devwckd.libraries.core.module.manager.ModuleManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author devwckd
 */

@Getter
public class LibraryPlugin extends JavaPlugin {

    private DependencyManager dependencyManager;
    private AdapterManager adapterManager;
    private ModuleManager moduleManager;

    @Override
    public final void onLoad() {
        initVariables();

        load();
        moduleManager.load();
    }

    @Override
    public final void onEnable() {
        enable();
        moduleManager.enable();
    }

    @Override
    public final void onDisable() {
        moduleManager.disable();
        disable();
    }

    public void load() { }
    public void enable() { }
    public void disable() { }
    public void reload() { }

    public void performReload() {
        reload();
        moduleManager.reload();
    }

    private void initVariables() {
        final String packagePrefix = getClass().getPackage().getName();

        dependencyManager = new DependencyManager();
        dependencyManager.storeLoadedDependency(this);

        adapterManager = new AdapterManager(dependencyManager, packagePrefix);
        dependencyManager.storeLoadedDependency(adapterManager.getAdapters());

        moduleManager = new ModuleManager(dependencyManager, packagePrefix);
        moduleManager.search();
        moduleManager.instantiate();
    }

}