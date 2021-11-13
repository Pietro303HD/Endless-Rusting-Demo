package rusting.content;

import arc.Events;
import arc.graphics.Color;
import mindustry.Vars;
import mindustry.ctype.ContentList;
import mindustry.game.EventType;
import mindustry.game.Team;

public class RustingTeams implements ContentList {
    public static Team
    antiquumNatives, acrillimyl, pulseInfected, voidInfected;
    @Override
    public void load() {

        /*
        Events.on(EventType.ClientLoadEvent.class, e -> {
            antiquumNatives = Team.get(113);
            antiquumNatives.name = "antiquum-natives";
            antiquumNatives.palette[0].set(antiquumNatives.color.set(Color.valueOf("#70696c")));
            antiquumNatives.palette[1] = antiquumNatives.color.cpy().mul(0.75f);
            antiquumNatives.palette[2] = antiquumNatives.color.cpy().mul(0.5f);
        });

         */


        //acrillimyl = Team.get(114);
        //acrillimyl = new Team(180, "Acrillimyl", Color.valueOf("#b6cad6"));
    }
}
