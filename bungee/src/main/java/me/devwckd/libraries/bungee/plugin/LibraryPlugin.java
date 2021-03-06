package me.devwckd.libraries.bungee.plugin;


import lombok.Getter;
import me.devwckd.libraries.core.manager.*;
import me.devwckd.libraries.core.utils.LibraryLogger;
import me.saiintbrisson.bungee.command.BungeeFrame;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author devwckd
 */

@Getter
public class LibraryPlugin extends Plugin {

    final String packagePrefix = getClass().getPackage().getName();

    private DependencyManager dependencyManager;
    private QueryLoaderManager queryLoaderManager;
    private AdapterManager adapterManager;
    private ModuleManager moduleManager;
    private ListenerManager listenerManager;

    private BungeeFrame bungeeFrame;
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
    }

    private void preLoad() {
        initBungeeFrame();
        initDependencyManager();
        initLogger();
        initQueryLoader();
        initAdapterManager();
        initModuleManager();
    }

    private void postEnable() {
        initListenerManager();
        initSbcfHook();
    }

    private void initBungeeFrame() {
        bungeeFrame = new BungeeFrame(this);
    }

    private void initDependencyManager() {
        dependencyManager = new DependencyManager();
        dependencyManager.storeLoadedDependency(this);
    }

    private void initLogger() {
        final LibraryLogger libraryLogger = new LibraryLogger(getProxy().getLogger(), getDescription().getName());
        dependencyManager.storeLoadedDependency(libraryLogger);
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
        moduleManager.loadExports(this);
        moduleManager.init();
    }

    private void initListenerManager() {
        listenerManager = new ListenerManager(dependencyManager, packagePrefix);
        listenerManager.load(listenerInstance -> getProxy().getPluginManager().registerListener(this, (Listener) listenerInstance));
    }

    private void initSbcfHook() {
        sbcfHookManager = new SbcfHookManager(dependencyManager, packagePrefix);
        sbcfHookManager.load(bungeeFrame::registerCommands);
    }


}
