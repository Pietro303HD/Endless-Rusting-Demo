package rusting.game;

import arc.Core;
import arc.Events;
import mindustry.Vars;
import mindustry.content.TechTree;
import mindustry.game.EventType;
import mindustry.type.Planet;
import mindustry.type.SectorPreset;
import rusting.Varsr;
import rusting.util.MusicControl.MusicSecController;

public class ERSectorPreset extends SectorPreset {
    private boolean unlockedInCampaign;
    //the earlier that a Seq of music is added, the more likely it'll play
    public MusicSecController musicSecController = new MusicSecController();
    public float musicChance = 0.0015f;

    public ERSectorPreset(String name, Planet planet, int sector) {
        super(name, planet, sector);
        Varsr.music.musicSectors.add(this);
        Events.on(EventType.ClientLoadEvent.class, e -> {
            loadBundles();
        });
    }

    @Override
    public void load() {
        super.load();
    }

    public void loadBundles(){
        if(!alwaysUnlocked){
            unlockedInCampaign = !Vars.enableConsole || Varsr.debug;
            TechTree.get(this).objectives.each(o -> {
                if(unlockedInCampaign && !o.complete()) unlockedInCampaign = false;
            });
        }
        if(unlocked || alwaysUnlocked) localizedName = Core.bundle.get("sector." + name + ".name");
        else localizedName = "???";
        if(alwaysUnlocked || (unlocked() && unlockedInCampaign)){
            description = Core.bundle.get("sector." + name + ".description");
            details = Core.bundle.get("sector." + name + ".details");
        }
        else{
            description = Core.bundle.get("sector.locked.description");
            details = Core.bundle.get("sector.locked.details");
        }
    }

    @Override
    public String displayDescription() {
        return description == Core.bundle.get("sector.locked.description") ? Core.bundle.get("sector.locked.description") : super.displayDescription();
    }
}
