package cn.innc11.giftcode.form;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.innc11.giftcode.dt.Codes;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindowModal;

import java.util.UUID;

public class RemoveCodeSetConfirmPanel extends FormWindowModal implements FormResponse
{
    String codeSetUUID;

    public RemoveCodeSetConfirmPanel(UUID uuid)
    {
        super("删除礼包码", "确认要删除这组礼包码吗?", "是的,删除这组礼包码", "取消");

        codeSetUUID = uuid.toString();

        Codes codes = GiftCodePlugin.ins.getCodesWithUUID(uuid);
        String contentText = "礼包码: " + codes.label + "\n";
        contentText += "对应的礼包: " + ((codes.giftUuid != null) ? (codes.getGift()).label : "没有对应的礼包") + "\n ";
        contentText = contentText + getContent();

        setContent(contentText);
    }

    @Override
    public void onFormClose(PlayerFormRespondedEvent e)
    {
        e.getPlayer().showFormWindow(new CodesPanel(UUID.fromString(codeSetUUID)));
    }

    public void onFormResponse(PlayerFormRespondedEvent e)
    {
        Player player = e.getPlayer();

        if (!player.isOp())
            return;

        if (getResponse().getClickedButtonId() == 0)
        {
            GiftCodePlugin.ins.removeCodesWithUUID(codeSetUUID);
            GiftCodePlugin.ins.saveGiftCodesConfig();
            player.showFormWindow(new CodesListPanel());
        } else {
            player.showFormWindow(new CodesPanel(UUID.fromString(codeSetUUID)));
        }
    }
}
