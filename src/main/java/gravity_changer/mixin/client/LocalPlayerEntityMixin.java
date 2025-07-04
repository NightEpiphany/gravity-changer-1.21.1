package gravity_changer.mixin.client;

import com.mojang.authlib.GameProfile;
import gravity_changer.api.GravityChangerAPI;
import gravity_changer.util.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerEntityMixin extends AbstractClientPlayer {

    @Shadow
    private boolean autoJumpEnabled;

    @Mutable
    @Shadow
    @Final
    protected final Minecraft minecraft;

    public LocalPlayerEntityMixin(ClientLevel world, GameProfile profile, Minecraft minecraft) {
        super(world, profile);
        this.minecraft = minecraft;
    }
    
    @Shadow
    protected abstract boolean suffocatesAt(BlockPos pos);

    //disables the annoying auto-jump functionality while swinging gravity direction

    @Inject(
            method = "updateAutoJump",
            at = @At("HEAD"),
            cancellable = true)
    private void inject_setAutoJumpDisableWhenChangeGravityDirection_1(CallbackInfo ci) {
        if (GravityChangerAPI.getGravityDirection(this) != Direction.DOWN) {
            ci.cancel();
        }
    }


    @Inject(
            method = "sendPosition",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;autoJump()Lnet/minecraft/client/OptionInstance;",
                    shift = At.Shift.AFTER
            )
    )private void inject_setAutoJumpDisableWhenChangeGravityDirection_2(CallbackInfo ci) {
        if (this.minecraft.getCameraEntity() == this) {
            if (GravityChangerAPI.getGravityDirection(this) != Direction.DOWN)
                this.autoJumpEnabled = false;
        }
    }
    
    @Redirect(
        method = "suffocatesAt(Lnet/minecraft/core/BlockPos;)Z",
        at = @At(
            value = "NEW",
            target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;",
            ordinal = 0
        )
    )
    private AABB redirect_wouldCollideAt_new_0(double x1, double y1, double z1, double x2, double y2, double z2, BlockPos pos) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this);
        if (gravityDirection == Direction.DOWN) {
            return new AABB(x1, y1, z1, x2, y2, z2);
        }
        
        AABB playerBox = this.getBoundingBox();
        Vec3 playerMask = RotationUtil.maskPlayerToWorld(0.0D, 1.0D, 0.0D, gravityDirection);
        AABB posBox = new AABB(pos);
        Vec3 posMask = RotationUtil.maskPlayerToWorld(1.0D, 0.0D, 1.0D, gravityDirection);
        
        return new AABB(
            playerMask.multiply(playerBox.minX, playerBox.minY, playerBox.minZ).add(posMask.multiply(posBox.minX, posBox.minY, posBox.minZ)),
            playerMask.multiply(playerBox.maxX, playerBox.maxY, playerBox.maxZ).add(posMask.multiply(posBox.maxX, posBox.maxY, posBox.maxZ))
        );
    }
    
    @Inject(
        method = "moveTowardsClosestSpace(DD)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inject_pushOutOfBlocks(double x, double z, CallbackInfo ci) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(this);
        if (gravityDirection == Direction.DOWN) return;
        
        ci.cancel();
        
        Vec3 pos = RotationUtil.vecPlayerToWorld(x - this.getX(), 0.0D, z - this.getZ(), gravityDirection).add(this.position());
        BlockPos blockPos = BlockPos.containing(pos);
        if (this.suffocatesAt(blockPos)) {
            double dx = pos.x - (double) blockPos.getX();
            double dy = pos.y - (double) blockPos.getY();
            double dz = pos.z - (double) blockPos.getZ();
            Direction direction = null;
            double minDistToEdge = Double.MAX_VALUE;
            
            Direction[] directions = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
            for (Direction playerDirection : directions) {
                Direction worldDirection = RotationUtil.dirPlayerToWorld(playerDirection, gravityDirection);
                
                double g = worldDirection.getAxis().choose(dx, dy, dz);
                double distToEdge = worldDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - g : g;
                if (distToEdge < minDistToEdge && !this.suffocatesAt(blockPos.relative(worldDirection))) {
                    minDistToEdge = distToEdge;
                    direction = playerDirection;
                }
            }
            
            if (direction != null) {
                Vec3 velocity = this.getDeltaMovement();
                if (direction.getAxis() == Direction.Axis.X) {
                    this.setDeltaMovement(0.1D * (double) direction.getStepX(), velocity.y, velocity.z);
                }
                else if (direction.getAxis() == Direction.Axis.Z) {
                    this.setDeltaMovement(velocity.x, velocity.y, 0.1D * (double) direction.getStepZ());
                }
            }
        }
    }
}
