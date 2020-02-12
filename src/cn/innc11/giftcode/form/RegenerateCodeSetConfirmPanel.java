package cn.innc11.giftcode.form;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.innc11.giftcode.dt.Codes;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindowModal;

import java.util.UUID;

public class RegenerateCodeSetConfirmPanel extends FormWindowModal implements FormResponse
{
    String codeSetUUID;

    public RegenerateCodeSetConfirmPanel(UUID uuid)
    {
        super("重新生成礼包码", "确认重新生成这组礼包码吗?", "是的,重新生成", "取消");

        codeSetUUID = uuid.toString();

        Codes codeSet = GiftCodePlugin.ins.getCodesWithUUID(uuid);

        StringBuffer content = new StringBuffer();
        content.append("礼包码: " + codeSet.label + "\n");
        content.append(String.format("使用情况: %s%s", codeSet.getUsedCount(), codeSet.isOneTimeCodes() ? "/"+codeSet.getTotalCount() : "") + "\n");
        content.append(getContent());

        setContent(content.toString());
    }

    @Override
    public void onFormClose(PlayerFormRespondedEvent e)
    {

    }

    @Override
    public void onFormResponse(PlayerFormRespondedEvent e)
    {
        Player player = e.getPlayer();
        GiftCodePlugin plugin = GiftCodePlugin.ins;
        
        if (!player.isOp())
            return;
        
        if (getResponse().getClickedButtonId() == 0)
        {
            Codes codeSet = plugin.getCodesWithUUID(codeSetUUID);

            if (codeSet._codeLength < 4)
            {
                plugin.sendTitleMessage(player, "礼包码需要设置长度", () -> player.showFormWindow(new CodesPanel(codeSet.uuid)));
                return;
            }

            if (codeSet.giftUuid == null)
            {
                plugin.sendTitleMessage(player, "礼包码需要设置对应的礼包", () -> player.showFormWindow(new CodesPanel(codeSet.uuid)));
                return;
            }

            codeSet.regenerate();
            plugin.sendTitleMessage(player, "礼包码已经生成"+ (codeSet.isOneTimeCodes() ? (" x &6" + codeSet._codeCount) : ""), () -> player.showFormWindow(new CodesPanel(UUID.fromString(codeSetUUID))));
            plugin.saveGiftCodesConfig();
        } else {
            player.showFormWindow(new CodesPanel(UUID.fromString(codeSetUUID)));
        }
    }
}
