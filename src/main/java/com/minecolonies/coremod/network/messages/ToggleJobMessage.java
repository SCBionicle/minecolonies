package com.minecolonies.coremod.network.messages;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.IColonyView;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.network.IMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Message class which manages the message to toggle automatic or manual job allocation.
 */
public class ToggleJobMessage implements IMessage
{
    /**
     * The Colony ID.
     */
    private int     colonyId;
    /**
     * Toggle the job allocation to true or false.
     */
    private boolean toggle;

    /**
     * The dimension of the 
     */
    private int dimension;

    /**
     * Empty public constructor.
     */
    public ToggleJobMessage()
    {
        super();
    }

    /**
     * Creates object for the player to turn manual allocation or or off.
     *
     * @param colony view of the colony to read data from.
     * @param toggle toggle the job to manually or automatically.
     */
    public ToggleJobMessage(@NotNull final IColonyView colony, final boolean toggle)
    {
        super();
        this.colonyId = colony.getID();
        this.toggle = toggle;
        this.dimension = colony.getDimension();
    }

    /**
     * Transformation from a byteStream.
     *
     * @param buf the used byteBuffer.
     */
    @Override
    public void fromBytes(@NotNull final PacketBuffer buf)
    {
        colonyId = buf.readInt();
        toggle = buf.readBoolean();
        dimension = buf.readInt();
    }

    /**
     * Transformation to a byteStream.
     *
     * @param buf the used byteBuffer.
     */
    @Override
    public void toBytes(@NotNull final PacketBuffer buf)
    {
        buf.writeInt(colonyId);
        buf.writeBoolean(toggle);
        buf.writeInt(dimension);
    }

    @Nullable
    @Override
    public LogicalSide getExecutionSide()
    {
        return LogicalSide.SERVER;
    }

    @Override
    public void onExecute(final NetworkEvent.Context ctxIn, final boolean isLogicalServer)
    {
        final IColony colony = IColonyManager.getInstance().getColonyByDimension(colonyId, dimension);
        if (colony != null)
        {
            final PlayerEntity player = ctxIn.getSender();

            //Verify player has permission to change this huts settings
            if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS))
            {
                return;
            }

            colony.setManualHiring(toggle);
        }
    }
}
