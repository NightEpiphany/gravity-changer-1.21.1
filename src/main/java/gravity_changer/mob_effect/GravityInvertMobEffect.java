package gravity_changer.mob_effect;

import gravity_changer.GravityChangerMod;
import gravity_changer.GravityComponent;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class GravityInvertMobEffect extends MobEffect {
    
    public static final int COLOR = 0x98D982;
    
    public static final ResourceLocation PHASE = ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "invert_mob_effect_phase");
    
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(GravityChangerMod.NAMESPACE, "invert");
    
    public static final GravityInvertMobEffect INSTANCE = new GravityInvertMobEffect();
    
    private GravityInvertMobEffect() {
        super(MobEffectCategory.NEUTRAL, COLOR);
    }
    
    public static void init() {
        GravityComponent.GRAVITY_UPDATE_EVENT.register(
            PHASE, (entity, component) -> {
                if (entity instanceof LivingEntity livingEntity) {
                    if (livingEntity.hasEffect(Holder.direct(INSTANCE))) {
                        component.applyGravityDirectionEffect(
                            component.getCurrGravityDirection().getOpposite(),
                            null, 5
                        );
                    }
                }
            }
        );
        
        // apply invert after gravity effect
        GravityComponent.GRAVITY_UPDATE_EVENT.addPhaseOrdering(
            GravityDirectionMobEffect.PHASE, GravityInvertMobEffect.PHASE
        );
        
        Registry.register(
            BuiltInRegistries.MOB_EFFECT, ID, INSTANCE
        );
    }
    
    
}
