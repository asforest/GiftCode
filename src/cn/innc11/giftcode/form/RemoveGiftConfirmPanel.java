package cn.innc11.giftcode.form;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.innc11.giftcode.dt.Gift;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindowModal;

import java.util.UUID;

public class RemoveGiftConfirmPanel extends FormWindowModal implements FormResponse
{
    String giftUuid;

    public RemoveGiftConfirmPanel(UUID giftUUID)
    {
        super("删除礼包", "确定要删除这个礼包吗?", "是的,删除这个礼包", "取消");

        giftUuid = giftUUID.toString();

        Gift gift = GiftCodePlugin.ins.getGiftWithUUID(giftUUID);

        String contentText = "礼包: " + gift.label + "\n";
        contentText += "UUID: " + gift.uuid.toString() + "\n";
        contentText += "物品数量: " + gift.items.size() + "\n";
        contentText += getContent();
        setContent(contentText);
    }

    @Override
    public void onFormClose(PlayerFormRespondedEvent e)
    {
        e.getPlayer().showFormWindow(new GiftPanel(UUID.fromString(giftUuid)));
    }

    public void onFormResponse(PlayerFormRespondedEvent e)
    {
        Player player = e.getPlayer();
        if (!player.isOp())
            return;

        if (getResponse().getClickedButtonId() == 0)
        {
            GiftCodePlugin.ins.removeGiftWithUUID(giftUuid);
            GiftCodePlugin.ins.saveGiftsConfig();
            GiftCodePlugin.ins.sendTitleMessage(player, "成功删除这个礼包!", () -> player.showFormWindow(new GiftListPanel()));
        } else {
            player.showFormWindow(new GiftPanel(UUID.fromString(giftUuid)));
        }

    }
}
