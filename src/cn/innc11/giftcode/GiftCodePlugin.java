package cn.innc11.giftcode;

import cn.innc11.giftcode.config.GiftCodeSetConfig;
import cn.innc11.giftcode.config.GiftsConfig;
import cn.innc11.giftcode.dt.Codes;
import cn.innc11.giftcode.dt.Gift;
import cn.innc11.giftcode.form.MainPanel;
import cn.innc11.giftcode.form.RedeemCodePanel;
import cn.innc11.giftcode.listener.FormResponseListener;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerToggleSprintEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.TextFormat;

import java.util.HashMap;
import java.util.UUID;

public class GiftCodePlugin extends PluginBase implements Listener
{
    public static GiftCodePlugin ins;
    public HashMap<UUID, Codes> codes = new HashMap<>();
    public HashMap<UUID, Gift> gifts = new HashMap<>();
    public HashMap<String, String> inputCache = new HashMap<>();
    GiftsConfig giftsConfig;
    GiftCodeSetConfig giftCodeSetConfig;
    public String charPool;

    public void loadConfig()
    {
        loadGiftsConfig();
        loadGiftCodeSetConfig();
        getLogger().info(TextFormat.colorize("Loaded Gifts: &e" + gifts.size()));
        getLogger().info(TextFormat.colorize("Loaded GiftCodeSet: &e" + codes.size()));

        getConfig().reload();
        charPool = getConfig().getString("charPool", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");

        getLogger().info(TextFormat.colorize("Loaded CharPool size: &e" + charPool.length()));
    }

    public void saveGiftsConfig()
    {
        giftsConfig.save();
    }

    public void loadGiftsConfig()
    {
        giftsConfig.reload();
    }

    public void saveGiftCodeSetConfig()
    {
        giftCodeSetConfig.save();
    }

    public void loadGiftCodeSetConfig()
    {
        giftCodeSetConfig.reload();
    }

    void registerEvents()
    {
        getServer().getPluginManager().registerEvents(new FormResponseListener(), this);
    }

    public void onEnable()
    {
        ins = this;
        giftsConfig = new GiftsConfig();
        giftCodeSetConfig = new GiftCodeSetConfig();
        saveDefaultConfig();
        loadConfig();
        registerEvents();
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void onPlayerSnake(PlayerToggleSprintEvent e)
    {
        if (e.getPlayer().isSprinting())
        {
            e.getPlayer().showFormWindow(new MainPanel());
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            String code = "";

            if(args.length>=1)
            {
                code = args[0].trim();
            }

            if(!code.isEmpty())
            {
                RedeemCodePanel rcp = new RedeemCodePanel((Player) sender, code);

                ((Player) sender).showFormWindow(rcp);
                return true;
            }

            if (sender.isOp())
            {
                ((Player) sender).showFormWindow(new MainPanel());
            } else {
                ((Player) sender).showFormWindow(new RedeemCodePanel((Player) sender, ""));
            }
        }
        return true;
    }

    public Gift getGiftWithUUID(String uuid)
    {
        return getGiftWithUUID(UUID.fromString(uuid));
    }

    public Gift getGiftWithUUID(UUID uuid)
    {
        return gifts.get(uuid);
    }

    public Gift getGiftWithLable(String lable)
    {
        for (Gift gift : gifts.values())
        {
            if (gift.label.equals(lable))
            {
                return gift;
            }
        }
        return null;
    }

    public boolean removeGiftWithUUID(String uuid)
    {
        return removeGiftWithUUID(UUID.fromString(uuid));
    }

    public boolean removeGiftWithUUID(UUID uuid)
    {
        return (gifts.remove(uuid) != null);
    }

    public void addGift(Gift gift)
    {
        gifts.put(gift.uuid, gift);
    }

    public Codes getCodesWithUUID(String uuid)
    {
        return getCodesWithUUID(UUID.fromString(uuid));
    }

    public Codes getCodesWithUUID(UUID uuid)
    {
        return codes.get(uuid);
    }

    public boolean removeCodeSetWithUUID(String uuid)
    {
        return removeCodeSetWithUUID(UUID.fromString(uuid));
    }

    public boolean removeCodeSetWithUUID(UUID uuid)
    {
        return (codes.remove(uuid) != null);
    }

    public Codes getCodeSetWithGiftCode(String giftCode)
    {
        for (Codes codeSet : codes.values())
        {
            if (codeSet.isOneTime)
            {
                if (codeSet.codes.containsKey(giftCode))
                    return codeSet;
                continue;
            }
            if (codeSet.publicGiftCode.equals(giftCode))
                return codeSet;
        }
        return null;
    }

    public void addCodeSet(Codes codeSet)
    {
        codes.put(codeSet.uuid, codeSet);
    }

    public void sendTitleMessage(Player player, String text, final Runnable task)
    {
        player.sendTitle(TextFormat.colorize(text));
        player.sendMessage(TextFormat.colorize(text));
        getServer().getScheduler().scheduleDelayedTask(new PluginTask<GiftCodePlugin>(this)
        {
            public void onRun(int currentTick)
            {
                if (task != null)
                    task.run();
            }
        }, 60);
    }
}
