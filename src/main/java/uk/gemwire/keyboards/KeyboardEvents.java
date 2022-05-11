package uk.gemwire.keyboards;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.gemwire.keyboards.spooky.CapabilitySpooky;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = Mod.MODID, bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE)
public class KeyboardEvents {

    @SubscribeEvent
    public static void entity(LivingEvent.LivingUpdateEvent event) {
        // Entity is a cat
        if (!(event.getEntity() instanceof Cat cat)) return;
        // Server side
        if (event.getEntity().level.isClientSide()) return;

        /**  Untameable black cat appears on keyboard at full moon nights */

        // Cat is on keyboard
        if (event.getEntity().level.getBlockState(new BlockPos(event.getEntity().position())).getBlock() instanceof Keyboard) {
            /**  Cats turn black at night  */
            if (cat.getCatType() != Cat.TYPE_BLACK && (cat.level.getDayTime() > 17000 && cat.level.getDayTime() < 19000)) {
                cat.hiss();
                cat.getCapability(CapabilitySpooky.CAPABILITY).ifPresent(data -> { if (!data.isSpooky()) { data.setSpooky(true); data.setType(cat.getCatType()); } });
                cat.setCatType(Cat.TYPE_BLACK);
            }

        } else {

            /** Cats turn to their original color when they move off a keyboard */
            cat.getCapability(CapabilitySpooky.CAPABILITY).ifPresent(data -> {
                if (data.isSpooky()) {
                    cat.hiss();
                    cat.setCatType(data.getType());
                    data.setSpooky(false);
                }
            });
        }
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(CapabilitySpooky.SpookyData.class);
    }

    @SubscribeEvent
    public static void capability(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Cat)) return;
        var spookyData = new CapabilitySpooky.SpookyImplementation();
        var spookyOptional = LazyOptional.of(() -> spookyData);

        var spookyProvider = new ICapabilitySerializable<IntTag>() {
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == CapabilitySpooky.CAPABILITY) {
                    return spookyOptional.cast();
                }
                return LazyOptional.empty();
            }

            public IntTag serializeNBT() {
                return IntTag.valueOf(spookyData.isSpooky() ? 1 : 0);
            }

            public void deserializeNBT(IntTag nbt) {
                spookyData.setSpooky(nbt.getAsInt() == 1);
            }
        };

        event.addCapability(new ResourceLocation("keyboards", "spookycats"), spookyProvider);
    }
}
