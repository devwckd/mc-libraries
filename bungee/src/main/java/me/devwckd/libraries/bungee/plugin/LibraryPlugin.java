package me.devwckd.libraries.bungee.plugin;


import lombok.Getter;
import me.devwckd.libraries.core.manager.AdapterManager;
import me.devwckd.libraries.core.manager.DependencyManager;
import me.devwckd.libraries.core.manager.ListenerManager;
import me.devwckd.libraries.core.manager.ModuleManager;
import me.devwckd.libraries.core.manager.QueryLoaderManager;
import me.devwckd.libraries.core.manager.SbcfHookManager;
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
        init();

        if(isShutdown) return;
        load();
        moduleManager.load();
    }

    @Override
    public final void onEnable() {
        if(isShutdown) return;
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

    public void performShutdown() {
        this.isShutdown = true;
    }

    private void init() {
        initDependencyManager();
        initQueryLoader();
        initAdapterManager();
        initModuleManager();
        initListenerManager();
        initSbcfHook();
    }

    private void initDependencyManager() {
        dependencyManager = new DependencyManager();
        dependencyManager.storeLoadedDependency(this);
    }

    private void initQueryLoader() {
        queryLoaderManager = new QueryLoaderManager(dependencyManager, this);
        queryLoaderManager.load();
        dependencyManager.storeLoadedDependency(queryLoaderManager.getQueries());
    }

    private void initAdapterManager() {
        adapterManager = new AdapterManager(dependencyManager, packagePrefix);
        adapterManager.load();
        dependencyManager.storeLoadedDependency(adapterManager.getAdapters());
    }

    private void initModuleManager() {
        moduleManager = new ModuleManager(dependencyManager, packagePrefix, this::isShutdown);
        moduleManager.init();
    }

    private void initListenerManager() {
        listenerManager = new ListenerManager(dependencyManager, packagePrefix);
        listenerManager.load(listenerInstance -> getProxy().getPluginManager().registerListener(this, (Listener) listenerInstance));
    }

    private void initSbcfHook() {
        bungeeFrame = new BungeeFrame(this);
        sbcfHookManager = new SbcfHookManager(dependencyManager, packagePrefix);
        sbcfHookManager.load(bungeeFrame::registerCommands);
    }


}
