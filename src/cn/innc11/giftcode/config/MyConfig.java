package cn.innc11.giftcode.config;

import cn.innc11.giftcode.GiftCodePlugin;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.Config;

import java.io.File;

public abstract class MyConfig
{
    protected boolean modified = false;
    protected boolean saving = false;
    protected File file;
    Config config;

    public MyConfig(String fileName)
    {
        this.file = new File(GiftCodePlugin.ins.getDataFolder(), fileName);
        this.config = new Config(this.file, 2);
    }

    public boolean exists()
    {
        return this.file.exists();
    }

    public final void save()
    {
        if (!this.saving)
        {
            this.modified = true;
            this.saving = true;
            GiftCodePlugin.ins.getServer().getScheduler().scheduleTask(GiftCodePlugin.ins, getNewSaveTask(), true);
        } else {
            this.modified = true;
        }
    }

    public abstract void reload();

    protected abstract void _save();

    protected final PluginTask<GiftCodePlugin> getNewSaveTask()
    {
        return new PluginTask<GiftCodePlugin>(GiftCodePlugin.ins)
        {
            public void onRun(int currentTicks)
            {
                while (MyConfig.this.modified)
                {
                    MyConfig.this.modified = false;
                    MyConfig.this._save();
                }
                MyConfig.this.saving = false;
            }
        };
    }
}
