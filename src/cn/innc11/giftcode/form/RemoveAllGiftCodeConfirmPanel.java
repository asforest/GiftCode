package cn.innc11.giftcode.form;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindowModal;

@Deprecated
public class RemoveAllGiftCodeConfirmPanel extends FormWindowModal implements FormResponse
{
    public RemoveAllGiftCodeConfirmPanel()
    {
        super("删除所有礼包码确认面板", "确定要删除所有礼包码吗?所有的礼包码都不能再使用,这个操作无法撤销,操作务必谨慎!", "是的,删除所有礼包码", "取消");
        String contentText = "";
        contentText = contentText + "总共礼包码数: " + GiftCodePlugin.ins.codes.size() + "\n\n";
        contentText = contentText + getContent();
        setContent(contentText);
    }

    @Override
    public void onFormClose(PlayerFormRespondedEvent e)
    {

    }

    public void onFormResponse(PlayerFormRespondedEvent e)
    {
        Player player = e.getPlayer();

        if (!player.isOp())
            return;

        if (getResponse().getClickedButtonId() == 0)
        {
            GiftCodePlugin.ins.codes.clear();
            player.sendMessage("所有礼包码已经被清空");
        } else
        {
            player.showFormWindow(new CodesListPanel());
        }
    }
}
