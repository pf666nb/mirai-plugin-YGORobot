package com.happysnaker;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.starter.HRobotStarter;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

/**
 * @author happysnakers
 */

public final class Main extends JavaPlugin {
    public static final Main INSTANCE = new Main();

    private Main() {
        super(new JvmPluginDescriptionBuilder("com.happysnaker.HRobot", RobotConfig.CURRENT_VERSION)
                .name("零依网络")
                .author("本初")
                .build());
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
