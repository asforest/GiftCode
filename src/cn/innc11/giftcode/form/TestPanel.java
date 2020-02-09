package cn.innc11.giftcode.form;

import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;

public class TestPanel extends FormWindowSimple implements FormResponse
{
    public TestPanel()
    {
        super("TestPanel", "content");
        addButton(new ElementButton("buttonbutton"));
    }

    @Override
    public void onFormResponse(PlayerFormRespondedEvent e)
    {

    }

    @Override
    public void onFormClose(PlayerFormRespondedEvent e)
    {

    }
}
