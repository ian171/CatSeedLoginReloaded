package cc.baka9.catseedlogin.bukkit.command;

import cc.baka9.catseedlogin.bukkit.CatSeedLogin;
import cc.baka9.catseedlogin.bukkit.Config;
import cc.baka9.catseedlogin.bukkit.database.Cache;
import cc.baka9.catseedlogin.bukkit.event.CatSeedPlayerRegisterEvent;
import cc.baka9.catseedlogin.bukkit.object.LoginPlayer;
import cc.baka9.catseedlogin.bukkit.object.LoginPlayerHelper;
import cc.baka9.catseedlogin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

public class CommandRegister implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] args){
        if (args.length != 2) return false;
        Player player = (Player) sender;
        if(handleRegister(player,args)){
            sender.sendMessage(Config.Language.REGISTER_SUCCESS);
        }else {
            sender.sendMessage("注册时出现问题");
            return false;
        }
        return true;
        //handleRegister(player,args);
//        if (LoginPlayerHelper.isLogin(name)) {
//            sender.sendMessage(Config.Language.REGISTER_AFTER_LOGIN_ALREADY);
//            return true;
//        }
//        if (LoginPlayerHelper.isRegister(name)) {
//            sender.sendMessage(Config.Language.REGISTER_BEFORE_LOGIN_ALREADY);
//            return true;
//        }
//        if (!args[0].equals(args[1])) {
//            sender.sendMessage(Config.Language.REGISTER_PASSWORD_CONFIRM_FAIL);
//            return true;
//        }
//        if (!Util.passwordIsDifficulty(args[0])) {
//            sender.sendMessage(Config.Language.COMMON_PASSWORD_SO_SIMPLE);
//            return true;
//        }
//        if (!Cache.isLoaded) {
//            return true;
//        }


//        CatSeedLogin.instance.runTaskAsync(() -> {
//            try {
//                String currentIp = player.getAddress().getAddress().getHostAddress();
//                List<LoginPlayer> LoginPlayerListlikeByIp = CatSeedLogin.sql.getLikeByIp(currentIp);
//                if (LoginPlayerListlikeByIp.size() >= Config.Settings.IpRegisterCountLimit) {
//                    sender.sendMessage(Config.Language.REGISTER_MORE
//                            .replace("{count}", String.valueOf(LoginPlayerListlikeByIp.size()))
//                            .replace("{accounts}", String.join(", ", LoginPlayerListlikeByIp.stream().map(LoginPlayer::getName).toArray(String[]::new))));
//                } else {
//                    LoginPlayer lp = new LoginPlayer(name, args[0]);
//                    lp.crypt();
//                    CatSeedLogin.sql.add(lp);
//                    LoginPlayerHelper.add(lp);
//                    Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
//                        CatSeedPlayerRegisterEvent event = new CatSeedPlayerRegisterEvent(Bukkit.getPlayer(sender.getName()));
//                        Bukkit.getServer().getPluginManager().callEvent(event);
//                    });
//                    sender.sendMessage(Config.Language.REGISTER_SUCCESS);
//                    player.updateInventory();
//                    LoginPlayerHelper.recordCurrentIP(player, lp);
//                }
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                sender.sendMessage("§c服务器内部错误!");
//            }
//        });


    }

    /**
     *
     * @param p player
     * @param passwd password
     * @return true:处理失败
     *  false:处理成功
     */
    public static boolean handleRegister(Player p, String[] passwd){
        if (LoginPlayerHelper.isLogin(p.getName())) {
            p.sendMessage(Config.Language.REGISTER_AFTER_LOGIN_ALREADY);
            return false;
        }
        if (LoginPlayerHelper.isRegister(p.getName())) {
            p.sendMessage(Config.Language.REGISTER_BEFORE_LOGIN_ALREADY);
            return false;
        }
        if (!passwd[0].equals(passwd[1])) {
            p.sendMessage(Config.Language.REGISTER_PASSWORD_CONFIRM_FAIL);
            return false;
        }
        if (!Util.passwordIsDifficulty(passwd[0])) {
            p.sendMessage(Config.Language.COMMON_PASSWORD_SO_SIMPLE);
            return false;
        }
        if (!Cache.isLoaded) {
            return false;
        }
        p.sendMessage("§e注册中..");
        CatSeedLogin.instance.runTaskAsync(() -> {
            try {
                String currentIp = p.getAddress().getAddress().getHostAddress();
                List<LoginPlayer> LoginPlayerListlikeByIp = CatSeedLogin.sql.getLikeByIp(currentIp);
                if (LoginPlayerListlikeByIp.size() >= Config.Settings.IpRegisterCountLimit) {
                    p.sendMessage(Config.Language.REGISTER_MORE
                            .replace("{count}", String.valueOf(LoginPlayerListlikeByIp.size()))
                            .replace("{accounts}", String.join(", ", LoginPlayerListlikeByIp.stream().map(LoginPlayer::getName).toArray(String[]::new))));
                } else {
                    LoginPlayer lp = new LoginPlayer(p.getName(), passwd[0]);
                    lp.crypt();
                    CatSeedLogin.sql.add(lp);
                    LoginPlayerHelper.add(lp);
                    Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
                        CatSeedPlayerRegisterEvent event = new CatSeedPlayerRegisterEvent(Bukkit.getPlayer(p.getName()));
                        Bukkit.getServer().getPluginManager().callEvent(event);
                    });
                    p.sendMessage(Config.Language.REGISTER_SUCCESS);
                    p.updateInventory();
                    LoginPlayerHelper.recordCurrentIP(p, lp);
                }

            } catch (Exception e) {
                CatSeedLogin.instance.getLogger().log(Level.SEVERE, "[CatSeedLogin]: ", e);
                p.sendMessage("§c服务器内部错误!");
            }
        });
        return true;
    }
}
