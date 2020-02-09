package cn.innc11.giftcode.listener;

import cn.innc11.giftcode.form.FormResponse;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;

public class FormResponseListener implements Listener
{
    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent e)
    {
        if (!(e.getWindow() instanceof FormResponse))
            return;

        FormResponse formResponse = ((FormResponse) e.getWindow());

        if(e.getResponse() != null)
        {
            formResponse.onFormResponse(e);
        }else {
            formResponse.onFormClose(e);
        }

    }
}
