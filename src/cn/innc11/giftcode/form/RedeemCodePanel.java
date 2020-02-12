package cn.innc11.giftcode.form;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.innc11.giftcode.dt.Codes;
import cn.innc11.giftcode.dt.Gift;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;

public class RedeemCodePanel extends FormWindowCustom implements FormResponse
{
    public RedeemCodePanel(Player player, String defaultCode)
    {
        super("礼包码使用面板");

        String defText = defaultCode;

        if (GiftCodePlugin.ins.inputCache.containsKey(player.getName()) && defaultCode.isEmpty())
        {
            defText = GiftCodePlugin.ins.inputCache.get(player.getName());
        }

        addElement(new ElementInput("礼包码", "在这里输入礼包码", defText));
    }

    @Override
    public void onFormClose(PlayerFormRespondedEvent e)
    {

    }

    @Override
    public void onFormResponse(PlayerFormRespondedEvent e)
    {
        Player player = e.getPlayer();
        String playerName = player.getName();
        GiftCodePlugin plugin = GiftCodePlugin.ins;
        String codeText = getResponse().getInputResponse(0);
        Codes codeSet = plugin.getCodesWithGiftCode(codeText);

        if (codeSet != null)
        {
            if (!codeSet.enable)
            {
                player.sendMessage("这个礼包码暂时不能使用: " + codeText);
                plugin.inputCache.put(playerName, codeText);
                return;
            }

            if (codeSet.isTimeout())
            {
                player.sendMessage("这个礼包码已经超过了使用期限: " + codeText);
                plugin.inputCache.put(playerName, codeText);
                return;
            }

            if (codeSet.isOneTimeCodes())
            {
                boolean used = !codeSet.codes.get(codeText);

                if (!used)
                {
                    Gift gift = plugin.getGiftWithUUID(codeSet.giftUuid);

                    if (gift.give(player))
                    {
                        player.sendMessage("礼包码兑换成功");
                        codeSet.codes.put(codeText, false);
                        plugin.inputCache.put(playerName, "");
                        plugin.saveGiftCodesConfig();
                    }else {
                        player.sendMessage("背包空间不够,需要"+ gift.items.size() + "格空间");
                        plugin.inputCache.put(playerName, codeText);
                    }
                } else {
                    player.sendMessage("这个礼包码已经被使用过了: " + codeText);
                    plugin.inputCache.put(playerName, codeText);
                }
            } else {
                boolean used = codeSet.codes.containsKey(player.getName());

                if (!used)
                {
                    Gift gift = plugin.getGiftWithUUID(codeSet.giftUuid);

                    if (gift.give(player))
                    {
                        player.sendMessage("礼包码兑换成功");
                        codeSet.codes.put(playerName, false);
                        plugin.inputCache.put(playerName, "");
                        plugin.saveGiftCodesConfig();
                        return;
                    } else {
                        player.sendMessage("背包空间不够,需要"+ gift.items.size() + "格空间");
                        plugin.inputCache.put(playerName, codeText);
                    }
                } else {
                    player.sendMessage("这个礼包码只能使用一次: " + codeText);
                    plugin.inputCache.put(playerName, codeText);
                }
            }
        } else {
            player.sendMessage("没有这个礼包码: " + codeText);
            plugin.inputCache.put(playerName, codeText);
        }

    }
}
