package rusting.util;

import arc.Core;
import arc.Events;
import arc.assets.AssetDescriptor;
import arc.assets.loaders.MusicLoader.MusicParameter;
import arc.audio.Music;
import arc.func.Boolf;
import arc.func.Boolp;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.core.GameState;
import mindustry.core.GameState.State;
import mindustry.game.EventType;
import mindustry.game.EventType.StateChangeEvent;
import mindustry.type.SectorPreset;
import rusting.game.ERSectorPreset;
import rusting.util.MusicControl.MusicSecController.MusicSecSegment;

import static mindustry.Vars.*;
import static rusting.Varsr.music;

//note: Yoinked from BetaMindy, originally from there. Go check it out, it's a good mod.
public class MusicControl {
    public final static String musicMod = "endless-rusting";

    public static final String[] musicFiles = {"omnipresentGlare", "march"};
    public Music[] musics;
    //used to hold all the songs that can play on a tick
    public static Seq<MusicSecSegment> possibleCandidates = Seq.with();
    public static Seq<Boolp> candidatesBools = Seq.with();

    public @Nullable Music current = null;
    public @Nullable Music waitingCurrent = null;
    protected int cimportance = 0;
    protected @Nullable Boolp cpred = null;
    protected boolean fadeinNext = true;
    public boolean waiting = false;
    boolean stillSearching = true;

    private float fade = 0f;
    private int fadeMode = 0;
    //1: wait for current song to finish, fade vanilla and custom out, then play
    //2: interrupt current song, fade it out, then fade in next
    //3: fade current song out, nothing else
    private int lastVol = -1;

    private ERSectorPreset sector;
    public ERSectorPreset currentMusicSector;
    public MusicSecSegment secSegment;

    public class MusicHolder{
        public Music music;
        public float fade = 1;
        public float duration = 0, totalDuration = music.getPosition();
    }

    //each sector has it's own music controller, which can do things
    public static class MusicSecController{
        private static int importance;
        private static int trueImportance;
        private float musicPlayTime = 0;
        public float musicChangeDelay = 15;
        public static class MusicSecSegment{
            public MusicSecSegment(int id, boolean loop){
                musicId = id;
                this.loop = loop;
            }
            public boolean loop;
            public int musicId;
            public float playChance = 0.0015f;
        }

        public Seq<ObjectMap<Boolf<GameState>, Seq<MusicSecSegment>>> musicMap = Seq.with();

        public MusicSecController(){

        }

        public void update(){
            if(musicPlayTime < musicChangeDelay) {
                musicPlayTime += 1;
                return;
            }
            musicPlayTime = 0;
            music.stillSearching = true;
            importance = 0;
            possibleCandidates.clear();
            candidatesBools.clear();
            music.currentMusicSector.musicSecController.musicMap.each((ms) -> {
                importance++;
                if(!music.stillSearching || (music.current != null || music.waitingCurrent != null) && importance <= music.cimportance) return;
                //Log.info("Attempting to play tracks with importance of: " + importance + " compared to " + music.cimportance);
                ms.each((b, m) -> {
                    //Log.info("Searching: " + music.stillSearching);
                    if(!music.stillSearching) return;
                    music.secSegment = m.random();
                    //Log.info("Able to play: " + b.get(state) + " " + music.musics[music.secSegment.musicId].toString());
                    if(b.get(state) && (music.secSegment.playChance == 1 || Mathf.chance(music.secSegment.playChance))) {
                        //Log.info("Posible track found!");
                        possibleCandidates.add(music.secSegment);
                        candidatesBools.add(() -> !b.get(state));
                        trueImportance = importance;
                    }
                });
            });
            if(possibleCandidates.size != 0) {
                //Log.info("Playing now: " + music.musics[music.secSegment.musicId].toString());
                music.secSegment = possibleCandidates.get(possibleCandidates.size - 1);
                control.sound.stop();

                music.stillSearching = false;
                music.playUntil(music.secSegment.musicId, candidatesBools.get(candidatesBools.size - 1), trueImportance,true, false, music.secSegment.loop);
            }
        }
    }

    public Boolf<SectorPreset> bSector = (sectorPreset -> {
        if(sectorPreset instanceof ERSectorPreset){
            sector = (ERSectorPreset) sectorPreset;
            return true;
        }
        return false;
    });

    public Seq<ERSectorPreset> musicSectors = Seq.with();

    public void init(){
        musics = new Music[musicFiles.length];
        Events.on(EventType.FileTreeInitEvent.class, e -> {
            //load music here
            for(int i = 0; i < musicFiles.length; i++){
                musics[i] = loadMusic(musicFiles[i]);
            }
        });

        Events.on(StateChangeEvent.class, e -> {
            if(e.to == State.playing && e.from == State.menu){
                setupSector();
            }
            else if(e.to == State.menu) currentMusicSector = null;
        });
    }

    public void setupSector(){
        if(state.isCampaign() && bSector.get(state.getSector().preset) && musicSectors.contains(sector) && sector.musicChance != 0 && musicSectors.size > 0){
            currentMusicSector = sector;
        }
        else if(bSector.get(musicSectors.find(this::isValidSector))) currentMusicSector = sector;
    }

    public boolean isValidSector(ERSectorPreset erSectorPreset){
        sector = erSectorPreset;
        return state.map.file.name().equals(sector.name.substring(sector.minfo.mod.name.length() + 1) + "." + mapExtension);
    }

    public void next(){
        current = waitingCurrent;
        waitingCurrent = null;
    }

    public void update(){
        if(state == null || !state.isGame()){
            if(current != null) reset();
        }

        if(waitingCurrent != null || current != null){
            //wait for current song to finish
            if(fadeMode == 1 && waitingCurrent != null){
            fade = Mathf.lerp(fade, 0f, 0.08f);
            Core.settings.put("musicvol", (int)(lastVol * fade));
            if(current == null || fade <= 0.01f){
                control.sound.stop();
                if(current != null) current.stop();
                fade = 0;
                next();
                cimportance = 0;
                current.play();
                current.setLooping(true);
            }
        }
        else{
            if(fadeMode == 2 && waitingCurrent != null){
                if(current == null && fade < 0.98f){
                        fade = Mathf.lerp(fade, 1f, 0.08f);
                        Core.settings.put("musicvol", (int)(lastVol * fade));
                    }
                }
                else if (current != null && waitingCurrent != null){
                    fade = Mathf.lerp(fade, 0f, 0.08f);
                    Core.settings.put("musicvol", (int)(lastVol * fade));
                    if(fade <= 0.01f){
                        control.sound.stop();
                        if(current != null) current.stop();
                        fade = 0;
                        next();
                        cimportance = 0;
                        current.play();
                        current.setLooping(true);
                    }
                }
            }
            if(fadeMode == 3 && current != null){
                fade = Mathf.lerp(fade, 0f, 0.08f);
                Core.settings.put("musicvol", (int)(lastVol * fade));
                if(fade <= 0.01f){
                    control.sound.stop();
                    if(current != null) current.stop();
                    fade = 0;
                    cimportance = 0;
                }
            }
            if(fadeMode != 3 && shouldEnd()){
                if(fadeMode == 0) fade = 1f;
                fadeMode = 3;
                current.setLooping(false);
            }
        }

        if(currentMusicSector != null){
            currentMusicSector.musicSecController.update();
        }
    }

    public void playUntil(int id, @Nullable Boolp end, int importance, boolean fadein, boolean wait, boolean loop){
        if(id < 0 || importance < cimportance && current != null) return; //do not use this method to shut it up!
        cpred = end;
        cimportance = Math.max(0, importance);

        if(current == null){
            fadeMode = 1;
            fade = 1f;
            fadeinNext = fadein;
            lastVol = Core.settings.getInt("musicvol");
            current = musics[id];
            waiting = wait;
        }
        else{
            play(musics[id], fadein, wait, loop);
        }
    }

    /** Fades out the current track but leaves the music in suspense, waiting. */
    public void interrupt(){
        if(current == null || fadeMode == 1 || fadeMode == 3) return;
        if(fadeMode != 2) fade = 1f;
        fadeMode = 4;
    }

    /** Start playing the current track that has been waiting. */
    public void go(){
        if(!waiting || current == null || current.isPlaying()) return;
        waiting = false;
        if(fadeMode == 1) return; //this is the only mode that calls play() at its end
        current.play();
        current.setVolume(Core.settings.getInt("musicvol") / 100f);
        current.setLooping(true);
    }

    public void play(Music music, boolean fadein, boolean wait, boolean loop){
        fadeMode = wait ? 1 : 2;
        if(lastVol != -1){
            Core.settings.put("musicvol", lastVol);
            lastVol = -1;
        }
        if(fadein) fade = 1;

        waitingCurrent = music;
        waiting = wait;
        if(!wait){
            next();
            current.play();
            current.setVolume(fadein ? 0f : Core.settings.getInt("musicvol") / 100f);
            if(loop) current.setLooping(true);
        }
    }

    public void reset(){
        if(current != null){
            current.setLooping(false);
            if(current.isPlaying()) current.stop();
        }
        current = null;
        waitingCurrent = null;
        cpred = null;
        fadeMode = 0;
        fade = 0f;
        cimportance = 0;
        if(lastVol != -1){
            Core.settings.put("musicvol", lastVol);
            lastVol = -1;
        }
    }

    public boolean shouldEnd(){
        return (cpred != null && cpred.get());
    }

    public Music loadMusic(String soundName){
        if(headless) return new Music();

        String name = "music/" + soundName;
        String path = tree.get(name + ".ogg").exists() ? name + ".ogg" : name + ".mp3";

        Music music = new Music();
        AssetDescriptor<?> desc = Core.assets.load(path, Music.class, new MusicParameter(music));
        desc.errored = Throwable::printStackTrace;

        return music;
    }
}
