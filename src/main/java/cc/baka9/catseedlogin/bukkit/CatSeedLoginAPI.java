package cc.baka9.catseedlogin.bukkit;

import cc.baka9.catseedlogin.bukkit.command.CommandLogin;
import cc.baka9.catseedlogin.bukkit.command.CommandRegister;
import cc.baka9.catseedlogin.bukkit.database.Cache;
import cc.baka9.catseedlogin.bukkit.event.CatSeedPlayerLoginEvent;
import cc.baka9.catseedlogin.bukkit.object.LoginPlayer;
import cc.baka9.catseedlogin.bukkit.object.LoginPlayerHelper;
import cc.baka9.catseedlogin.util.Crypt;
import org.bukkit.entity.Player;

import java.util.Objects;

import static cc.baka9.catseedlogin.bukkit.command.CommandLogin.handleLogin;

public class CatSeedLoginAPI {
    public static boolean isLogin(String name){
        return LoginPlayerHelper.isLogin(name);
    }

    public static boolean isRegister(String name){
        return LoginPlayerHelper.isRegister(name);
    }

    public static boolean HandleLogin(Player player,String[] passwd){
        if (isLogin(player.getName())){
            return false;
        }
        LoginPlayer lp = Cache.getIgnoreCase(player.getName());
        if (Objects.equals(Crypt.encrypt(player.getName(),passwd[0]), lp.getPassword().trim())) {
            LoginPlayerHelper.add(lp);
            //CatSeedPlayerLoginEvent loginEvent = new CatSeedPlayerLoginEvent(player, lp.getEmail(), CatSeedPlayerLoginEvent.Result.SUCCESS);
        }
        CatSeedPlayerLoginEvent loginEvent = new CatSeedPlayerLoginEvent(player,lp.getEmail(),CatSeedPlayerLoginEvent.Result.SUCCESS);
        handleLogin(player, player, lp, loginEvent);
        return true;
    }
    public static boolean handleRegister(Player player,String[] passwd){
        if (isRegister(passwd[0])){
            return false;
        }
        return CommandRegister.handleRegister(player,passwd);
    }
}
