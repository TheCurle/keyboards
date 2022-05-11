package uk.gemwire.keyboards;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.RandomStringUtils;

import javax.annotation.Nullable;

/**
 * A simple rotateable block that messes with your chat if a cat sits on it.
 *
 * @author Curle
 */
public class Keyboard extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    protected static final VoxelShape EAST_AABB = Shapes.join(Shapes.empty(), Shapes.box(0.5625, 0, 0, 1, 0.1875, 1), BooleanOp.OR);
    protected static final VoxelShape WEST_AABB = Shapes.join(Shapes.empty(), Shapes.box(0, 0, 0, 0.4375, 0.1875, 1), BooleanOp.OR);
    protected static final VoxelShape SOUTH_AABB = Shapes.join(Shapes.empty(), Shapes.box(0, 0, 0.5625, 1, 0.1875, 1), BooleanOp.OR);
    protected static final VoxelShape NORTH_AABB = Shapes.join(Shapes.empty(), Shapes.box(0, 0, 0, 1, 0.1875, 0.4375), BooleanOp.OR);

    public Keyboard() {
        super(Properties.of(Material.STONE).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
        return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH_AABB;
            case WEST -> WEST_AABB;
            case EAST -> EAST_AABB;
            default -> NORTH_AABB;
        };
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        // This is meant to only fire on the server, but we have to be certain
        if(level.isClientSide()) return;

        ServerPlayer player;

        // If there's a cat above us
        if ((entity instanceof Cat cat)) {
            if (!(pos.equals(new BlockPos(cat.position())))) return;
            // With a Player owner
            LivingEntity owner = cat.getOwner();
            if (!(owner instanceof ServerPlayer p)) return; // i wish you could assign to an existing variable with pattern instanceof
            player = p;
            // Or a player above us
        } else if ((entity instanceof ServerPlayer p)) {
            player = p;
            // Otherwise, stop
        } else {
            return;
        }

        // And the user is particularly unlucky
        if (level.getRandom().nextInt(10000) > 2) return;

        // Generate some text
        String message = RandomStringUtils.randomAlphanumeric(level.random.nextInt(200));

        // Send the message, with a fake packet
        player.connection.handleChat(new ServerboundChatPacket(message));
    }
}
