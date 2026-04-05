package me.TyAlternative.com.nocturne;

import me.TyAlternative.com.nocturne.core.NocturneGame;
import me.TyAlternative.com.nocturne.role.RoleRegistry;
import org.bukkit.plugin.java.JavaPlugin;

public final class Nocturne extends JavaPlugin {
    private static Nocturne instance = new Nocturne();

    @Override
    public void onEnable() {
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }



    public NocturneGame getGame() {
        return new NocturneGame(new RoleRegistry(getLogger()), getLogger());
    }

    public static Nocturne getInstance() {
        return instance;
    }

}
