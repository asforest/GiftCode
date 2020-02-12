package cn.innc11.giftcode.form;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.innc11.giftcode.dt.Gift;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;

import java.util.*;

public class GiftListPanel extends FormWindowSimple implements FormResponse
{
    static final int buttons = 1;
    String giftLabels = "";

    public GiftListPanel()
    {
        super(String.format("礼包管理面板 (%d)", GiftCodePlugin.ins.gifts.size()), "");

        addButton(new ElementButton("添加新的礼包"));

        for( Gift gift : GiftCodePlugin.ins.gifts.values())
        {
            String buttonText = String.format("%s (%d个物品)", gift.label, gift.items.size());
            giftLabels += String.format("%s;", gift.uuid);
            addButton(new ElementButton(buttonText));
        }
    }

    @Override
    public void onFormClose(PlayerFormRespondedEvent e)
    {
        e.getPlayer().showFormWindow(new MainPanel());
    }

    public void onFormResponse(PlayerFormRespondedEvent e)
    {
        Player player = e.getPlayer();

        if (!player.isOp())
            return;

        int clickedButtonIndex = getResponse().getClickedButtonId();

        if (clickedButtonIndex == 0)
        {
            player.showFormWindow(new GiftPanel(null));
        } else {
            String[] GiftLabels = giftLabels.split(";");
            int index = clickedButtonIndex - buttons;

            Gift gift = GiftCodePlugin.ins.getGiftWithUUID(GiftLabels[index]);
            player.showFormWindow(new GiftPanel(gift.uuid));
        }
    }
}
