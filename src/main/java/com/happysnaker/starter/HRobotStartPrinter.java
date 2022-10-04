package com.happysnaker.starter;

import com.happysnaker.config.RobotConfig;

/**
 * @author Happysnaker
 * @description
 * @date 2022/2/27
 * @email happysnaker@foxmail.com
 */
public class HRobotStartPrinter {
    static String banner = "        ,--,                                                    \n" +
            "      ,--.'|,-.----.                                    ___     \n" +
            "   ,--,  | :\\    /  \\             ,---,               ,--.'|_   \n" +
            ",---.'|  : ';   :    \\   ,---.  ,---.'|      ,---.    |  | :,'  \n" +
            "|   | : _' ||   | .\\ :  '   ,'\\ |   | :     '   ,'\\   :  : ' :  \n" +
            ":   : |.'  |.   : |: | /   /   |:   : :    /   /   |.;__,'  /   \n" +
            "|   ' '  ; :|   |  \\ :.   ; ,. ::     |,-..   ; ,. :|  |   |    \n" +
            "'   |  .'. ||   : .  /'   | |: :|   : '  |'   | |: ::__,'| :    \n" +
            "|   | :  | ';   | |  \\'   | .; :|   |  / :'   | .; :  '  : |__  \n" +
            "'   : |  : ;|   | ;\\  \\   :    |'   : |: ||   :    |  |  | '.'| \n" +
            "|   | '  ,/ :   ' | \\.'\\   \\  / |   | '/ : \\   \\  /   ;  :    ; \n" +
            ";   : ;--'  :   : :-'   `----'  |   :    |  `----'    |  ,   /  \n" +
            "|   ,/      |   |.'             /    \\  /              ---`-'       \n" +
            "'---'       `---'               `-'----'                            v" + RobotConfig.CURRENT_VERSION;

    public static void printBanner() {
        System.out.println(banner);
    }
}
