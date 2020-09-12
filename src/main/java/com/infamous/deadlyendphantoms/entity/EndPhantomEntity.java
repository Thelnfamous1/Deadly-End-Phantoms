package com.infamous.deadlyendphantoms.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.BodyController;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class EndPhantomEntity extends FlyingEntity implements IMob {
    private static final DataParameter<Integer> SIZE = EntityDataManager.createKey(EndPhantomEntity.class, DataSerializers.VARINT);
    private Vector3d orbitOffset = Vector3d.ZERO;
    private BlockPos orbitPosition = BlockPos.ZERO;
    private EndPhantomEntity.AttackPhase attackPhase = EndPhantomEntity.AttackPhase.CIRCLE;

    public EndPhantomEntity(EntityType<? extends EndPhantomEntity> type, World worldIn) {
        super(type, worldIn);
        this.experienceValue = 5;
        this.moveController = new EndPhantomEntity.MoveHelperController(this);
        this.lookController = new EndPhantomEntity.LookHelperController(this);
    }

    public EndPhantomEntity(World worldIn){
        super(ModEntityTypes.END_PHANTOM.get(), worldIn);
        this.experienceValue = 5;
        this.moveController = new EndPhantomEntity.MoveHelperController(this);
        this.lookController = new EndPhantomEntity.LookHelperController(this);
    }

    // MobEntity
    public static AttributeModifierMap.MutableAttribute func_233666_p_() {
        return LivingEntity.func_233639_cI_().func_233815_a_(Attributes.field_233819_b_, 16.0D).func_233814_a_(Attributes.field_233824_g_);
    }

    // LivingEntity
    public static AttributeModifierMap.MutableAttribute func_233639_cI_() {
        return AttributeModifierMap.func_233803_a_().func_233814_a_(Attributes.field_233818_a_).func_233814_a_(Attributes.field_233820_c_).func_233814_a_(Attributes.field_233821_d_).func_233814_a_(Attributes.field_233826_i_).func_233814_a_(Attributes.field_233827_j_).func_233814_a_(net.minecraftforge.common.ForgeMod.SWIM_SPEED.get()).func_233814_a_(net.minecraftforge.common.ForgeMod.NAMETAG_DISTANCE.get()).func_233814_a_(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
    }

    public static AttributeModifierMap.MutableAttribute setCustomAttributes() {
        return MobEntity.func_233666_p_()
                // movement speed
                //.func_233815_a_(Attributes.field_233821_d_, (double)0.5F)
                // max_health
                .func_233815_a_(Attributes.field_233818_a_, 20.0D)
                // attack damage
                .func_233815_a_(Attributes.field_233823_f_, 6.0D);
    }

    protected BodyController createBodyController() {
        return new EndPhantomEntity.BodyHelperController(this);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new EndPhantomEntity.PickAttackGoal());
        this.goalSelector.addGoal(2, new PursueElytraFlyerGoal());
        this.goalSelector.addGoal(2, new EndPhantomEntity.SweepAttackGoal());
        this.goalSelector.addGoal(3, new EndPhantomEntity.OrbitPointGoal());
        this.targetSelector.addGoal(1, new EndPhantomEntity.AttackPlayerGoal());
    }

    protected void registerData() {
        super.registerData();
        this.dataManager.register(SIZE, 0);
    }

    public void setPhantomSize(int sizeIn) {
        this.dataManager.set(SIZE, MathHelper.clamp(sizeIn, 0, 64));
    }

    private void updatePhantomSize() {
        this.recalculateSize();
        this.getAttribute(Attributes.field_233823_f_).setBaseValue((double)(6 + this.getPhantomSize()));
    }

    public int getPhantomSize() {
        return this.dataManager.get(SIZE);
    }

    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * 0.35F;
    }

    public void notifyDataManagerChange(DataParameter<?> key) {
        if (SIZE.equals(key)) {
            this.updatePhantomSize();
        }

        super.notifyDataManagerChange(key);
    }

    protected boolean isDespawnPeaceful() {
        return true;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void tick() {
        super.tick();
        if (this.world.isRemote) {
            float f = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksExisted) * 0.13F + (float)Math.PI);
            float f1 = MathHelper.cos((float)(this.getEntityId() * 3 + this.ticksExisted + 1) * 0.13F + (float)Math.PI);
            if (f > 0.0F && f1 <= 0.0F) {
                this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PHANTOM_FLAP, this.getSoundCategory(), 0.95F + this.rand.nextFloat() * 0.05F, 0.95F + this.rand.nextFloat() * 0.05F, false);
            }

            int i = this.getPhantomSize();
            float f2 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
            float f3 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
            float f4 = (0.3F + f * 0.45F) * ((float)i * 0.2F + 1.0F);
            this.world.addParticle(ParticleTypes.MYCELIUM, this.getPosX() + (double)f2, this.getPosY() + (double)f4, this.getPosZ() + (double)f3, 0.0D, 0.0D, 0.0D);
            this.world.addParticle(ParticleTypes.MYCELIUM, this.getPosX() - (double)f2, this.getPosY() + (double)f4, this.getPosZ() - (double)f3, 0.0D, 0.0D, 0.0D);
        }

    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    public void livingTick() {
        if (this.isAlive() && this.isInDaylight()) {
            this.setFire(8);
        }

        super.livingTick();
    }

    protected void updateAITasks() {
        super.updateAITasks();
    }

    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        // func_233580_cy_ = getBlockPos()
        this.orbitPosition = this.func_233580_cy_().up(5);
        this.setPhantomSize(0);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("AX")) {
            this.orbitPosition = new BlockPos(compound.getInt("AX"), compound.getInt("AY"), compound.getInt("AZ"));
        }

        this.setPhantomSize(compound.getInt("Size"));
    }

    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("AX", this.orbitPosition.getX());
        compound.putInt("AY", this.orbitPosition.getY());
        compound.putInt("AZ", this.orbitPosition.getZ());
        compound.putInt("Size", this.getPhantomSize());
    }

    /**
     * Checks if the entity is in range to render.
     */
    @OnlyIn(Dist.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }

    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PHANTOM_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_PHANTOM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PHANTOM_DEATH;
    }

    public CreatureAttribute getCreatureAttribute() {
        return CreatureAttribute.UNDEFINED;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume() {
        return 1.0F;
    }

    public boolean canAttack(EntityType<?> typeIn) {
        return true;
    }

    public EntitySize getSize(Pose poseIn) {
        int i = this.getPhantomSize();
        EntitySize entitysize = super.getSize(poseIn);
        float f = (entitysize.width + 0.2F * (float)i) / entitysize.width;
        return entitysize.scale(f);
    }

    static enum AttackPhase {
        CIRCLE,
        SWOOP,
        PURSUE;
    }

    class AttackPlayerGoal extends Goal {
        private final EntityPredicate entityPredicate = (new EntityPredicate()).setDistance(64.0D);
        private int tickDelay = 20;

        private AttackPlayerGoal() {
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            if (this.tickDelay > 0) {
                --this.tickDelay;
                return false;
            } else {
                this.tickDelay = 60;
                List<PlayerEntity> list = EndPhantomEntity.this.world.getTargettablePlayersWithinAABB(this.entityPredicate, EndPhantomEntity.this, EndPhantomEntity.this.getBoundingBox().grow(16.0D, 64.0D, 16.0D));
                if (!list.isEmpty()) {
                    list.sort(
                            Comparator.comparing(Entity::getPosY).reversed()
                    );

                    for(PlayerEntity playerentity : list) {
                        if (EndPhantomEntity.this.canAttack(playerentity, EntityPredicate.DEFAULT)) {
                            EndPhantomEntity.this.setAttackTarget(playerentity);
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            LivingEntity livingentity = EndPhantomEntity.this.getAttackTarget();
            return livingentity != null && EndPhantomEntity.this.canAttack(livingentity, EntityPredicate.DEFAULT);
        }
    }

    class BodyHelperController extends BodyController {
        public BodyHelperController(MobEntity mob) {
            super(mob);
        }

        /**
         * Update the Head and Body rendenring angles
         */
        public void updateRenderAngles() {
            EndPhantomEntity.this.rotationYawHead = EndPhantomEntity.this.renderYawOffset;
            EndPhantomEntity.this.renderYawOffset = EndPhantomEntity.this.rotationYaw;
        }
    }

    class LookHelperController extends LookController {
        public LookHelperController(MobEntity entityIn) {
            super(entityIn);
        }

        /**
         * Updates look
         */
        public void tick() {
        }
    }

    abstract class MoveGoal extends Goal {
        public MoveGoal() {
            this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean withinOrbitOffset() {
            return EndPhantomEntity.this.orbitOffset.squareDistanceTo(EndPhantomEntity.this.getPosX(), EndPhantomEntity.this.getPosY(), EndPhantomEntity.this.getPosZ()) < 4.0D;
        }
    }

    class MoveHelperController extends MovementController {
        private float speedFactor = 0.1F;

        public MoveHelperController(MobEntity entityIn) {
            super(entityIn);
        }

        public void tick() {
            if (EndPhantomEntity.this.collidedHorizontally) {
                EndPhantomEntity.this.rotationYaw += 180.0F;
                this.speedFactor = 0.1F;
            }

            float f = (float)(EndPhantomEntity.this.orbitOffset.x - EndPhantomEntity.this.getPosX());
            float f1 = (float)(EndPhantomEntity.this.orbitOffset.y - EndPhantomEntity.this.getPosY());
            float f2 = (float)(EndPhantomEntity.this.orbitOffset.z - EndPhantomEntity.this.getPosZ());
            double d0 = (double)MathHelper.sqrt(f * f + f2 * f2);
            double d1 = 1.0D - (double)MathHelper.abs(f1 * 0.7F) / d0;
            f = (float)((double)f * d1);
            f2 = (float)((double)f2 * d1);
            d0 = (double)MathHelper.sqrt(f * f + f2 * f2);
            double d2 = (double)MathHelper.sqrt(f * f + f2 * f2 + f1 * f1);
            float f3 = EndPhantomEntity.this.rotationYaw;
            float f4 = (float)MathHelper.atan2((double)f2, (double)f);
            float f5 = MathHelper.wrapDegrees(EndPhantomEntity.this.rotationYaw + 90.0F);
            float f6 = MathHelper.wrapDegrees(f4 * (180F / (float)Math.PI));
            EndPhantomEntity.this.rotationYaw = MathHelper.approachDegrees(f5, f6, 4.0F) - 90.0F;
            EndPhantomEntity.this.renderYawOffset = EndPhantomEntity.this.rotationYaw;
            if (MathHelper.degreesDifferenceAbs(f3, EndPhantomEntity.this.rotationYaw) < 3.0F) {
                this.speedFactor = MathHelper.approach(this.speedFactor, 1.8F, 0.005F * (1.8F / this.speedFactor));
            } else {
                this.speedFactor = MathHelper.approach(this.speedFactor, 0.2F, 0.025F);
            }

            float f7 = (float)(-(MathHelper.atan2((double)(-f1), d0) * (double)(180F / (float)Math.PI)));
            EndPhantomEntity.this.rotationPitch = f7;
            float f8 = EndPhantomEntity.this.rotationYaw + 90.0F;
            double d3 = (double)(this.speedFactor * MathHelper.cos(f8 * ((float)Math.PI / 180F))) * Math.abs((double)f / d2);
            double d4 = (double)(this.speedFactor * MathHelper.sin(f8 * ((float)Math.PI / 180F))) * Math.abs((double)f2 / d2);
            double d5 = (double)(this.speedFactor * MathHelper.sin(f7 * ((float)Math.PI / 180F))) * Math.abs((double)f1 / d2);
            Vector3d vector3d = EndPhantomEntity.this.getMotion();
            EndPhantomEntity.this.setMotion(vector3d.add((new Vector3d(d3, d5, d4)).subtract(vector3d).scale(0.2D)));
        }
    }

    class OrbitPointGoal extends EndPhantomEntity.MoveGoal {
        private float field_203150_c;
        private float field_203151_d;
        private float field_203152_e;
        private float field_203153_f;

        private OrbitPointGoal() {
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return EndPhantomEntity.this.getAttackTarget() == null || EndPhantomEntity.this.attackPhase == EndPhantomEntity.AttackPhase.CIRCLE;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.field_203151_d = 5.0F + EndPhantomEntity.this.rand.nextFloat() * 10.0F;
            this.field_203152_e = -4.0F + EndPhantomEntity.this.rand.nextFloat() * 9.0F;
            this.field_203153_f = EndPhantomEntity.this.rand.nextBoolean() ? 1.0F : -1.0F;
            this.updateOrbitPositionAndOffset();
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            if (EndPhantomEntity.this.rand.nextInt(350) == 0) {
                this.field_203152_e = -4.0F + EndPhantomEntity.this.rand.nextFloat() * 9.0F;
            }

            if (EndPhantomEntity.this.rand.nextInt(250) == 0) {
                ++this.field_203151_d;
                if (this.field_203151_d > 15.0F) {
                    this.field_203151_d = 5.0F;
                    this.field_203153_f = -this.field_203153_f;
                }
            }

            if (EndPhantomEntity.this.rand.nextInt(450) == 0) {
                this.field_203150_c = EndPhantomEntity.this.rand.nextFloat() * 2.0F * (float)Math.PI;
                this.updateOrbitPositionAndOffset();
            }

            if (this.withinOrbitOffset()) {
                this.updateOrbitPositionAndOffset();
            }

            if (EndPhantomEntity.this.orbitOffset.y < EndPhantomEntity.this.getPosY() && !EndPhantomEntity.this.world.isAirBlock(EndPhantomEntity.this.func_233580_cy_().down(1))) {
                this.field_203152_e = Math.max(1.0F, this.field_203152_e);
                this.updateOrbitPositionAndOffset();
            }

            if (EndPhantomEntity.this.orbitOffset.y > EndPhantomEntity.this.getPosY() && !EndPhantomEntity.this.world.isAirBlock(EndPhantomEntity.this.func_233580_cy_().up(1))) {
                this.field_203152_e = Math.min(-1.0F, this.field_203152_e);
                this.updateOrbitPositionAndOffset();
            }

        }

        private void updateOrbitPositionAndOffset() {
            if (BlockPos.ZERO.equals(EndPhantomEntity.this.orbitPosition)) {
                EndPhantomEntity.this.orbitPosition = EndPhantomEntity.this.func_233580_cy_();
            }

            this.field_203150_c += this.field_203153_f * 15.0F * ((float)Math.PI / 180F);
            EndPhantomEntity.this.orbitOffset = Vector3d.func_237491_b_(EndPhantomEntity.this.orbitPosition).add((double)(this.field_203151_d * MathHelper.cos(this.field_203150_c)), (double)(-4.0F + this.field_203152_e), (double)(this.field_203151_d * MathHelper.sin(this.field_203150_c)));
        }
    }

    class PickAttackGoal extends Goal {
        private int tickDelay;

        private PickAttackGoal() {
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            LivingEntity livingentity = EndPhantomEntity.this.getAttackTarget();
            return livingentity != null && EndPhantomEntity.this.canAttack(EndPhantomEntity.this.getAttackTarget(), EntityPredicate.DEFAULT);
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.tickDelay = 10;
            EndPhantomEntity.this.attackPhase = EndPhantomEntity.AttackPhase.CIRCLE;
            this.orbitTarget();
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            EndPhantomEntity.this.orbitPosition = EndPhantomEntity.this.world.getHeight(Heightmap.Type.MOTION_BLOCKING, EndPhantomEntity.this.orbitPosition).up(10 + EndPhantomEntity.this.rand.nextInt(20));
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            if (EndPhantomEntity.this.attackPhase == EndPhantomEntity.AttackPhase.CIRCLE) {
                --this.tickDelay;
                if (this.tickDelay <= 0) {
                    if(EndPhantomEntity.this.getAttackTarget() != null && EndPhantomEntity.this.getAttackTarget().isElytraFlying()){
                        EndPhantomEntity.this.attackPhase = EndPhantomEntity.AttackPhase.PURSUE;
                        this.orbitTarget();
                        this.tickDelay = (8 + EndPhantomEntity.this.rand.nextInt(4)) * 20;
                        EndPhantomEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0F, 0.95F + EndPhantomEntity.this.rand.nextFloat() * 0.1F);
                    }
                    else{
                        EndPhantomEntity.this.attackPhase = EndPhantomEntity.AttackPhase.SWOOP;
                        this.orbitTarget();
                        this.tickDelay = (8 + EndPhantomEntity.this.rand.nextInt(4)) * 20;
                        EndPhantomEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0F, 0.95F + EndPhantomEntity.this.rand.nextFloat() * 0.1F);
                    }
                }
            }

        }

        private void orbitTarget() {
            // func_233580_cy_ = getBlockPos()
            EndPhantomEntity.this.orbitPosition = EndPhantomEntity.this.getAttackTarget().func_233580_cy_().up(20 + EndPhantomEntity.this.rand.nextInt(20));
            if (EndPhantomEntity.this.orbitPosition.getY() < EndPhantomEntity.this.world.getSeaLevel()) {
                EndPhantomEntity.this.orbitPosition = new BlockPos(EndPhantomEntity.this.orbitPosition.getX(), EndPhantomEntity.this.world.getSeaLevel() + 1, EndPhantomEntity.this.orbitPosition.getZ());
            }

        }
    }

    class SweepAttackGoal extends EndPhantomEntity.MoveGoal {
        private SweepAttackGoal() {
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return EndPhantomEntity.this.getAttackTarget() != null && EndPhantomEntity.this.attackPhase == EndPhantomEntity.AttackPhase.SWOOP;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            LivingEntity livingentity = EndPhantomEntity.this.getAttackTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else if (!(livingentity instanceof PlayerEntity) || !((PlayerEntity)livingentity).isSpectator() && !((PlayerEntity)livingentity).isCreative()) {
                if (!this.shouldExecute()) {
                    return false;
                } else {
                    if (EndPhantomEntity.this.ticksExisted % 20 == 0) {
                        List<CatEntity> list = EndPhantomEntity.this.world.getEntitiesWithinAABB(CatEntity.class, EndPhantomEntity.this.getBoundingBox().grow(16.0D), EntityPredicates.IS_ALIVE);
                        if (!list.isEmpty()) {
                            for(CatEntity catentity : list) {
                                catentity.func_213420_ej();
                            }

                            return false;
                        }
                    }

                    return true;
                }
            } else {
                return false;
            }
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            EndPhantomEntity.this.setAttackTarget((LivingEntity)null);
            EndPhantomEntity.this.attackPhase = EndPhantomEntity.AttackPhase.CIRCLE;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            LivingEntity livingentity = EndPhantomEntity.this.getAttackTarget();
            EndPhantomEntity.this.orbitOffset = new Vector3d(livingentity.getPosX(), livingentity.getPosYHeight(0.5D), livingentity.getPosZ());
            if (EndPhantomEntity.this.getBoundingBox().grow((double)0.2F).intersects(livingentity.getBoundingBox())) {
                EndPhantomEntity.this.attackEntityAsMob(livingentity);
                EndPhantomEntity.this.attackPhase = EndPhantomEntity.AttackPhase.CIRCLE;
                if (!EndPhantomEntity.this.isSilent()) {
                    EndPhantomEntity.this.world.playEvent(1039, EndPhantomEntity.this.func_233580_cy_(), 0);
                }
            } else if (EndPhantomEntity.this.collidedHorizontally || EndPhantomEntity.this.hurtTime > 0) {
                EndPhantomEntity.this.attackPhase = EndPhantomEntity.AttackPhase.CIRCLE;
            }

        }
    }

    class PursueElytraFlyerGoal extends EndPhantomEntity.MoveGoal {
        private PursueElytraFlyerGoal() {
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean shouldExecute() {
            return EndPhantomEntity.this.getAttackTarget() != null
                    && EndPhantomEntity.this.attackPhase == AttackPhase.PURSUE
                    && EndPhantomEntity.this.getAttackTarget().isElytraFlying();
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            LivingEntity livingentity = EndPhantomEntity.this.getAttackTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else if (!livingentity.isElytraFlying()) {
                return false;
            } else if (!(livingentity instanceof PlayerEntity) || !((PlayerEntity)livingentity).isSpectator() && !((PlayerEntity)livingentity).isCreative()) {
                if (!this.shouldExecute()) {
                    return false;
                } else {
                    if (EndPhantomEntity.this.ticksExisted % 20 == 0) {
                        List<CatEntity> list = EndPhantomEntity.this.world.getEntitiesWithinAABB(CatEntity.class, EndPhantomEntity.this.getBoundingBox().grow(16.0D), EntityPredicates.IS_ALIVE);
                        if (!list.isEmpty()) {
                            for(CatEntity catentity : list) {
                                catentity.func_213420_ej();
                            }

                            return false;
                        }
                    }

                    return true;
                }
            } else {
                return false;
            }
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void resetTask() {
            EndPhantomEntity.this.setAttackTarget((LivingEntity)null);
            EndPhantomEntity.this.attackPhase = EndPhantomEntity.AttackPhase.CIRCLE;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            LivingEntity elytraFlyer = EndPhantomEntity.this.getAttackTarget();
            EndPhantomEntity.this.orbitOffset = new Vector3d(elytraFlyer.getPosX(), elytraFlyer.getPosYHeight(0.5D), elytraFlyer.getPosZ());

            // Attack on collide
            if (EndPhantomEntity.this.getBoundingBox().grow((double)0.2F).intersects(elytraFlyer.getBoundingBox())) {
                EndPhantomEntity.this.attackEntityAsMob(elytraFlyer);
                //EndPhantomEntity.this.attackPhase = EndPhantomEntity.AttackPhase.CIRCLE;
                if (!EndPhantomEntity.this.isSilent()) {
                    EndPhantomEntity.this.world.playEvent(1039, EndPhantomEntity.this.func_233580_cy_(), 0);
                }
            }
            // If a wall was hit, go back to circling
            else if (EndPhantomEntity.this.collidedHorizontally || EndPhantomEntity.this.hurtTime > 0) {
                EndPhantomEntity.this.attackPhase = EndPhantomEntity.AttackPhase.CIRCLE;
            }

        }
    }
}
