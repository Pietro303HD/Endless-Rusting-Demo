package rusting.content;

import arc.func.Cons;
import arc.graphics.Color;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.ctype.ContentList;
import mindustry.entities.Fires;
import mindustry.entities.bullet.*;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import rusting.entities.bullet.*;
import rusting.math.Mathr;
import rusting.world.blocks.defense.turret.PanelTurret;

public class RustingBullets implements ContentList{
    public static BulletType
        //basic bullets
        fossilShard, craeShard, raehShard, mhenShard, fraeShard, paveShard, darkShard,
        //missile/weaving bullets
        craeWeaver, paveWeaver,
        //lightning bullets
        craeBolt,
        //laser bolt bullets
        paveBolt,
        //essentualy small nukes
        craeBalistorm,
        //boomerangs
        craeLightRoundaboutRight, craeLightRoundaboutLeft;
    
    @Override
    public void load(){

        fossilShard = new BounceBulletType(4, 9, "bullet"){{
            width = 7;
            height = 8;
            lifetime = 54;
            hitEffect = Fx.hitFuse;
            despawnEffect = Fx.plasticburn;
            bounceEffect = Fx.blockExplosionSmoke;
            status = RustingStatusEffects.amberstriken;
            statusDuration = 45;
            knockback = 1;
            drag = 0.005f;
            bounciness = 0.6;
        }};

        craeShard = new BounceBulletType(4, 5, "bullet"){{
            width = 7;
            height = 8;
            lifetime = 15;
            hitEffect = Fx.hitFuse;
            despawnEffect = Fx.plasticburn;
            bounceEffect = Fx.hitLancer;
            status = RustingStatusEffects.macrosis;
            frontColor = Palr.pulseChargeStart;
            backColor = Palr.pulseBullet;
            trailColor = frontColor;
            trailEffect = Fx.lightningShoot;
            knockback = 1;
            drag = 0.005f;
            bounciness = 0.6;
        }};

        raehShard = new BounceBulletType(6, 12, "bullet"){{
            width = 9;
            height = 10;
            lifetime = 45;
            hitEffect = Fx.hitFuse;
            despawnEffect = Fx.plasticburn;
            bounceEffect = Fx.blockExplosionSmoke;
            trailColor = Pal.darkPyraFlame;
            incendChance = 1;
            incendAmount = 3;
            status = StatusEffects.burning;
            statusDuration = 640;
            trailLength = 10;
            homingPower = 0.25F;
            knockback = 3;
            drag = 0.005f;
            bounciness = 0.85;
        }};



        mhenShard = new BounceBulletType(6, 25, "bullet"){{
            consUpdate = new Cons<Bullet>() {
                @Override
                public void get(Bullet bullet) {
                    Fxr.singingFlame.at(bullet.x, bullet.y, bullet.rotation());
                    //essentualy goes to owner aim pos if owner has instance of the Panel Turret Build.
                    if(bullet.owner instanceof PanelTurret.PanelTurretBuild) bullet.rotation((bullet.angleTo(((PanelTurret.PanelTurretBuild) bullet.owner).targetPos) + Mathf.lerpDelta(bullet.angleTo(((PanelTurret.PanelTurretBuild) bullet.owner).targetPos) - bullet.rotation(), 0, 0.0525f * bullet.fin())));
                }
            };
            despawnEffect = Fx.fireSmoke;
            hitEffect = Fx.fire;
            bounceEffect = Fxr.shootMhemFlame;
            incendChance = 1;
            incendAmount = 10;
            status = StatusEffects.burning;
            statusDuration = 3600;
            width = 6;
            height = 8;
            hitSize = 12;
            lifetime = 35;
            hitEffect = Fx.hitFuse;
            trailLength = 0;
            homingPower = 0.125F;
            drag = 0.015f;
            bounciness = 0.95;
        }};

        fraeShard = new ConsBulletType(10, 25, "bullet"){{
            consUpdate = new Cons<Bullet>() {
                @Override
                public void get(Bullet bullet) {
                    darkShard.create(bullet.owner, bullet.team, bullet.x, bullet.y, bullet.rotation() - 90, Mathr.helix(7, 1, bullet.fin()));
                    darkShard.create(bullet.owner, bullet.team, bullet.x, bullet.y, bullet.rotation() + 90, Mathr.helix(7, 1, bullet.fin()));
                }
            };
            despawnEffect = Fx.plasticburn;
            hitEffect = Fx.plasticExplosion;
            status = StatusEffects.corroded;
            statusDuration = 7200;
            width = 10;
            height = 12;
            pierce = true;
            pierceBuilding = true;
            lifetime = 20;
            hitEffect = Fx.hitFuse;
        }};

        paveShard = new ConsBulletType(12, 100, "bullet"){{
            consUpdate = new Cons<Bullet>() {
                @Override
                public void get(Bullet bullet){

                    Fxr.singingFlame.at(bullet.x, bullet.y, bullet.rotation() + Mathr.helix(7, 45, bullet.fin()));
                    Fxr.singingFlame.at(bullet.x, bullet.y, bullet.rotation() - Mathr.helix(7, 45, bullet.fin()));

                    if(bullet.collided.size >= 1) {
                        Fires.create(Vars.world.tileWorld(bullet.x, bullet.y));
                        Fxr.paveFlame.at(bullet.x, bullet.y, bullet.rotation());
                    }
                }
            };
            despawnEffect = Fx.fireSmoke;
            hitEffect = Fxr.shootMhemFlame;
            incendChance = 1;
            incendAmount = 10;
            status = StatusEffects.melting;
            statusDuration = 3600;
            width = 8;
            height = 10;
            hitSize = 12;
            pierce = true;
            pierceBuilding = true;
            lifetime = 25;
            hitEffect = Fx.hitFuse;
        }};

        darkShard = new BounceBulletType(4, 15, "bullet"){{
            consUpdate = new Cons<Bullet>() {
                @Override
                public void get(Bullet bullet) {
                    if(bullet.fin() % 0.04 < 0.01) Fxr.blackened.at(bullet.x, bullet.y, bullet.rotation());
                }
            };
            despawnEffect = Fx.fireSmoke;
            hitEffect = Fx.casing3Double;
            bounceEffect = Fx.none;
            frontColor = Color.darkGray;
            backColor = Palr.voidBullet;
            status = RustingStatusEffects.umbrafliction;
            statusDuration = 3600;
            width = 10;
            height = 12;
            lifetime = 35;
            hitEffect = Fx.hitFuse;
            trailLength = 0;
            homingPower = 0.125F;
            drag = 0.015f;
            bounciness = 0.95;
        }};

        craeWeaver = new BounceBulletType(3, 14, "bullet"){{
            width = 15;
            height = 18;
            lifetime = 45;
            shrinkX = 1;
            shootEffect = Fx.none;
            hitEffect = Fx.hitLancer;
            despawnEffect = Fx.plasticburn;
            bounceEffect = Fx.hitFuse;
            status = RustingStatusEffects.macotagus;
            statusDuration = 1440;
            frontColor = Palr.pulseChargeStart;
            backColor = Palr.pulseBullet;
            trailColor = frontColor;
            trailEffect = Fxr.craeWeaversResidue;
            trailChance = 0.15f;
            trailLength = 8;
            trailWidth = 5;
            weaveMag = 2;
            weaveScale = 5;
            homingPower = 0.125f;
            knockback = -0.15f;
            bounciness = 0.8;
        }};

        //duplicated bullet, only to be used for duoplys
        paveWeaver = new BounceBulletType(5, 7, "bullet"){{
            width = 15;
            height = 18;
            lifetime = 35;
            healPercent = 1f;
            shrinkX = 1;
            collidesTeam = true;
            shootEffect = Fx.heal;
            hitEffect = Fx.plasticburn;
            despawnEffect = Fx.plasticburn;
            backColor = Pal.heal;
            frontColor = Color.white;
            trailColor = frontColor;
            trailEffect = Fx.shootHeal;
            trailChance = 0.15f;
            trailLength = 8;
            trailWidth = 5;
            weaveMag = 2;
            weaveScale = 5;
            homingPower = 0.325f;
            knockback = 0.25f;
            bounciness = 0.8;
        }};

        craeBolt = new LightningBulletType(){{
            damage = 15;
            lightningDamage = 35f;
            lightningLength = 12;
            lightningColor = Palr.pulseChargeStart;
            status = RustingStatusEffects.macrosis;
        }};

        paveBolt = new LaserBoltBulletType(5.2f, 8){{
            recoil = 1.25f;
            lifetime = 15f;
            healPercent = 2f;
            collidesTeam = true;
            backColor = Pal.heal;
            frontColor = Color.white;
        }};

        craeBalistorm = new ConsBulletType(3, 14, "bullet"){{

            consDespawned = new Cons<Bullet>() {
                @Override
                public void get(Bullet b) {
                    Fxr.pulseSmoke.at(b.x, b.y, b.rotation(), new float[]{
                            80,
                            12,
                            0.85f
                    });
                    Fxr.pulseExplosion.at(b.x, b.y, b.rotation(), new Float[]{
                            80f,
                            12f
                    });
                }
            };

            consHit = consDespawned;

            splashDamage = 125;
            splashDamageRadius = 80;

            width = 25;
            height = 25;
            lifetime = 460;
            shrinkX = -0.5f;
            hitEffect = Fx.none;
            despawnEffect = Fx.none;
            status = RustingStatusEffects.macotagus;
            statusDuration = 1440;
            frontColor = Palr.pulseChargeStart;
            backColor = Palr.pulseBullet;
            trailColor = frontColor;
            trailChance = 1f;
            weaveMag = 2;
            weaveScale = 5;
            homingPower = 0.125f;
            collidesTiles = false;
            collides = false;
            collidesAir = false;
            scaleVelocity = true;
            hitShake = 1f;
            hitSound = Sounds.explosion;
            knockback = -0.15f;
        }};

        craeLightRoundaboutRight = new BoomerangBulletType(2, 15, "endless-rusting-boomerang"){{
            other = craeLightRoundaboutLeft;
            width = 12;
            height = 14;
            lifetime = 300;
            homingPower = 0.05f;
            rotateMag = 3;
            rotScaleMin = 0.3f;
            homingPower = 0.01f;
            rotateRight = true;
            hitEffect = Fx.hitFuse;
            despawnEffect = Fx.plasticburn;
            status = RustingStatusEffects.macrosis;
            frontColor = Palr.pulseChargeStart;
            backColor = Palr.pulseChargeStart;
            trailEffect = Fxr.whoosh;
            drag = -0.001f;
        }};

        craeLightRoundaboutLeft = new BoomerangBulletType(2, 15, "endless-rusting-boomerang"){{
            other = craeLightRoundaboutRight;
            width = 12;
            height = 14;
            lifetime = 300;
            homingPower = 0.05f;
            rotateMag = 3;
            rotScaleMin = 0.3f;
            homingPower = 0.01f;
            rotateRight = false;
            hitEffect = Fx.hitFuse;
            despawnEffect = Fx.plasticburn;
            status = RustingStatusEffects.macrosis;
            frontColor = Palr.pulseChargeStart;
            backColor = Palr.pulseChargeEnd;
            trailEffect = Fxr.whoosh;
            drag = -0.001f;
        }};
    }
}
