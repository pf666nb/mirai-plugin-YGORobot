package com.happysnaker;

import com.happysnaker.config.RobotConfig;
import com.happysnaker.exception.CanNotSendMessageException;
import com.happysnaker.starter.HRobotStarter;
import com.happysnaker.starter.HRobotVersionChecker;
import com.happysnaker.starter.Patch;
import com.happysnaker.utils.RobotUtil;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import org.quartz.SchedulerException;

/**
 * @author happysnakers
 */
public final class Main extends JavaPlugin {
    public static final Main INSTANCE = new Main();

    private Main() {
        super(new JvmPluginDescriptionBuilder("com.happysnaker.HRobot", RobotConfig.CURRENT_VERSION)
                .name("HRobot")
                .author("Happysnaker")
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