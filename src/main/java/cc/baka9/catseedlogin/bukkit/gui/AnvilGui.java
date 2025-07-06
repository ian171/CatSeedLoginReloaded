package cc.baka9.catseedlogin.bukkit.gui;

import cc.baka9.catseedlogin.bukkit.CatSeedLogin;
import cc.baka9.catseedlogin.bukkit.CatSeedLoginAPI;
import cc.baka9.catseedlogin.bukkit.command.CommandRegister;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class AnvilGui {
    private final Logger logger = CatSeedLogin.instance.getLogger();
    private final HashMap<UUID, String> brandMap = new HashMap<>();
    private boolean isBe = false;
    public AnvilGui(Player player) {
        if(CatSeedLogin.loadProtocolLib){
            try {
                ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(CatSeedLogin.instance,
                        PacketType.Handshake.Client.SET_PROTOCOL) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        String channel = event.getPacket().getStrings().read(0);
                        if (channel.equalsIgnoreCase("MC|Brand") || channel.equalsIgnoreCase("minecraft:brand")) {
                            byte[] data = event.getPacket().getByteArrays().read(0);
                            String brand = new String(data);
                            brandMap.put(event.getPlayer().getUniqueId(), brand);
                        }
                    }
                });
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        if (brandMap.getOrDefault(player.getUniqueId(), "Unknown").equals("geyser")) {
            isBe = true;
        }
    }

    public void openLoginGui(Player p) {
        if(isBe) return;
        try {
            new AnvilGUI.Builder().plugin(CatSeedLogin.instance)
                    .itemLeft(new ItemStack(Material.FEATHER))
                    .itemLeft(new ItemStack(Material.DIAMOND))
                    .onClickAsync((slot,textbar)->{
                        if(slot == AnvilGUI.Slot.INPUT_LEFT){
                            if (!CatSeedLoginAPI.HandleLogin(p, new String[]{p.getName()})){
                                openLoginGui(p);
                                return null;
                            }
                        }
                        return CompletableFuture.completedFuture(Arrays.asList(AnvilGUI.ResponseAction.run(() -> {
                            // 完成时执行的代码
                            logger.info(p.getName() + " Done");
                        })));
                    })
                    .disableGeyserCompat()
                    .open(p)
                    .setTitle("Login",false);
        } catch (Exception e) {
            logger.info("AnvilGUI Error");
        }
        System.gc();
    }


    //内存不安全
    String[] passwd_temp = new String[2];
    int counter =0;

    public void openRegisterGui(Player p) {
        if(isBe) return;
        try {
            new AnvilGUI.Builder().plugin(CatSeedLogin.instance)

                    .itemRight(new ItemStack(Material.BRICK))
                    .itemLeft(new ItemStack(Material.DIAMOND))
                    .onClickAsync((slot,textbar)->{
                        if(counter == 1){
                            if(textbar.getText().equals(passwd_temp[0])){
                                CatSeedLoginAPI.handleRegister(p,passwd_temp);
                                passwd_temp = new String[2]; //清空
                            }
                        }
                        if(slot == AnvilGUI.Slot.OUTPUT){
                            counter = counter + 1;
                            passwd_temp[0] = textbar.getText();
                            openLoginGui(p);
                            counter = 0;
                        }
                        return CompletableFuture.completedFuture(Arrays.asList(AnvilGUI.ResponseAction.run(() -> {
                            // 完成时执行的代码
                            logger.info(p.getName() + " Done");
                        })));
                    })
                    .disableGeyserCompat()
                    .open(p)
                    .setTitle("Login",false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
