package cn.innc11.giftcode.form;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;

public class MainPanel extends FormWindowSimple implements FormResponse
{
    public MainPanel()
    {
        super("GiftCode管理面板", "");

        addButton(new ElementButton(TextFormat.colorize("&9礼包")));
        addButton(new ElementButton(TextFormat.colorize("&e礼包码")));
        addButton(new ElementButton("重载配置文件"));
        addButton(new ElementButton("去使用礼包码"));
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

        int clickedButtonIndex = getResponse().getClickedButtonId();

        switch (clickedButtonIndex)
        {
            case 0:
                player.showFormWindow(new GiftListPanel());
                break;
            case 1:
                player.showFormWindow(new CodesListPanel());
                break;
            case 2:
                GiftCodePlugin.ins.loadConfig();
                player.sendMessage("重新加载配置文件...");
                break;
            case 3:
                player.showFormWindow(new RedeemCodePanel(player, ""));
                break;
        }

    }
}
