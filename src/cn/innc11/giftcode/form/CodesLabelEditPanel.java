package cn.innc11.giftcode.form;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.innc11.giftcode.dt.Codes;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;

import java.util.UUID;

public class CodesLabelEditPanel extends FormWindowCustom implements FormResponse
{
    String codeSetUUID;

    public CodesLabelEditPanel(UUID uuid)
    {
        super("编辑标签");

        codeSetUUID = uuid.toString();

        Codes codeSet = GiftCodePlugin.ins.getCodesWithUUID(uuid);

        addElement(new ElementInput("礼包码的标签", "输入新的礼包码的标签", codeSet.label));
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

        Codes codes = GiftCodePlugin.ins.getCodesWithUUID(codeSetUUID);
        String newLabel = getResponse().getInputResponse(0);
        codes.label = newLabel;

        GiftCodePlugin.ins.saveGiftCodesConfig();
        player.showFormWindow(new CodesPanel(UUID.fromString(codeSetUUID)));
    }
}
