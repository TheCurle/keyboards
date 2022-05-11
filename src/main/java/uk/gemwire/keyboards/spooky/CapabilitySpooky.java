package uk.gemwire.keyboards.spooky;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilitySpooky {

    public static final Capability<SpookyData> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public interface SpookyData {
        boolean isSpooky();
        void setSpooky(boolean value);
        int getType();
        void setType(int value);
    }

    public static class SpookyImplementation implements SpookyData {
        boolean spooky;
        int type;
        public boolean isSpooky() { return spooky; }
        public void setSpooky(boolean value) { spooky = value; }
        public int getType() { return type; }
        public void setType(int value) { type = value; }
    }

}