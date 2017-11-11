package net.darkhax.cravings.handler;

import net.darkhax.bookshelf.util.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CravingDataHandler {

    @CapabilityInject(ICustomData.class)
    public static final Capability<ICustomData> CUSTOM_DATA = null;

    public static void init () {

        CapabilityManager.INSTANCE.register(ICustomData.class, new Storage(), Default.class);
        MinecraftForge.EVENT_BUS.register(new CravingDataHandler());
    }

    @SubscribeEvent
    public void attachCapabilities (AttachCapabilitiesEvent<Entity> event) {

        if (PlayerUtils.isPlayerReal(event.getObject())) {

            event.addCapability(new ResourceLocation("cravings", "craving_info"), new Provider());
        }
    }

    /**
     * Gets the data from an entity.
     *
     * @param entity The entity to pull data from.
     * @return The data.
     */
    public static ICustomData getData (Entity entity) {

        return entity.getCapability(CUSTOM_DATA, EnumFacing.DOWN);
    }

    /**
     * Interface for holding various getter and setter methods.
     */
    public static interface ICustomData {

        ItemStack getCravedItem ();

        void setCravedItem (ItemStack craved);

        int getTimeToSatisfy ();

        void setTimeToSatisfy (int time);

        int getTimeToNextAttempt ();

        void setTimeToNextAttempt (int time);
    }

    /**
     * Default implementation of the custom data.
     */
    public static class Default implements ICustomData {

        private ItemStack cravedItem;

        private int timeToSatisfy;

        private int timeToNextAttempt;

        @Override
        public ItemStack getCravedItem () {

            return this.cravedItem;
        }

        @Override
        public void setCravedItem (ItemStack craved) {

            this.cravedItem = craved;
        }

        @Override
        public int getTimeToSatisfy () {

            return this.timeToSatisfy;
        }

        @Override
        public void setTimeToSatisfy (int time) {

            this.timeToSatisfy = time;
        }

        @Override
        public int getTimeToNextAttempt () {

            return this.timeToNextAttempt;
        }

        @Override
        public void setTimeToNextAttempt (int time) {

            this.timeToNextAttempt = time;
        }
    }

    /**
     * Handles reand/write of custom data.
     */
    public static class Storage implements Capability.IStorage<ICustomData> {

        @Override
        public NBTBase writeNBT (Capability<ICustomData> capability, ICustomData instance, EnumFacing side) {

            final NBTTagCompound tag = new NBTTagCompound();

            tag.setTag("CravedItem", instance.getCravedItem().writeToNBT(new NBTTagCompound()));
            tag.setInteger("TimeToSatisfy", instance.getTimeToSatisfy());
            tag.setInteger("TimeToNextAttempt", instance.getTimeToNextAttempt());

            return tag;
        }

        @Override
        public void readNBT (Capability<ICustomData> capability, ICustomData instance, EnumFacing side, NBTBase nbt) {

            final NBTTagCompound tag = (NBTTagCompound) nbt;
            instance.setCravedItem(new ItemStack(tag.getCompoundTag("CravedItem")));
            instance.setTimeToSatisfy(tag.getInteger("TimeToSatisfy"));
            instance.setTimeToNextAttempt(tag.getInteger("TimeToNextAttempt"));
        }
    }

    /**
     * Handles all the checks and delegate methods for the capability.
     */
    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

        ICustomData instance = CUSTOM_DATA.getDefaultInstance();

        @Override
        public boolean hasCapability (Capability<?> capability, EnumFacing facing) {

            return capability == CUSTOM_DATA;
        }

        @Override
        public <T> T getCapability (Capability<T> capability, EnumFacing facing) {

            return this.hasCapability(capability, facing) ? CUSTOM_DATA.<T> cast(this.instance) : null;
        }

        @Override
        public NBTTagCompound serializeNBT () {

            return (NBTTagCompound) CUSTOM_DATA.getStorage().writeNBT(CUSTOM_DATA, this.instance, null);
        }

        @Override
        public void deserializeNBT (NBTTagCompound nbt) {

            CUSTOM_DATA.getStorage().readNBT(CUSTOM_DATA, this.instance, null, nbt);
        }
    }
}