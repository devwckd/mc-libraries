package me.devwckd.libraries.spigot.plugin;

import lombok.Getter;
import me.devwckd.libraries.core.manager.AdapterManager;
import me.devwckd.libraries.core.manager.DependencyManager;
import me.devwckd.libraries.core.manager.ListenerManager;
import me.devwckd.libraries.core.manager.ModuleManager;
import me.devwckd.libraries.core.manager.QueryLoaderManager;
import me.devwckd.libraries.core.manager.SbcfHookManager;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author devwckd
 */

@Getter
public class LibraryPlugin extends JavaPlugin {

    private final String packagePrefix = getClass().getPackage().getName();

    private DependencyManager dependencyManager;
    private QueryLoaderManager queryLoaderManager;
    private AdapterManager adapterManager;
    private ModuleManager moduleManager;
    private ListenerManager listenerManager;

    private BukkitFrame bukkitFrame;
    private SbcfHookManager sbcfHookManager;

    private boolean isShutdown = false;

    @Override
    public final void onLoad() {
        preLoad();

        if(isShutdown) return;
        load();

        if(isShutdown) return;
        moduleManager.load();
    }

    @Override
    public final void onEnable() {
        if(isShutdown) return;
        enable();

        if(isShutdown) return;
        moduleManager.enable();

        if(isShutdown) return;
        postEnable();
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

    public void performShutdown() {
        this.isShutdown = true;
        getServer().getPluginManager().disablePlugin(this);
    }

    private void preLoad() {
        initDependencyManager();
        initQueryLoader();
        initAdapterManager();
        initModuleManager();
    }

    private void postEnable() {
        initListenerManager();
        initSbcfHook();
    }

    private void initDependencyManager() {
        dependencyManager = new DependencyManager();
        dependencyManager.storeLoadedDependency(this);
    }

    private void initQueryLoader() {
        queryLoaderManager = new QueryLoaderManager(this);
        queryLoaderManager.init();
        dependencyManager.storeLoadedDependency(queryLoaderManager.getQueries());
    }

    private void initAdapterManager() {
        adapterManager = new AdapterManager(dependencyManager, packagePrefix);
        dependencyManager.storeLoadedDependency(adapterManager.getAdapters());
        adapterManager.load();
    }

    private void initModuleManager() {
        moduleManager = new ModuleManager(dependencyManager, packagePrefix, this::isShutdown);
        moduleManager.init();
    }

    private void initListenerManager() {
        listenerManager = new ListenerManager(dependencyManager, packagePrefix);
        listenerManager.load(listenerInstance -> getServer().getPluginManager().registerEvents((Listener) listenerInstance, this));
    }

    private void initSbcfHook() {
        bukkitFrame = new BukkitFrame(this);
        sbcfHookManager = new SbcfHookManager(dependencyManager, packagePrefix);
        sbcfHookManager.load(bukkitFrame::registerCommands);
    }

}
