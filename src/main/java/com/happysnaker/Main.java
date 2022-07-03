package com.happysnaker;

import com.happysnaker.starter.HRobotStarter;
import com.happysnaker.starter.Patch;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

/**
 * @author happysnakers
 */
public final class Main extends JavaPlugin {
    public static final Main INSTANCE = new Main();

    private Main() {
        super(new JvmPluginDescriptionBuilder("com.happysnaker.HRobot", "1.0.0")
                .name("HRobot")
                .author("Happysnaker")
                .build());
        Patch.patch();
    }

    @Override
    public void onEnable() {
        try {
            HRobotStarter.Start(this);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(500);
        }
    }
}