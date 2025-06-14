package gravity_changer;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.ladysnake.cca.api.v3.world.WorldComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.world.WorldComponentInitializer;

public class GravityChangerComponents implements EntityComponentInitializer, WorldComponentInitializer {
    
    public static final ResourceLocation DATA_COMPONENT_ID =
        ResourceLocation.fromNamespaceAndPath("gravity_changer", "gravity_data");
    
    public static final org.ladysnake.cca.api.v3.component.ComponentKey<GravityComponent> GRAVITY_COMP_KEY =
        ComponentRegistry.getOrCreate(DATA_COMPONENT_ID, GravityComponent.class);
    
    public static final ResourceLocation DIMENSION_DATA_ID =
        ResourceLocation.fromNamespaceAndPath("gravity_changer", "dimension_data");
    
    public static final org.ladysnake.cca.api.v3.component.ComponentKey<DimensionGravityDataComponent> DIMENSION_COMP_KEY =
        ComponentRegistry.getOrCreate(DIMENSION_DATA_ID, DimensionGravityDataComponent.class);
    
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(
            GRAVITY_COMP_KEY, GravityComponent::new,
                (from, to, provider, lossless, keepInventory, sameCharacter) -> {
                    if (lossless || !GravityChangerMod.config.resetGravityOnRespawn) {
                        RespawnCopyStrategy.copy(from, to, provider);
                    }
                }
        );
        registry.registerFor(Entity.class, GRAVITY_COMP_KEY, GravityComponent::new);
    }
    
    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(DIMENSION_COMP_KEY, DimensionGravityDataComponent::new);
    }
}
