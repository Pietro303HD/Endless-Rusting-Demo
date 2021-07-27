package rusting.world.blocks.defense.turret;

import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Damage;
import mindustry.entities.bullet.BulletType;
import mindustry.world.blocks.defense.turrets.PowerTurret;

//a turret which shoots spaced projectiles, with some of their stats being treated differently.
public class QuakeTurret extends PowerTurret {

    //spacing of shots
    public float spacing = 7;
    //delay of shots
    public float quakeInterval = 5;
    //whether to actaly create bullet at the spot
    public boolean createBullet;
    public QuakeTurret(String name) {
        super(name);
    }

    public class QuakeTurretBuild extends PowerTurretBuild{
        @Override
        protected void shoot(BulletType type) {
            recoil = recoilAmount;
            heat = 1f;
            effects();
            useAmmo();
            for (int s = 0; s < shots; s++) {
                float rot = rotation - spread * (shots - 1)/2 + s * spread;
                for(int o = 1; o < range/ spacing; o++){
                    int i = o;
                    Time.run(o * quakeInterval, () -> {
                        Tmp.v1.set(x + tr.x, y + tr.y).add(Tmp.v2.trns(rot, i * spacing));
                        Damage.damage(team, Tmp.v1.x, Tmp.v1.y, type.splashDamageRadius, type.splashDamage, type.collidesAir, type.collidesGround);
                        if(type.status != null) Damage.status(team, Tmp.v1.x, Tmp.v1.y, type.splashDamageRadius, type.status, type.statusDuration, type.collidesAir, type.collidesGround);
                        type.hitEffect.at(Tmp.v1.x, Tmp.v1.y);
                    });
                }
            }
        }
    }
}
