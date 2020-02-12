package cn.innc11.giftcode.form;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.innc11.giftcode.dt.Gift;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;

import java.util.UUID;

public class GiftLableEditPanel extends FormWindowCustom implements FormResponse
{
    String giftUUID;

    public GiftLableEditPanel(UUID giftUuid)
    {
        super("编辑礼包标签");

        giftUUID = giftUuid.toString();

        Gift gift = GiftCodePlugin.ins.getGiftWithUUID(giftUUID);

        addElement( new ElementInput("礼包标签", "输入新的礼包标签", gift.label));
    }

    @Override
    public void onFormClose(PlayerFormRespondedEvent e)
    {
        e.getPlayer().showFormWindow(new GiftPanel(UUID.fromString(giftUUID)));
    }

    public void onFormResponse(PlayerFormRespondedEvent e)
    {
        Player player = e.getPlayer();

        if (!player.isOp())
            return;


        GiftCodePlugin plugin = GiftCodePlugin.ins;
        Gift gift = plugin.getGiftWithUUID(giftUUID);
        String newLabel = getResponse().getInputResponse(0);

        if (plugin.getGiftWithLabel(newLabel) == null)
        {
            gift.label = newLabel;
            player.showFormWindow(new GiftPanel(UUID.fromString(giftUUID)));
        } else {
            plugin.sendTitleMessage(player, "已经有一个相同的标签了,不能重复", () -> player.showFormWindow(new GiftPanel(UUID.fromString(giftUUID))));
        }
    }
}
