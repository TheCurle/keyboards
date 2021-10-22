package uk.gemwire.keyboards;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Cat;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = Mod.MODID, bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.FORGE)
public class KeyboardEvents {

    @SubscribeEvent
    public static void tick(LivingEvent.LivingUpdateEvent event) {
        // Entity is a cat
        if(!(event.getEntity() instanceof Cat cat)) return;
        // Server side
        if(event.getEntity().level.isClientSide()) return;

        /**  Untameable black cat appears on keyboard at full moon nights */

        // Cat is on keyboard
        if(event.getEntity().level.getBlockState(new BlockPos(event.getEntity().position())).getBlock() instanceof Keyboard) {
            /**  Cats turn black at night  */

        } else {
            /** Cats turn to their original color when they move off a keyboard */

        }
    }
}
